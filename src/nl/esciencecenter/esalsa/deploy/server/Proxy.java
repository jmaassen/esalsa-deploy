package nl.esciencecenter.esalsa.deploy.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.esciencecenter.esalsa.deploy.ConfigurationTemplate;
import nl.esciencecenter.esalsa.deploy.ExperimentTemplate;
import nl.esciencecenter.esalsa.deploy.FileSet;
import nl.esciencecenter.esalsa.deploy.POPRunnerInterface;
import nl.esciencecenter.esalsa.deploy.WorkerDescription;

public class Proxy implements Runnable, Protocol {

	private static Logger globalLogger = LoggerFactory.getLogger("eSalsa");

	private final Socket socket;
	private final ObjectInputStream in;
	private final ObjectOutputStream out;
	
	private final POPRunnerInterface runner;
	
	private final Thread myThread;
	
	public Proxy(Socket socket, POPRunnerInterface runner) throws IOException { 
		this.socket = socket;
		this.runner = runner;
		
		out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		out.flush();
		
		in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
		
		myThread = new Thread(this, "POPRunner proxy");
		myThread.start();
	}

	private void close() { 
	
		try { 
			in.close();
		} catch (Exception e) {
			// IGNORED
		}
		
		try { 
			out.close();
		} catch (Exception e) {
			// IGNORED
		}
		
		try {
			socket.close();
		} catch (Exception e) {
			// IGNORED
		}
	}
		
	private void add(int type, Object param) throws Exception { 		
		switch (type) { 
		case WORKER:
			runner.addWorkerDescription((WorkerDescription) param);
			break;
		case INPUTS:
			runner.addInputFileSet((FileSet) param);
			break;
		case CONFIG:
			runner.addConfigurationTemplate((ConfigurationTemplate) param);
			break;
		case EXPERIMENT:
			runner.addExperimentDescription((ExperimentTemplate) param);
			break;
		default:
			throw new Exception("Illegal add of type " + type);
		}		
	} 
	
	private Object get(int type, String ID) throws Exception { 		
		switch (type) { 
		case WORKER:
			return runner.getWorkerDescription(ID);
		case INPUTS:
			return runner.getInputFileSet(ID);
		case CONFIG:
			return runner.getConfigurationTemplate(ID);
		case EXPERIMENT:
			return runner.getExperimentDescription(ID);
		case WAITING:
			return runner.getWaitingExperiment(ID);		
		case RUNNING:
			return runner.getRunningExperiment(ID);
		case COMPLETED:
			return runner.getStoppedExperiment(ID);		
		default:
			throw new Exception("Illegal get of type " + type);
		} 
	} 

	private void remove(int type, String ID) throws Exception { 		
		switch (type) { 
		case WORKER:
			runner.removeWorkerDescription(ID);
			break;
		case INPUTS:
			runner.removeInputFileSet(ID);
			break;
		case CONFIG:
			runner.removeConfigurationTemplate(ID);
			break;
		case EXPERIMENT:
			runner.removeExperimentDescription(ID);
			break;
		case WAITING:
		case RUNNING:
		case COMPLETED:
		default:
			throw new Exception("Illegal remove of type " + type);
		} 
	} 

	private Object list(int type) throws Exception { 		
		switch (type) { 
		case WORKER:
			return runner.listWorkerDescriptions();
		case INPUTS:
			return runner.listInputFileSets();
		case CONFIG:
			return runner.listConfigurationTemplates();
		case EXPERIMENT:
			return runner.listExperimentDescriptions();
		case WAITING:
			return runner.listWaitingExperiments();
		case RUNNING:
			return runner.listRunningExperiments();
		case COMPLETED:
			return runner.listStoppedExperiments();		
		default:
			throw new Exception("Illegal get of type " + type);
		} 
	} 

	private Object rpc(int opcode, int type, Object param) throws Exception { 		
		
		switch (opcode) { 
		case ADD:
			add(type, param);
			return null;
		case GET:
			return get(type, (String) param);
		case REMOVE:
			remove(type, (String) param);
			return null;
		case LIST:
			return list(type);					
		case CREATE:
			return runner.createExperiment((String) param);
		case START:
			runner.startExperiment((String) param);
			return null;
		case STOP:
			runner.stopRunningExperiment((String) param);
			return null;
		default: 
			throw new Exception("Unknown opcode! (" + opcode + ")");
		}
	}
	
	@Override
	public void run() {

		boolean done = false;
		
		while (!done) { 
			
			int opcode = -1;
			int type = -1;
			Object param = null;
			
			try { 
				opcode = in.readByte();
				type = in.readByte();
				param = in.readObject();
			} catch (Exception e) {
				globalLogger.error("Failed to read opcode from " + socket.getInetAddress());
				done = true;
			}

			if (!done && opcode >= 0) { 

				System.err.println("GOT RPC " + opcode + " " + type + " " + param);
				
				int status = -1;
				Object result = null;
				
				if (opcode == EXIT) { 
					done = true;
					status = 0;
				} else { 
					try { 
						result = rpc(opcode, type, param);
						status = 0;
					} catch (Exception e) { 
						result = e;
						status = 1;
					} 
				}

				try { 
					out.writeInt(status);
					out.writeObject(result);
					out.flush();
				} catch (Exception e) {
					done = true;
					System.out.println("WARNING: Lost connection to " + socket.getRemoteSocketAddress());					
				}
			} 
		}
		
		close();		
	}
}
