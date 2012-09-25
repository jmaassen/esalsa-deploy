package nl.esciencecenter.esalsa.deploy;

import java.util.Calendar;
import java.util.List;

import nl.esciencecenter.esalsa.deploy.parser.DeployProperties;
import nl.esciencecenter.esalsa.util.FileTransferService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class POPRunner implements POPRunnerInterface, Runnable {

	private static final int MAX_FILETRANSFER_THREADS = 8;
	
	private static Logger globalLogger = LoggerFactory.getLogger("eSalsa");
	
	private final Object lock = new Object();
	
	protected final FileTransferService fileTransferService; 
	
	private MarkableMap<ConfigurationTemplate> configurations = new MarkableMap<ConfigurationTemplate>(lock, "Configuration");
	
	private MarkableMap<WorkerDescription> workers = new MarkableMap<WorkerDescription>(lock, "Worker");
	
	private MarkableMap<FileSet> inputs = new MarkableMap<FileSet>(lock, "InputFileSet");
//	private MarkableMap<FileSet> outputs = new MarkableMap<FileSet>(lock, "OutputFileSet");
	
	private MarkableMap<ExperimentDescription> experimentDescriptions = new MarkableMap<ExperimentDescription>(lock, "Experiment Descriptions");

	private MarkableMap<ExperimentInstance> runningExperiments = new MarkableMap<ExperimentInstance>(lock, "Running Experiments");
	
	private MarkableMap<ExperimentInfo> stoppedExperiments = new MarkableMap<ExperimentInfo>(lock, "Stopped Experiments");
	
	// FIXME!!!
	private static int counter = 0;
	
	public POPRunner(DeployProperties p) {
		
		globalLogger.info("Reading configuration...");
		
		int threads = p.getIntProperty("filetransfer.threads", 1);
		
		if (threads <= 0) { 
			globalLogger.warn("Number of file transfer threads <= 0: reset to 1");
			threads = 1;
		} 
		
		if (threads > MAX_FILETRANSFER_THREADS) {
			globalLogger.warn("Number of file transfer threads exceeds maximum: reset to " + MAX_FILETRANSFER_THREADS);
			threads = MAX_FILETRANSFER_THREADS;
		}

		globalLogger.info("filetransfer.threads = " + threads);
		
		fileTransferService = new FileTransferService(threads);
		
		// Add more info...
		
		globalLogger.info("Configuration successfull");
	}
	
	public String getTempDir() {
		return "/tmp";
	}

	
	private String format(int number, int pos) { 
		
		String tmp = "" + number;
		
		while (tmp.length() < pos) { 
			tmp = "0" + tmp;
		}

		return tmp;
	}
 	
	private synchronized String generateID(String baseID) {
		
		Calendar c = Calendar.getInstance();
		
		StringBuilder b = new StringBuilder(baseID);
		b.append(".");
		b.append(c.get(Calendar.YEAR));
		b.append(format(c.get(Calendar.MONTH), 2));
		b.append(format(c.get(Calendar.DAY_OF_MONTH), 2));
		b.append(".");
		b.append(format(c.get(Calendar.HOUR_OF_DAY), 2));
		b.append(format(c.get(Calendar.MINUTE), 2));
		b.append(format(c.get(Calendar.SECOND), 2));
		b.append(".");
		b.append(counter++);
		
		return b.toString();
	}
	
	private void sleep(int seconds) { 
		try { 
			Thread.sleep(seconds*1000);
		} catch (Exception e) {
			// ignored
		}
	}
	
	/* Configuration */
	@Override
	public void addConfigurationTemplate(ConfigurationTemplate template) throws Exception { 
		configurations.add(template);
	}

	@Override
	public List<String> listConfigurationTemplates() { 
		return configurations.getKeys();
	}
	
	@Override
	public ConfigurationTemplate getConfigurationTemplate(String ID) throws Exception { 
		return configurations.get(ID);
	}
	
	@Override
	public void removeConfigurationTemplate(String ID) throws Exception { 
		configurations.remove(ID);
	}
	
	/* Worker */

	@Override
	public void addWorkerDescription(WorkerDescription worker) throws Exception { 
		workers.add(worker);
	}
	
	@Override
	public List<String> listWorkerDescriptions() { 
		return workers.getKeys();
	}

	@Override
	public WorkerDescription getWorkerDescription(String ID) throws Exception { 
		return workers.get(ID);
	}

	@Override
	public void removeWorkerDescription(String ID) throws Exception { 
		workers.remove(ID);
	}

	/* Inputs */
	@Override
	public void addInputFileSet(FileSet f) throws Exception { 
		inputs.add(f);
	}

	@Override
	public List<String> listInputFileSets() { 
		return inputs.getKeys();
	}

	@Override
	public FileSet getInputFileSet(String ID) throws Exception { 
		return inputs.get(ID);
	}

	@Override
	public void removeInputFileSet(String ID) throws Exception { 
		inputs.remove(ID);
	}
	
	/* Outputs */
/*	
	public void addOutputFileSet(FileSet f) throws Exception { 
		outputs.add(f);
	}

	public List<String> listOutputFileSets() { 
		return outputs.getKeys();
	}
	
	public FileSet getOutputFileSet(String ID) throws Exception { 
		return outputs.get(ID);
	}
	
	public void removeOutputFileSet(String ID) throws Exception { 
		outputs.remove(ID);
	}
*/	
	/* Experiment Descriptions */

	@Override
	public void addExperimentDescription(ExperimentDescription exp) throws Exception { 
		experimentDescriptions.add(exp);
	}

	@Override
	public List<String> listExperimentDescriptions() { 
		return experimentDescriptions.getKeys();
	}

	@Override
	public ExperimentDescription getExperimentDescription(String ID) throws Exception { 
		return experimentDescriptions.get(ID);
	}

	@Override
	public void removeExperimentDescription(String ID) throws Exception { 
		experimentDescriptions.remove(ID);
	}
	
	/* Experiments */	
	@Override
	public String startExperiment(String ID) throws Exception { 
		ExperimentDescription exp = experimentDescriptions.get(ID);
		String runID = generateID(ID);
	
		ExperimentInstance e = new ExperimentInstance(runID, exp, this);
		runningExperiments.add(e);
		return e.ID;
	}

	protected void finishedExperiment(ExperimentInstance experimentInstance) {
		
		synchronized (lock) {

			try { 
				ExperimentInstance tmp = runningExperiments.remove(experimentInstance.ID);
				
				if (tmp != null) { 
					stoppedExperiments.add(tmp.getInfo());
				}
			} catch (Exception e) {
				System.out.println("Failed to remove experiment " + experimentInstance.ID);
			}
		}
	}

	@Override
	public ExperimentInfo getRunningExperiment(String ID) throws Exception {
		return runningExperiments.get(ID).getInfo();
	}
	
	@Override
	public List<String> listRunningExperiments() {
		return runningExperiments.getKeys();
	}

	private ExperimentInstance getExperimentInstance(String ID) throws Exception {
		return runningExperiments.get(ID);
	}

	@Override
	public void stopRunningExperiment(String ID) throws Exception {
		getExperimentInstance(ID).mustStop();
	}

	@Override
	public List<String> listStoppedExperiments() throws Exception { 
		return stoppedExperiments.getKeys();
	}

	@Override
	public ExperimentInfo getStoppedExperiment(String experimentID) 	throws Exception {
		return stoppedExperiments.get(experimentID);
	}

	@Override
	public void removeStoppedExperiment(String experimentID) throws Exception {
		stoppedExperiments.remove(experimentID);		
	}	
	
	public void run() { 
		
		globalLogger.info("Starting...");
		
		boolean mustPrint = true;
		
		while (true) { 

			boolean stateChanged = false;
			
			List<String> tmp = listRunningExperiments();
				
			if (tmp.size() > 0) { 
				//System.out.println("Running " + tmp.size() + " experiments: ");
					
				for (String ID : tmp) { 
					try { 
						ExperimentInstance e = getExperimentInstance(ID);
						//System.out.println(" " + e.ID + " on " + e.worker.ID);
						boolean change = e.run();
						stateChanged = stateChanged | change;
					} catch (Exception e) {
						globalLogger.error("Failed to retrieve experiment: " + ID, e);
					}
				}

				mustPrint = true;
			} else { 
				if (mustPrint) { 
					globalLogger.info("I am now idle...");
					mustPrint = false;
				}
			}

			if (!stateChanged) { 
				sleep(1);
			}
		}
	}
}
