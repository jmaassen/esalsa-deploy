package nl.esciencecenter.esalsa.deploy.ui.cli;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.esciencecenter.esalsa.deploy.ConfigurationTemplate;
import nl.esciencecenter.esalsa.deploy.ExperimentDescription;
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

	@Override
	public void run() {

		boolean done = false;
		
		while (!done) { 
			
			int opcode = -1;
			
			try { 
				opcode = in.readInt();
			} catch (Exception e) {
				globalLogger.error("Failed to read opcode from " + socket.getInetAddress());
				done = true;
			}
			
			if (!done && opcode >= 0) { 

				try { 

					int status  = -1;
					Object param = null;
					Object result = null;

					switch (opcode) { 
					case ADD_CONFIG:
						param = in.readObject();

						try { 
							runner.addConfigurationTemplate((ConfigurationTemplate) param);
							status = 0;						
						} catch (Exception e) {
							status = 1;
							result = e;						
						}
						break;

					case GET_CONFIG: 
						param = in.readUTF();

						try { 
							result = runner.getConfigurationTemplate((String) param);
							status = 0;
						} catch (Exception e) {
							status = 1;
							result = e;						
						}

						break;

					case REMOVE_CONFIG:
						param = in.readUTF();

						try { 
							runner.removeConfigurationTemplate((String) param);
							status = 0;
						} catch (Exception e) {
							status = 1;
							result = e;						
						}
						break;
					case LIST_CONFIG:

						try { 
							result = runner.listConfigurationTemplates();
							status = 0;
						} catch (Exception e) {
							status = 1;
							result = e;						
						}

						break;

					case ADD_WORKER:
						param = in.readObject();

						try { 
							runner.addWorkerDescription((WorkerDescription) param);
							status = 0;						
						} catch (Exception e) {
							status = 1;
							result = e;						
						}
						break;

					case GET_WORKER:
						param = in.readUTF();

						try { 
							result = runner.getWorkerDescription((String) param);
							status = 0;						
						} catch (Exception e) {
							status = 1;
							result = e;						
						}
						break;

					case REMOVE_WORKER:					
						param = in.readUTF();

						try { 
							runner.removeWorkerDescription((String) param);
							status = 0;						
						} catch (Exception e) {
							status = 1;
							result = e;						
						}
						break;

					case LIST_WORKER:
						try { 
							result = runner.listWorkerDescriptions();
							status = 0;						
						} catch (Exception e) {
							status = 1;
							result = e;						
						}
						break;

					case ADD_INPUTS:
						param = in.readObject();

						try { 
							runner.addInputFileSet((FileSet) param);
							status = 0;						
						} catch (Exception e) {
							status = 1;
							result = e;						
						}
						break;

					case GET_INPUTS:
						param = in.readUTF();

						try { 
							result = runner.getInputFileSet((String) param);
							status = 0;						
						} catch (Exception e) {
							status = 1;
							result = e;						
						}
						break;

					case REMOVE_INPUTS:
						param = in.readUTF();

						try { 
							runner.removeInputFileSet((String) param);
							status = 0;						
						} catch (Exception e) {
							status = 1;
							result = e;						
						}
						break;

					case LIST_INPUTS:
						try { 
							result = runner.listInputFileSets();
							status = 0;						
						} catch (Exception e) {
							status = 1;
							result = e;						
						}
						break;

					case ADD_EXPERIMENT:
						param = in.readObject();

						try { 
							runner.addExperimentDescription((ExperimentDescription) param);
							status = 0;						
						} catch (Exception e) {
							status = 1;
							result = e;						
						}
						break;

					case GET_EXPERIMENT:
						param = in.readUTF();

						try { 
							result = runner.getExperimentDescription((String) param);
							status = 0;						
						} catch (Exception e) {
							status = 1;
							result = e;						
						}
						break;

					case REMOVE_EXPERIMENT:
						param = in.readUTF();

						try { 
							runner.removeExperimentDescription((String) param);
							status = 0;						
						} catch (Exception e) {
							status = 1;
							result = e;						
						}
						break;

					case LIST_EXPERIMENT:
						try { 
							result = runner.listExperimentDescriptions();
							status = 0;						
						} catch (Exception e) {
							status = 1;
							result = e;						
						}
						break;

					case START_RUNNING:
						param = in.readUTF();

						try { 
							result = runner.startExperiment((String) param);
							status = 0;						
						} catch (Exception e) {
							status = 1;
							result = e;						
						}
						break;

					case STOP_RUNNING:
						param = in.readUTF();

						try { 
							runner.stopRunningExperiment((String) param);
							status = 0;						
						} catch (Exception e) {
							status = 1;
							result = e;						
						}
						break;

					case LIST_RUNNING:
						try { 
							result = runner.listRunningExperiments();
							status = 0;						
						} catch (Exception e) {
							status = 1;
							result = e;						
						}
						break;

					case GET_RUNNING:
						param = in.readUTF();

						try { 
							result = runner.getRunningExperiment((String) param);
							status = 0;						
						} catch (Exception e) {
							status = 1;
							result = e;						
						}
						break;

					case LIST_STOPPED:
						try { 
							result = runner.listStoppedExperiments();
							status = 0;						
						} catch (Exception e) {
							status = 1;
							result = e;						
						}
						break;
						
					case GET_STOPPED:
						param = in.readUTF();

						try { 
							result = runner.getStoppedExperiment((String) param);
							status = 0;						
						} catch (Exception e) {
							status = 1;
							result = e;						
						}
						break;
						
					case REMOVE_STOPPED:

						param = in.readUTF();

						try { 
							runner.removeStoppedExperiment((String) param);
							status = 0;						
						} catch (Exception e) {
							status = 1;
							result = e;						
						}
						break;

					default: 
						status = 1;
						result = new Exception("Server does not understand request (opcode = " + opcode);
						done = true;
					}					
					
					out.writeInt(status);
					out.writeObject(result);
					out.flush();
				} catch (Exception e) {
					globalLogger.error("Failed to read handle invocation from " + socket.getInetAddress(), e);
					done = true;
				}
			} 
		}
		
		
		
	}
	
}
