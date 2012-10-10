package nl.esciencecenter.esalsa.deploy;

import java.util.List;

public interface POPRunnerInterface {
	
	// Configuration Templates
	public void addConfigurationTemplate(ConfigurationTemplate template) throws Exception;
	public ConfigurationTemplate getConfigurationTemplate(String templateID) throws Exception;
	public void removeConfigurationTemplate(String templateID) throws Exception;
	public List<String> listConfigurationTemplates() throws Exception;
	
	// Worker Descriptions
	public void addWorkerDescription(WorkerDescription worker) throws Exception;
	public WorkerDescription getWorkerDescription(String workerDescriptionID) throws Exception;
	public void removeWorkerDescription(String workerDescriptionID) throws Exception;
	public List<String> listWorkerDescriptions() throws Exception;
	
	// Input Files
	public void addInputFileSet(FileSet f) throws Exception;
	public FileSet getInputFileSet(String inputFileSetID) throws Exception; 
	public void removeInputFileSet(String inputFileSetID) throws Exception;
	public List<String> listInputFileSets() throws Exception;
	
	// Experiment Descriptions
	public void addExperimentDescription(ExperimentTemplate exp) throws Exception;
	public ExperimentTemplate getExperimentDescription(String experimentDescriptionID) throws Exception;
	public void removeExperimentDescription(String experimentDescriptionID) throws Exception;
	public List<String> listExperimentDescriptions() throws Exception;
		
	// Waiting experiments
	public String createExperiment(String descriptionID) throws Exception;
	public void startExperiment(String experimentID) throws Exception;
	public List<String> listWaitingExperiments() throws Exception;
	public ExperimentInfo getWaitingExperiment(String experimentID) throws Exception;
	
	// Running Experiments
	public void stopRunningExperiment(String experimentID) throws Exception;	
	public List<String> listRunningExperiments() throws Exception;
	public ExperimentInfo getRunningExperiment(String experimentID) throws Exception;
		
	// Stopped experiments
	public List<String> listStoppedExperiments() throws Exception;
	public ExperimentInfo getStoppedExperiment(String experimentID) throws Exception;
	public void removeStoppedExperiment(String experimentID) throws Exception;	
}

