package nl.esciencecenter.esalsa.deploy.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import nl.esciencecenter.esalsa.deploy.ConfigurationTemplate;
import nl.esciencecenter.esalsa.deploy.ExperimentDescription;
import nl.esciencecenter.esalsa.deploy.ExperimentInfo;
import nl.esciencecenter.esalsa.deploy.FileSet;
import nl.esciencecenter.esalsa.deploy.POPRunnerInterface;
import nl.esciencecenter.esalsa.deploy.WorkerDescription;

public class Stub implements POPRunnerInterface, Protocol {

	private final SimpleStub stub;
	
	public Stub(Socket socket) throws IOException {
		stub = new SimpleStub(socket);
	}
	
	public void close() { 
		stub.close();
	}
			
	@Override
	public void addConfigurationTemplate(ConfigurationTemplate template) throws Exception {
		stub.add(CONFIG, template);
	}		
		
	@Override
	public ConfigurationTemplate getConfigurationTemplate(String templateID) throws Exception {
		return (ConfigurationTemplate) stub.get(CONFIG, templateID);
	}

	@Override
	public void removeConfigurationTemplate(String templateID) throws Exception {
		stub.remove(CONFIG, templateID);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> listConfigurationTemplates() throws Exception {
		return stub.list(CONFIG);
	} 		

	@Override
	public void addWorkerDescription(WorkerDescription worker) throws Exception {
		stub.add(WORKER, worker);
	}

	@Override
	public WorkerDescription getWorkerDescription(String workerDescriptionID) throws Exception {
		return (WorkerDescription) stub.get(WORKER,  workerDescriptionID);
	}

	@Override
	public void removeWorkerDescription(String workerDescriptionID) throws Exception {
		stub.remove(WORKER, workerDescriptionID);
	}		

	@SuppressWarnings("unchecked")
	@Override
	public List<String> listWorkerDescriptions() throws Exception {
		return stub.list(WORKER);
	}

	@Override
	public void addInputFileSet(FileSet f) throws Exception {
		stub.add(INPUTS, f);
	}

	@Override
	public FileSet getInputFileSet(String inputFileSetID) throws Exception {
		return (FileSet) stub.get(INPUTS, inputFileSetID);
	}

	@Override
	public void removeInputFileSet(String inputFileSetID) throws Exception {
		stub.remove(INPUTS, inputFileSetID);
	}

	@Override
	public List<String> listInputFileSets() throws Exception {
		return stub.list(INPUTS);
	}
		
	@Override
	public void addExperimentDescription(ExperimentDescription exp) throws Exception {
		stub.add(EXPERIMENT, exp);
	}
	
	@Override
	public ExperimentDescription getExperimentDescription(String experimentDescriptionID) throws Exception {
		return (ExperimentDescription) stub.get(EXPERIMENT, experimentDescriptionID);
	}

	@Override
	public void removeExperimentDescription(String experimentDescriptionID) throws Exception {
		stub.remove(EXPERIMENT, experimentDescriptionID);
	}

	@Override
	public List<String> listExperimentDescriptions() throws Exception {
		return stub.list(EXPERIMENT);
	}

	@Override
	public String startExperiment(String descriptionID) throws Exception {
		return stub.start(descriptionID);
	}

	@Override
	public void stopRunningExperiment(String experimentID) throws Exception {
		stub.stop(experimentID);
	} 

	@Override
	public ExperimentInfo getRunningExperiment(String experimentID) throws Exception {
		return (ExperimentInfo) stub.get(RUNNING, experimentID);
	}

	@Override
	public List<String> listRunningExperiments() throws Exception {
		return stub.list(RUNNING);
	}
		
	@Override
	public List<String> listStoppedExperiments() throws Exception {
		return stub.list(COMPLETED);
	}

	@Override
	public ExperimentInfo getStoppedExperiment(String experimentID) throws Exception {
		return (ExperimentInfo) stub.get(COMPLETED, experimentID);
	}

	@Override
	public void removeStoppedExperiment(String experimentID) throws Exception {
		stub.remove(COMPLETED, experimentID);
	}
}
