package nl.esciencecenter.esalsa.deploy;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.esciencecenter.esalsa.deploy.parser.TemplateParser;
import nl.esciencecenter.esalsa.util.Event;
import nl.esciencecenter.esalsa.util.EventLogger;
import nl.esciencecenter.esalsa.util.Utils;

public class EnsembleDescription {

	private static final Logger globalLogger = LoggerFactory.getLogger("eSalsa");
	
	public static final String [] ensembleFields = new String [] { 
		"baseID", 
		"size", 
		"template", 
		"resources", 
		"resources.file"
	};
	
	public static final String [] inputFiles = new String [] {
		"input.grid.horizontal", 
		"input.grid.vertical", 
		"input.grid.topography", 
		"input.grid.bottomcells",
		"input.forcing.ws", 
		"input.forcing.shf", 
		"input.forcing.sfwf",
		"input.tracers.dye",
		"input.config.diag.transport", 
		"input.config.tavg", 
		"input.config.movie",
		/*"input.config.history",*/	
		"input.restart",
		"input.restart.header",
	};
	
	public static final String [] outputFiles = new String [] {
		"output.diag", 
		"output.diag.transport", 
		"output.tavg", 
		"output.movie", 
		/*"output.history",*/ 
		"output.restart" 
	};
	
	public static final String [] siteFields = new String [] { 
		"site.slots",
		"site.inputDir",
		"site.outputDir",
		"site.experimentDir",
		"site.templateDir"		
	};

	public static final String [] generatedFields = new String [] {
		"generated.log", 
		"generated.runID"  
	};

	private final String name;
	
	public final String baseID;
	
	// Input files + the resource that contains them.
	private final Map<String, FileDescription> inputs;
	
	// Output directory
	private final Map<String, String> outputs;

	// Configuration file template
	public final String templateFile;

	// Resources on which to run	
	public final List<MachineDescription> machines;
	
	// Pending experiments
	public final LinkedList<Experiment> pendingExperiments = new LinkedList<Experiment>(); 

	// Running experiments, one entry for each execution slot.
	public final LinkedList<Experiment> runningExperiments = new LinkedList<Experiment>(); 

	// Experiments that haved stopped running, but have not been moved to the finished list yet.
	public final LinkedList<Experiment> stoppedExperiments = new LinkedList<Experiment>(); 
	
	// Finished experiments
	public final LinkedList<Experiment> finishedExperiments = new LinkedList<Experiment>(); 

	// Empty execution slots (machines waiting for work).
	public final LinkedList<MachineDescription> emptyExecutionSlots = new LinkedList<MachineDescription>();
	
	// Filled execution slots (machines currently working).
//	public final LinkedList<MachineDescription> filledExecutionSlots = new LinkedList<MachineDescription>();
	
	// Total execution slots available on the various machines.
	public final int totalSlots;
	
	private EventLogger eventLogger;
	
	private File localOutputDir; 
	private File localTemplateDir; 
	
//	private Executor [] executors;
	
	private final int size;
//	private int currentExperiment = 0;	
//	private int done = 0;	
	
	private FileTransferService fileTransferService;
	
	private void info(String message) { 
		globalLogger.info("[" + baseID + "] " + message);
	}

	private void error(String message, Throwable e) { 
		globalLogger.error("[" + baseID + "] " + message, e);
		System.exit(1);
	}
	
	private void error(String message) {
		globalLogger.error("[" + baseID + "] " + message);
		System.exit(1);
	}
	
	public EnsembleDescription(String name, 
			int size, String baseID, 
			Map<String, FileDescription> inputs, 
			Map<String, String> outputs, 
			String templateFile, 
			List<MachineDescription> machines) {
		
		this.name = name;
		this.size = size;
		this.baseID = baseID;
		this.inputs = inputs;
		this.outputs = outputs;
		this.templateFile = templateFile;
		this.machines = machines;
		
		int slots = 0;
		
		for (MachineDescription m : machines) { 
			slots += m.slots;			
		} 	
		
		totalSlots = slots;
		
		for (MachineDescription m : machines) {
			for (int i=0;i<m.slots;i++) {
				emptyExecutionSlots.addLast(m);
			}
		}
		
		try { 
			eventLogger = EventLogger.get();
		} catch (Exception e) {
			eventLogger = null;
			error("[EnsembleDescription] Event logger not found!");
		}
		
		eventLogger.log("[ENSEMBLE]", baseID, "CREATED", "OK");
	}
	
	public Map<String, FileDescription> getInputs() {
		return inputs;
	}
	
	public Map<String, String> getOutputs() {
		return outputs;
	}
	
	public String toString() { 
		return "Esemble(" + name + ", " + size + ")";
	}			
	
	private void verifyFile(FileDescription fd, String prefix) {		
		if (Utils.accessibleFile(fd)) { 
			info(prefix + fd.file.URI + " - ACCESS OK");
		} else { 
			error(prefix + fd.file.URI + " - ACCESS FAILED");
		}
	}
	
	private void verifyInputs() {
		
		// Check if each of the input files exists. These may be remote!
		info("  Verifying input files: ");
		
		for (FileDescription fd : inputs.values()) {
			verifyFile(fd, "    ");
		} 
	}

	private void verifyDir(FileDescription dir, String prefix) { 
		
		if (Utils.accessibleDirectory(dir)) { 
			info(prefix + dir + " - ACCESS OK");
		} else { 
			error(prefix + dir + " - ACCESS FAILED");
		} 
	}
	

	
	private void verifyMachine(MachineDescription m) {

		// Check if we can access each of the machines
		// Check if the executable is available on the machine 
		// Check if we can access/create the necessary directories 

		info("  Verifying machine: " + m.name);
		
		verifyDir(m.inputDir, "    input directory: ");
		verifyDir(m.outputDir, "    output directory: ");
		verifyDir(m.experimentDir, "    experiment directory: ");
		verifyDir(m.templateDir, "    template directory: ");
		verifyFile(m.startScript, "    start script: ");
		verifyFile(m.stopScript, "    stop script: ");
		verifyFile(m.monitorScript, "    monitor script: ");
		verifyFile(m.configTemplate, "    pop configuration template: ");
	}
	
	private void verifyMachines() {
		for (MachineDescription m : machines) {
			verifyMachine(m);
		}
	}
	
	private void verifyOutput() {
		// Check if the output directory exists/can be created
//		for (String file : ensemble.getOutputs().values()) { 
			//logger.info("Verifying output : " + file);
			//verifyFile(file);
		//}
	}

	public void verify() {
		
		info("Verifying ensemble description...");
		
		verifyInputs();
		verifyMachines();
		verifyOutput();
		
		eventLogger.log("[ENSEMBLE]", baseID, "VERIFIED", "OK");

		info("Ensemble description appears to be correct!");		
	}
		
	private void createLocalDirectory(File path) { 

		info("Creating local directory: " + path.getAbsolutePath());
		
		if (!path.mkdirs()) { 
			error("Failed to creating output directory: " + path.getAbsolutePath());
		}
	}
	
	
	private void createLocalDirectories(File localOutputBase) { 
		
		info("Creating local directories: ");
		
		if (!(localOutputBase.exists() && localOutputBase.isDirectory() && localOutputBase.canWrite())) { 
			error("Failed to access output directory: " + localOutputBase.getAbsolutePath());
		}

		localOutputDir = new File(localOutputBase.getAbsolutePath() + File.separator + baseID);
		localTemplateDir = new File(localOutputDir.getAbsoluteFile() + File.separator + "templates"); 

		if (!eventLogger.restart) { 
			createLocalDirectory(localOutputDir);
			createLocalDirectory(localTemplateDir);
		}
	}
	
	private void generateOutputDirectories(MachineDescription m) { 

		FileDescription experimentDir = Utils.getSubFile(m.experimentDir, baseID);
		
		if (!eventLogger.restart) { 
			try {
				Utils.createDir(experimentDir);
			} catch (Exception e) {
				error("Failed to create directory " + experimentDir.file.URI, e);
			}
		} 
		
		m.setExperimentDir(experimentDir);
		
		FileDescription outputDir = Utils.getSubFile(m.outputDir, baseID);
		
		if (!eventLogger.restart) { 
			try {
				Utils.createDir(outputDir);
			} catch (Exception e) {
				error("Failed to create directory " + outputDir.file.URI, e);
			}
		}
		
		m.setOutputDir(outputDir);
	}
	
	private void generateOutputDirectories() { 
		
		info("Creating output directories: ");
		
		for (MachineDescription m : machines) { 
			generateOutputDirectories(m); 
		}
	}
	
	public void createDirectories(File localOutputBase) {
		
		// First check if this was already handled... 
		if (eventLogger.restart) { 
			Event e = eventLogger.findOne("[ENSEMBLE]", baseID, "DIRS_CREATED", "OK");
			
			if (e == null) {
				error("Log seems to be corrupt. No DIRS_CREATED entry found!");
			} 
		}
		
		// FIXME: we don't allow partial failures...
		createLocalDirectories(localOutputBase);
		generateOutputDirectories();
		
		eventLogger.log("[ENSEMBLE]", baseID, "DIRS_CREATED", "OK");
	}

	private boolean contains(String [] array, String value) {
		for (String s : array) { 
			if (s != null && value.equals(s)) { 
				return true;
			}
		}
		
		return false;
	}
	
	private void verifyTemplate(Template t,  FileDescription source) {
		
		info("Verifying template variables:");
		
		String [] variables = t.getVariables();
		
		for (String v : variables) { 
		
			if (contains(EnsembleDescription.inputFiles, v)) { 
				info("  " + v + " - INPUT FILE");
			} else if (contains(EnsembleDescription.outputFiles, v)) { 
				info("  " + v + " - OUTPUT FILE");			
			} else if (contains(EnsembleDescription.siteFields, v)) {
				info("  " + v + " - SITE DEPENDANT");
			} else if (contains(EnsembleDescription.generatedFields, v)) {
				info("  " + v + " - GENERATED"); 
			} else { 
				error("  " + v + " - UNKNOWN VARIABLE!");
			}
		} 
	}
	
	public void generateTemplates() {
		
		info("Loading machine specific templates: ");
		
		for (MachineDescription m : machines) {
			
			String path = localTemplateDir.getAbsolutePath() + File.separator + "pop_in_template_" + m.name;
			
			FileDescription tmp = new FileDescription(new ResourceDescription(path), null);		
			FileTransferHandle h = fileTransferService.queue(new FileTransferDescription(m.configTemplate, tmp));
			
			h.waitUntilDone();
			Exception e = h.getException();
			
			if (e != null) { 
				error("Failed to access configuration template: " + m.configTemplate, e);
			}
		
			Template t = null;
			
			try { 		
				t = new TemplateParser(new File(path)).parse();
			} catch (Exception ex) { 
				error("Failed to parse template: " + m.configTemplate, e);
			}
			
			verifyTemplate(t, m.configTemplate);
			m.addTemplate(t);
		}
		
		eventLogger.log("[ENSEMBLE]", baseID, "TEMPLATES", "OK");
	}

	/*
	public synchronized void startExecutors(FileTransferService fileTransferService) { 
		
		info("Starting executors: " + totalSlots);
		
		MachineDescription [] tmp = new MachineDescription[totalSlots];
		int index = 0;
				
		for (MachineDescription m : machines) {
			for (int i=0;i<m.slots;i++) { 
				tmp[index++] = m;
			}
		}
		
		executors = new Executor[totalSlots];
			
		for (int i=0;i<totalSlots;i++) { 
			executors[i] = new Executor(baseID + ".executor." + i, this, fileTransferService, tmp[i]);
			executors[i].start();
		}
	}
	*/
	/*
	private synchronized void waitUntilDone(FileTransferService fileTransferService) { 
	
		while (done < size) { 
			try {
				wait(1000);
			} catch (InterruptedException e) {
				// ignored
			}
		}
		
		fileTransferService.done();
	}
	 */
	
	/*
	public HashMap<String, String> generateMachineMapping(MachineDescription md, String ID) throws Exception { 
		
		HashMap<String, String> variables = new HashMap<String, String>();		
		
		for (String s : inputFiles) { 
		
			FileDescription fd = inputs.get(s);
			
			if (fd == null) { 
				throw new Exception("Failed to find value for input file variable: " + s);
			}

//System.err.println(s + " " + md + " " + fd);			
			
			variables.put(s, Utils.getPath(md.inputDir.file.URI) + File.separator + Utils.getFileName(fd.file.URI));
		} 

		for (String s : EnsembleDescription.outputFiles) { 
			
			String name = outputs.get(s);
			
			if (name == null) { 
				throw new Exception("Failed to find value for output file variable: " + s);
			}

			variables.put(s, Utils.getPath(md.outputDir.file.URI) + File.separator + baseID + File.separator + name);
		} 
		
		// variables.put("site.cores", "" + md.cores);		
		variables.put("generated.runID", "run." + baseID + "." + ID);
		variables.put("generated.log", Utils.getPath(md.experimentDir.file.URI) + File.separator + baseID + File.separator + ID + ".log");

		return variables;
	}
	
	public synchronized Experiment generateExperiment(MachineDescription target) throws Exception {
		
		if (currentExperiment == size) {
			info("NOT generating another experiment!");
			return null;
		}
		
		String ID = "experiment." + currentExperiment;
		currentExperiment++;
		
		info("Generating experiment: " + ID);
		
		HashMap<String, String> mapping = generateMachineMapping(target, ID); 
		String config = target.generateConfiguration(mapping);
		
		 

		createLocalDirectory(dir);
		
		return new Experiment(ID, inputs, outputs, config, target, dir);
	}
*/

	public void finishedExperiment(Experiment e) {

		eventLogger.log("[ENSEMBLE]", baseID, "STOPPED_EXPERIMENT", e.ID);
		
		synchronized (stoppedExperiments) {
			stoppedExperiments.addLast(e);
		}
	}

	private boolean processStoppedExperiments() { 
		
		boolean stopped = false;
		
		synchronized (stoppedExperiments) {
			
			stopped = (stoppedExperiments.size() > 0);
			
			while (stoppedExperiments.size() > 0) { 
				Experiment e = stoppedExperiments.removeFirst();
				MachineDescription m = e.getMachineDescription();
	
				runningExperiments.remove(e);
				
				finishedExperiments.addLast(e);
				emptyExecutionSlots.addLast(m);

				eventLogger.log("[ENSEMBLE]", baseID, "FINISHED_EXPERIMENT", e.ID);
				
				info("Finished " + e.ID + " on " + m.name);
			}
		} 
		
		return stopped;
	}
	
	private int getFinished() {
		return finishedExperiments.size();
	}
	
	public void generateExperiments() {

		if (eventLogger.restart) { 
			
			Event e = eventLogger.findOne("[ENSEMBLE]", baseID, "GENERATING_EXPERIMENTS_DONE", "-");
			
			if (e == null) {
				error("Log seems to be corrupt: No GENERATING_EXPERIMENTS DONE entry found");
			}
			
			restoreExperiments();
			return;
		} 
		
		generateFreshExperiments();
	}
	
	private void generateFreshExperiments() { 
		
		// We can safely generate the experiment here, as they have not run.
		info("Generating " + size + " experiments: ");
		
		eventLogger.log("[ENSEMBLE]", baseID, "GENERATING_EXPERIMENTS_START", "-");
		
		for (int i=0;i<size;i++) { 
			String ID = "experiment." + i;
			
			pendingExperiments.add(new Experiment(this, ID, inputs, outputs, localOutputDir));
		
			eventLogger.log("[ENSEMBLE]", baseID, "GENERATING_EXPERIMENT", ID);
			
			info("  generated: " + ID);
		}
		
		eventLogger.log("[ENSEMBLE]", baseID, "GENERATING_EXPERIMENTS_DONE", "-");
	}

	private void restoreExperiments() { 
		
		// We can safely generate the experiment here, as they have not run.
		info("Restoring " + size + " experiments: ");
		
		for (int i=0;i<size;i++) { 
			String ID = "experiment." + i;
			
			Experiment e = new Experiment(this, ID, inputs, outputs, localOutputDir);
			
			if (eventLogger.exists("[ENSEMBLE]", baseID, "FINISHED_EXPERIMENT", ID)) { 
				finishedExperiments.add(e);
				info("  restored: " + ID + " to finished queue");
			} else if (eventLogger.exists("[ENSEMBLE]", baseID, "STOPPED_EXPERIMENT", ID)) {  
				stoppedExperiments.add(e);
				info("  restored: " + ID + " to stopped queue");
			} else if (eventLogger.exists("[ENSEMBLE]", baseID, "STARTING_EXPERIMENT", ID)) {
				runningExperiments.add(e);
				e.restore(machines);
			
				emptyExecutionSlots.remove(e.getMachineDescription());
				
				info("  restored: " + ID + " to running queue");
			} else { 
				pendingExperiments.add(e);
				info("  restored: " + ID + " to pending queue");
			}
		}
		
		info("All experiments sucessfully restored!");
	}
	
	/*
	public void runAll(FileTransferService fileTransferService) { 
		startExecutors(fileTransferService);
		waitUntilDone(fileTransferService);
	}
	 */
	
	public void setFileTransferService(FileTransferService fileTransferService) {
		this.fileTransferService = fileTransferService;
	}

	public FileTransferService getFileTransferService() {
		return fileTransferService;
	}

	private boolean addRunningExperiments() { 
		
		boolean change = false;
		
		while (pendingExperiments.size() > 0 && emptyExecutionSlots.size() > 0) {
			Experiment exp = pendingExperiments.removeFirst();
			MachineDescription slot = emptyExecutionSlots.removeFirst();
			
			exp.setTargetMachine(slot);

			eventLogger.log("[ENSEMBLE]", baseID, "STARTING_EXPERIMENT", exp.ID);
			
			info("Starting " + exp.ID + " on " + slot.name);
			
			synchronized (this) {
				runningExperiments.addLast(exp);
//				filledExecutionSlots.addLast(slot);
			}
			
			change = true;
		} 
		
		return change;
	}
	
	private boolean pollRunningExperiments() { 

		boolean stateChange = false;
		
		for (Experiment e : runningExperiments) {
			stateChange = e.run() || stateChange;
		}
		
		return stateChange;
	}
	
	public void run() {
		
		eventLogger.log("[ENSEMBLE]", baseID, "START", "-");			
		
		info("Running ensemble \"" + baseID + "\"");
		
		while (getFinished() != size) {
			
			boolean added = addRunningExperiments();			
			boolean statesChanges = pollRunningExperiments();
			boolean stopped = processStoppedExperiments();
			
			if (!(added || statesChanges || stopped)) { 
				try { 
					//info("SLEEP");
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// ignored
				}
			}
		}
		
		eventLogger.log("[ENSEMBLE]", baseID, "DONE", "OK");			
		
		info("Finished ensemble \"" + baseID + "\"");
	}

	public void setEventLogger(EventLogger eventLogger) {
		this.eventLogger = eventLogger;
	}	
}
