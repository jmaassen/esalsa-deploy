package nl.esciencecenter.esalsa.deploy.ui.cli;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import nl.esciencecenter.esalsa.deploy.ConfigurationTemplate;
import nl.esciencecenter.esalsa.deploy.ExperimentDescription;
import nl.esciencecenter.esalsa.deploy.ExperimentInfo;
import nl.esciencecenter.esalsa.deploy.FileSet;
import nl.esciencecenter.esalsa.deploy.POPRunnerInterface;
import nl.esciencecenter.esalsa.deploy.WorkerDescription;

public class Stub implements POPRunnerInterface, Protocol {

	private final Socket socket;
	private final ObjectInputStream in;
	private final ObjectOutputStream out;
	
	public Stub(Socket socket) throws IOException { 
		this.socket = socket;
		
		in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
		out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		out.flush();
	}
	
	public void close() { 
		
		try { 
			out.close();
		} catch (Exception e) {
			// ignored
		}

		try { 
			in.close();
		} catch (Exception e) {
			// ignored
		}
		
		try { 
			socket.close();
		} catch (Exception e) {
			// ignored
		}
	}
	
	
	@Override
	public void addConfigurationTemplate(ConfigurationTemplate template) throws Exception {
	
		out.writeInt(ADD_CONFIG);
		out.writeObject(template);
		out.flush();
		
		int status = in.readInt();
		Object result = in.readObject();
		
		if (status == 0) { 
			return;
		} else {
			throw (Exception) result;
		}
	}

	@Override
	public ConfigurationTemplate getConfigurationTemplate(String templateID) throws Exception {
		
		out.writeInt(GET_CONFIG);
		out.writeUTF(templateID);
		out.flush();
		
		int status = in.readInt();
		Object result = in.readObject();
		
		if (status == 0) { 
			return (ConfigurationTemplate) result;
		} else {
			throw (Exception) result;
		}
	}

	@Override
	public void removeConfigurationTemplate(String templateID) throws Exception {
		
		out.writeInt(REMOVE_CONFIG);
		out.writeUTF(templateID);
		out.flush();

		int status = in.readInt();
		Object result = in.readObject();
		
		if (status == 0) { 
			return;
		} else {
			throw (Exception) result;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> listConfigurationTemplates() throws Exception {
		
		out.writeInt(LIST_CONFIG);
		out.flush();

		int status = in.readInt();
		Object result = in.readObject();
		
		if (status == 0) { 
			return (List<String>) result;
		} else {
			throw (Exception) result;
		}
	}

	@Override
	public void addWorkerDescription(WorkerDescription worker) throws Exception {

		out.writeInt(ADD_WORKER);
		out.writeObject(worker);
		out.flush();
		
		int status = in.readInt();
		Object result = in.readObject();
		
		if (status == 0) { 
			return;
		} else {
			throw (Exception) result;
		}
	}

	@Override
	public WorkerDescription getWorkerDescription(String workerDescriptionID) throws Exception {

		out.writeInt(GET_WORKER);
		out.writeUTF(workerDescriptionID);
		out.flush();

		int status = in.readInt();
		Object result = in.readObject();
		
		if (status == 0) { 
			return (WorkerDescription) result;
		} else {
			throw (Exception) result;
		}
	}

	@Override
	public void removeWorkerDescription(String workerDescriptionID) throws Exception {
		
		out.writeInt(REMOVE_WORKER);
		out.writeUTF(workerDescriptionID);
		out.flush();
		
		int status = in.readInt();
		Object result = in.readObject();
		
		if (status == 0) { 
			return;
		} else {
			throw (Exception) result;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> listWorkerDescriptions() throws Exception {
		
		out.writeInt(LIST_WORKER);
		out.flush();
		
		int status = in.readInt();
		Object result = in.readObject();
		
		if (status == 0) { 
			return (List<String>) result;
		} else {
			throw (Exception) result;
		}
	}

	@Override
	public void addInputFileSet(FileSet f) throws Exception {

		out.writeInt(ADD_INPUTS);
		out.writeObject(f);
		out.flush();
		
		int status = in.readInt();
		Object result = in.readObject();
		
		if (status == 0) { 
			return;
		} else {
			throw (Exception) result;
		}
	}

	@Override
	public FileSet getInputFileSet(String inputFileSetID) throws Exception {
		
		out.writeInt(GET_INPUTS);
		out.writeUTF(inputFileSetID);
		out.flush();
		
		int status = in.readInt();
		Object result = in.readObject();
		
		if (status == 0) { 
			return (FileSet) result;
		} else {
			throw (Exception) result;
		}
	}

	@Override
	public void removeInputFileSet(String inputFileSetID) throws Exception {

		out.writeInt(REMOVE_INPUTS);
		out.writeUTF(inputFileSetID);
		out.flush();

		int status = in.readInt();
		Object result = in.readObject();
		
		if (status == 0) { 
			return;
		} else {
			throw (Exception) result;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> listInputFileSets() throws Exception {
		
		out.writeInt(LIST_INPUTS);
		out.flush();
		
		
		int status = in.readInt();
		Object result = in.readObject();
		
		if (status == 0) { 
			return (List<String>) result;
		} else {
			throw (Exception) result;
		}
	}

	@Override
	public void addExperimentDescription(ExperimentDescription exp) throws Exception {
		
		out.writeInt(ADD_EXPERIMENT);
		out.writeObject(exp);
		out.flush();

		int status = in.readInt();
		Object result = in.readObject();
		
		if (status == 0) { 
			return;
		} else {
			throw (Exception) result;
		}
	}

	@Override
	public ExperimentDescription getExperimentDescription(String experimentDescriptionID) throws Exception {
		
		out.writeInt(GET_EXPERIMENT);
		out.writeUTF(experimentDescriptionID);
		out.flush();
		
		int status = in.readInt();
		Object result = in.readObject();
		
		if (status == 0) { 
			return (ExperimentDescription) result;
		} else {
			throw (Exception) result;
		}
	}

	@Override
	public void removeExperimentDescription(String experimentDescriptionID) throws Exception {

		out.writeInt(REMOVE_EXPERIMENT);
		out.writeUTF(experimentDescriptionID);
		out.flush();
		
		int status = in.readInt();
		Object result = in.readObject();
		
		if (status == 0) { 
			return;
		} else {
			throw (Exception) result;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> listExperimentDescriptions() throws Exception {
		
		out.writeInt(LIST_EXPERIMENT);
		out.flush();

		int status = in.readInt();
		Object result = in.readObject();
		
		if (status == 0) { 
			return (List<String>) result;
		} else {
			throw (Exception) result;
		}
	}

	@Override
	public String startExperiment(String descriptionID) throws Exception {
		
		out.writeInt(START_RUNNING);
		out.writeUTF(descriptionID);
		out.flush();

		int status = in.readInt();
		Object result = in.readObject();
		
		if (status == 0) { 
			return (String) result;
		} else {
			throw (Exception) result;
		}
	}

	@Override
	public void stopRunningExperiment(String experimentID) throws Exception {

		out.writeInt(STOP_RUNNING);
		out.writeUTF(experimentID);
		out.flush();
		
		int status = in.readInt();
		Object result = in.readObject();
		
		if (status == 0) { 
			return;
		} else {
			throw (Exception) result;
		}
	}

	@Override
	public ExperimentInfo getRunningExperiment(String experimentID) throws Exception {
		
		out.writeInt(GET_RUNNING);
		out.writeUTF(experimentID);
		out.flush();
		
		int status = in.readInt();
		Object result = in.readObject();
		
		if (status == 0) { 
			return (ExperimentInfo) result;
		} else {
			throw (Exception) result;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> listRunningExperiments() throws Exception {
		
		out.writeInt(LIST_RUNNING);
		out.flush();
		
		int status = in.readInt();
		Object result = in.readObject();
		
		if (status == 0) { 
			return (List<String>) result;
		} else {
			throw (Exception) result;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> listStoppedExperiments() throws Exception {
		
		out.writeInt(LIST_STOPPED);
		out.flush();
		
		int status = in.readInt();
		Object result = in.readObject();
		
		if (status == 0) { 
			return (List<String>) result;
		} else {
			throw (Exception) result;
		}
	}


	@Override
	public ExperimentInfo getStoppedExperiment(String experimentID) throws Exception {
		
		out.writeInt(GET_STOPPED);
		out.writeUTF(experimentID);
		out.flush();
		
		int status = in.readInt();
		Object result = in.readObject();
		
		if (status == 0) { 
			return (ExperimentInfo) result;
		} else {
			throw (Exception) result;
		}
	}


	@Override
	public void removeStoppedExperiment(String experimentID) throws Exception {
		
		out.writeInt(REMOVE_STOPPED);
		out.writeUTF(experimentID);
		out.flush();
		
		int status = in.readInt();
		Object result = in.readObject();
		
		if (status == 0) { 
			return;
		} else {
			throw (Exception) result;
		}
	}

}
