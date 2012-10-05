package nl.esciencecenter.esalsa.deploy;

import java.io.File;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import nl.esciencecenter.esalsa.deploy.parser.DeployProperties;
import nl.esciencecenter.esalsa.util.FileTransferService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class POPRunner implements POPRunnerInterface, Runnable {

	private static final int MAX_FILETRANSFER_THREADS = 8;
	
	private static Logger globalLogger = LoggerFactory.getLogger("eSalsa");
	
	protected final FileTransferService fileTransferService; 
	
	private Store<ConfigurationTemplate> configurations; 
	
	private Store<WorkerDescription> workers;
	
	private Store<FileSet> inputs;
	
	private Store<ExperimentDescription> experimentDescriptions;

	private Store<ExperimentInfo> runningExperiments;
	
	private Store<ExperimentInfo> completedExperiments;
	
	private LinkedList<ExperimentRunner> experimentRunners = new LinkedList<ExperimentRunner>();
	
	// FIXME!!!
	private static int counter = 0;
	
	private final eSalsaDatabase db; 
	
	public POPRunner(DeployProperties p) {
		
		globalLogger.info("Reading configuration...");

		String databaseDir = p.getProperty("database.location");
		
		if (databaseDir == null) { 
			globalLogger.error("No database location specified! Please set the database.location property.");
			System.exit(1);
		}

		File tmp = new File(databaseDir);
		
		if (!tmp.exists()) { 
			if (!tmp.mkdirs()) { 
				globalLogger.error("Failed to create database directory " + tmp.getAbsolutePath());
				System.exit(1);
			}
		}
		
		if (!tmp.isDirectory() || !(tmp.canWrite() && tmp.canRead())) {
			globalLogger.error("Cannot access database directory " + tmp.getAbsolutePath());
			System.exit(1);
		}

		db = new eSalsaDatabase(databaseDir);
		
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
		
		globalLogger.info("Configuration successfull");
		
		globalLogger.info("Initializing datastructures....");
		
		configurations = db.getConfigurationTemplates(); 
		
		workers = db.getWorkerDescriptions();
		
		inputs = db.getInputSets();
		
		experimentDescriptions = db.getExperimentDescriptions();

		runningExperiments = db.getRunningExperiments();
		
		completedExperiments = db.getCompletedExperiments();
		
		globalLogger.info("Recovering running experiments....");
		
		List<String> running = runningExperiments.getKeys();
		
		if (running != null && running.size() > 0) { 
			
			for (String ID : running) { 
				try { 
					ExperimentInfo info = runningExperiments.get(ID);
					
					ExperimentRunner runner = new ExperimentRunner(info, this);
					experimentRunners.add(runner);
					
				} catch (Exception e) {
					globalLogger.warn("Failed to restore running experiment " + ID);
				}
			}
		}
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
		
		if (ID == null || ID.length() == 0) { 
			throw new IllegalArgumentException("Illegal ID!");
		}
		
		ExperimentDescription exp = experimentDescriptions.get(ID);
		String runID = generateID(ID);
	
		WorkerDescription worker = workers.get(exp.worker);
		FileSet input = inputs.get(exp.inputs); 
		ConfigurationTemplate template = configurations.get(exp.configuration);
		
		ExperimentInfo info = new ExperimentInfo(ID, exp.ID, worker, input, template);
		runningExperiments.add(info);
		
		ExperimentRunner e = new ExperimentRunner(info, this);
		experimentRunners.add(e);
		return runID;
	}

	protected void finishedExperiment(ExperimentRunner experimentInstance) {
		
		try { 
			completedExperiments.add(experimentInstance.getInfo());
		} catch (Exception e) {
			System.err.println("INTERNAL ERROR: Failed to add experiment " + experimentInstance.getInfo().ID + " to completed experiments database!");
			e.printStackTrace();
		}
		
		try { 
			runningExperiments.remove(experimentInstance.getInfo().ID);
		} catch (Exception e) {
			System.err.println("INTERNAL ERROR: Failed to remove experiment " + experimentInstance.getInfo().ID + " from running experiments database!");
			e.printStackTrace();
		}
	}
	
	@Override
	public ExperimentInfo getRunningExperiment(String ID) throws Exception {
		return runningExperiments.get(ID);
	}
	
	@Override
	public List<String> listRunningExperiments() {
		return runningExperiments.getKeys();
	}

	
//	private ExperimentInstance getExperimentInstance(String ID) throws Exception {
		//return runningExperiments.get(ID);
	//}

	@Override
	public void stopRunningExperiment(String ID) throws Exception {
		for (ExperimentRunner e : experimentRunners) { 
			if (ID.equals(e.getID())) { 
				e.mustStop();
				return;
			}
		}
	}

	@Override
	public List<String> listStoppedExperiments() throws Exception { 
		return completedExperiments.getKeys();
	}

	@Override
	public ExperimentInfo getStoppedExperiment(String experimentID) throws Exception {
		return completedExperiments.get(experimentID);
	}

	@Override
	public void removeStoppedExperiment(String experimentID) throws Exception {
		completedExperiments.remove(experimentID);		
	}	
	
	public void run() { 
		
		globalLogger.info("Starting...");
		
		boolean mustPrint = true;
		
		while (true) { 

			boolean stateChanged = false;
			
			if (experimentRunners.size() > 0) { 

				for (ExperimentRunner e : experimentRunners) { 

					try { 
						boolean change = e.run();
						
						if (change) { 
							runningExperiments.update(e.getInfo());
						}
						
						stateChanged = stateChanged | change;
					} catch (Exception ex) {
						globalLogger.error("Failed to retrieve experiment: " + e.getInfo().ID, ex);
					}

					mustPrint = true;
				} 
			
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
