package nl.esciencecenter.esalsa.deploy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.esciencecenter.esalsa.util.BulkFileTransferHandle;
import nl.esciencecenter.esalsa.util.Event;
import nl.esciencecenter.esalsa.util.EventLogger;
import nl.esciencecenter.esalsa.util.FileDescription;
import nl.esciencecenter.esalsa.util.FileTransferDescription;
import nl.esciencecenter.esalsa.util.FileTransferService;
import nl.esciencecenter.esalsa.util.Utils;

public class Experiment {
	
	// Global logger 
	private static final Logger globalLogger = LoggerFactory.getLogger("eSalsa");
	
	// Minimum time to wait between calls to the monitor script.
	private static final int MONITOR_SLEEP = 30; 

	// The various states that an experiment can be in.
	public enum State {
		INITIAL,
		TARGET_SET,
		STAGE_IN, 
		STAGE_IN_COMPLETE, 
		SUBMITTED, 
		RUNNING, 
		STOPPED, 
		STAGE_OUT,
		STAGE_OUT_COMPLETE, 
		FINISHED,
		ERROR;
	}
	
	// Parent ensemble
	private final EnsembleDescription parent;
		
	// Unique ID for the ensemble this experiment belongs to.
	public final String ensembleID; 
	
	// Unique ID for this experiment
	public final String ID; 

	// Input files as described in experiment configuration. 
	private final Map<String, FileDescription> inputsAsConfigured;

	// Output files as described in experiment configuration.
	private final Map<String, String> outputsAsConfigured;
	
	// Required input file transfers. 
	private final LinkedList<FileTransferDescription> inputsTransfers = new LinkedList<FileTransferDescription>();
		
	// The output files on the target machine.
	private final Map<String, FileDescription> outputsOnMachine = new HashMap<String, FileDescription>();
	
	// Local output directory for this experiment.	
	private final File localOutputDir; 
	
	// The machine this experiment will run on.
	private MachineDescription machine;
	
	// Experiment directory on target machine.
	private String remoteExperimentDirAsString;
	private FileDescription remoteExperimentDir;
	
	// Output directory on target machine  
	private FileDescription remoteOutputDir;
	
	// Input directory on target machine 
	// private FileDescription remoteInputDir;
		
	// Startup script on target machine (in experiment directory). 
	private FileDescription startScript;
	
	// Stop script on target machine (in experiment directory). 
	private FileDescription stopScript;
	
	// Monitor script on target machine (in experiment directory). 
	private FileDescription monitorScript;

	// Local configuration file
	private File localConfig;
	
	// Configuration file on target machine (in experiment directory)	
	private FileDescription remoteConfigurationFile;
			
	// Current submission number
//	private int count;
	
	// Current state
	private State state = State.INITIAL;
	
	// Current JobID of remote job	
	private String jobID;
	
	// Counter used to generate unique temp files.	
	private int tempFileCounter = 0;
	
	// Handle used to monitor file transfers.
	private BulkFileTransferHandle fileTransfers;
	
	// Has the state changed during the last run ?
	private boolean stateChanged = false;
		
	// Time at which the last monitorJob call was done.
	private long lastMonitorJob = 0;
	
	public Experiment(EnsembleDescription parent, String experimentID, Map<String, FileDescription> inputs, Map<String, String> outputs, File localOuput) {
		this.parent = parent;
		this.ensembleID = parent.baseID;
		this.ID = experimentID;		
		this.inputsAsConfigured = inputs;
		this.outputsAsConfigured = outputs;
		this.localOutputDir = new File(localOuput.getAbsolutePath() + File.separator + ID);
		
		//EventLogger.get().log("[EXPERIMENT]", ID, "CREATED", "OK");			
	}

	public MachineDescription getMachineDescription() {
		return machine;
	}
	
	private String getTempFileName(String name, String ext) { 
		if (ext != null) { 
			return name + "_" + tempFileCounter++ + "." + ext;
		} else { 
			return name + "_" + tempFileCounter++;
		}
	}
	
	private void info(String message) { 
		globalLogger.info("[" + ID + "] " + message);
	}
	
	private void warn(String message, Throwable e) { 
		globalLogger.warn("[" + ID + "] " + message, e);
	}

	private void warn(String message) { 
		globalLogger.warn("[" + ID + "] " + message);
	}
	
	private void error(String message, Throwable e) { 
		globalLogger.error("[" + ID + "] " + message, e);
		setState(State.ERROR, message + " " + e.getMessage());
	}
	
	private void error(String message) { 
		globalLogger.error("[" + ID + "] " + message);
		setState(State.ERROR, message);
	}

	private void setState(State nextState, String message) {
		setState(nextState, message, true);
	} 
	
	private void setState(State nextState, String message, boolean log) {
	
		// This switch contains all legal state transitions in the state machine.
		switch (state) {
		case INITIAL:
			if (nextState == State.ERROR || nextState == State.TARGET_SET) {
				state = nextState;
				stateChanged = true;
			} else { 
				throw new Error("Illegal state transition: " + state + " -> " + nextState);
			}
			break;
		case TARGET_SET:
			if (nextState == State.ERROR || nextState == State.STAGE_IN) { 
				state = nextState;
				stateChanged = true;
			} else { 
				throw new Error("Illegal state transition: " + state + " -> " + nextState);
			}
			break;
		case STAGE_IN:
			if (nextState == State.ERROR || nextState == State.STAGE_IN_COMPLETE) { 
				state = nextState;
				stateChanged = true;
			} else { 
				throw new Error("Illegal state transition: " + state + " -> " + nextState);
			}
			break;
		case STAGE_IN_COMPLETE:
			if (nextState == State.ERROR || nextState == State.SUBMITTED) { 
				state = nextState;
				stateChanged = true;
			} else { 
				throw new Error("Illegal state transition: " + state + " -> " + nextState);
			}
			break;
		case SUBMITTED:
			if (nextState == State.ERROR || nextState == State.RUNNING) { 
				state = nextState;
				stateChanged = true;
			} else { 
				throw new Error("Illegal state transition: " + state + " -> " + nextState);
			}
			break;
		case RUNNING:
			if (nextState == State.ERROR || nextState == State.STOPPED) { 
				state = nextState;
				stateChanged = true;
			} else { 
				throw new Error("Illegal state transition: " + state + " -> " + nextState);
			}
			break;
		case STOPPED:
			if (nextState == State.ERROR || nextState == State.STAGE_OUT) { 
				state = nextState;
				stateChanged = true;
			} else { 
				throw new Error("Illegal state transition: " + state + " -> " + nextState);
			}
			break;
		case STAGE_OUT:
			if (nextState == State.ERROR || nextState == State.STAGE_OUT_COMPLETE) { 
				state = nextState;
				stateChanged = true;
			} else { 
				throw new Error("Illegal state transition: " + state + " -> " + nextState);
			}
			break;
		case STAGE_OUT_COMPLETE:
			if (nextState == State.ERROR || nextState == State.FINISHED) { 
				state = nextState;
				stateChanged = true;
			} else { 
				throw new Error("Illegal state transition: " + state + " -> " + nextState);
			}
			break;

		case ERROR:
			if (nextState != State.ERROR) { 
				throw new Error("Illegal state transition: " + state + " -> " + nextState);
			}
			break;
			
		case FINISHED:
			throw new Error("Illegal state transition: " + state + " -> " + nextState);
		}
		
		if (log) { 
			EventLogger.get().log("[EXPERIMENT]", ID, nextState.name(), message);
		}
	}

	public void restoreTargetMachine(MachineDescription md) {
		
		info("Restoring target machine to: " + md.name);

		// Save the target machine, and all variables that depend on it.  
		machine = md;

		// The machines input dir is used directly. It is used to cache the input 
		// files and can be shared between multiple runs of the ensemble!
		// remoteInputDir = machine.inputDir;
				
		// The output and experiment directories are unique for each experiment. We generate
		// their names here.
		remoteExperimentDir = Utils.getSubFile(machine.experimentDir, ID);
		remoteOutputDir = Utils.getSubFile(machine.outputDir, ID);

		try { 
			remoteExperimentDirAsString = Utils.getPath(remoteExperimentDir);
		} catch (Exception e) {
			error("Failed to retrieve remote experiment path");
			return;
		} 
		
		// The remote configuration file and the start, stop and monitor scripts 
		// reside in the remote experiment dir. Create a file description for each.
		remoteConfigurationFile = Utils.getSubFile(remoteExperimentDir, "pop_in");
		startScript = Utils.getSubFile(remoteExperimentDir, "pop_start");
		stopScript = Utils.getSubFile(remoteExperimentDir, "pop_stop");
		monitorScript = Utils.getSubFile(remoteExperimentDir, "pop_monitor");

		// Generate a machine dependent location description for all output files. -- FIXME do we need this ? 		
		try { 
			for (Entry<String,String> e : outputsAsConfigured.entrySet()) { 
				outputsOnMachine.put(e.getKey(), mapOutputToMachine(e.getValue(), machine));
			} 
		} catch (Exception e) {
			error("Failed to generate remote output file list!", e);
			return;
		}

		localConfig = new File(localOutputDir + File.separator + "pop_in");			
	}
	
	public void setTargetMachine(MachineDescription md) {
		
		info("Setting target machine to: " + md.name);

		// Save the target machine, and all variables that depend on it.  
		machine = md;

		// The machines input dir is used directly. It is used to cache the input 
		// files and can be shared between multiple runs of the ensemble!
		// remoteInputDir = machine.inputDir;
				
		// The output and experiment directories are unique for each experiment. We generate
		// their names here.
		remoteExperimentDir = Utils.getSubFile(machine.experimentDir, ID);
		remoteOutputDir = Utils.getSubFile(machine.outputDir, ID);

		try { 
			remoteExperimentDirAsString = Utils.getPath(remoteExperimentDir);
		} catch (Exception e) {
			error("Failed to retrieve remote experiment path");
			return;
		} 
		
		// The remote configuration file and the start, stop and monitor scripts 
		// reside in the remote experiment dir. Create a file description for each.
		remoteConfigurationFile = Utils.getSubFile(remoteExperimentDir, "pop_in");
		startScript = Utils.getSubFile(remoteExperimentDir, "pop_start");
		stopScript = Utils.getSubFile(remoteExperimentDir, "pop_stop");
		monitorScript = Utils.getSubFile(remoteExperimentDir, "pop_monitor");

		// Generate a machine dependent location description for all output files. -- FIXME do we need this ? 		
		try { 
			for (Entry<String,String> e : outputsAsConfigured.entrySet()) { 
				outputsOnMachine.put(e.getKey(), mapOutputToMachine(e.getValue(), machine));
			} 
		} catch (Exception e) {
			error("Failed to generate remote output file list!", e);
			return;
		}

		// Try to generate a machine specific "pop_in" configuration. 
		String configuration = null;
		
		try { 
			configuration = machine.generateConfiguration(generateMachineMapping(machine, ID));
		} catch (Exception e) {
			error("Failed to generate configuration file \"pop_in\"!", e);
			return;
		}
		
		// Create the local output directory, and write the configuration file
		try {
			if (!localOutputDir.mkdirs()) {
				error("Failed create local output directory " + localOutputDir.getAbsolutePath());
				return;
			}
			
			localConfig = new File(localOutputDir + File.separator + "pop_in");			
			BufferedWriter w = new BufferedWriter(new FileWriter(localConfig));
			w.write(configuration);
			w.close();
		} catch (Exception e) {
			error("Failed to store generated config file!", e);
			return;
		}
		
		setState(State.TARGET_SET, md.name);
	}
	
	private void stagein() {

		info("Preparing remote directories and files.");
		
		// First create the remote directories private to this experiment				
		try { 
			if (!Utils.createDir(remoteExperimentDir)) { 
				error("Failed to create remote experiment directory: " + remoteExperimentDir);
				return;
			}
			
			if (!Utils.createDir(remoteOutputDir)) { 
				error("Failed to create remote output directory: " + remoteOutputDir);
				return;
			}
			
		} catch (Exception e) {
			error("Failed to create remote directories!", e);
			return;
		}
		
		// Add all input files to the file transfer list 
		try { 	
			for (Entry<String,FileDescription> e : inputsAsConfigured.entrySet()) { 
				inputsTransfers.addLast(new FileTransferDescription(e.getValue(), mapInputToMachine(e.getValue(), machine)));
			} 
		} catch (Exception e) {
			error("Failed to generate transfer list for input files!", e);
			return;
		}
		
		// Add the local config file.
		FileDescription config = new FileDescription(new ResourceDescription("local:///" + localConfig.getAbsolutePath()), null); 
		inputsTransfers.add(new FileTransferDescription(config, remoteConfigurationFile));
		
		// transfers.add(new FileTransferDescription(md.templateDir, experiment.remoteExperimentDir));
		
		// Add the files in the remote experiment template dir.		
		try {
			Utils.createTransferList(machine.templateDir, remoteExperimentDir, inputsTransfers);
		} catch (Exception e1) {
			error("Failed create transferlist for remote template directory", e1);
			return;
		}

		info("Starting file transfer.");

		// Retrieve the file transfer service from our parent.
		FileTransferService fileTransferService = parent.getFileTransferService();
		
		// Enqueue our list of file transfers. 
		fileTransfers = fileTransferService.queue(inputsTransfers, true);
		
		setState(State.STAGE_IN, "-");
	}
		
	private void monitorStageIn() { 
	
		if (!fileTransfers.isDone()) {
			return;
		}
			
		info("File transfer completed.");
		
		List<Exception> exceptions = fileTransfers.getExceptions();
		fileTransfers = null;
		
		if (exceptions.size() != 0) { 
			for (Exception e : exceptions) {
				error("Failed to copy input file!", e);				
			}

			return;
		}
	
		// Check if the essential files are present in the experiment directory, 
		// as can be expected after the file transfer.
		if (!Utils.accessibleFile(startScript)) { 
			error("Failed to find the remote start script: " + startScript);
			return;
		}

		if (!Utils.accessibleFile(stopScript)) { 
			error("Failed to find the remote stop script: " + stopScript);
			return;
		}

		if (!Utils.accessibleFile(monitorScript)) { 
			error("Failed to find the remote monitor script: " + monitorScript);
			return;
		}

		setState(State.STAGE_IN_COMPLETE, "-");
		
		info("Remote directories and files are ready.");		
	}

	private void submit() {
		
		info("Submitting remote job.");
		
		String tmpName = getTempFileName("start", null);
		
		File stdout = new File(localOutputDir + File.separator + tmpName + ".out");
		File stderr = new File(localOutputDir + File.separator + tmpName + ".err");
		
		int exit = Utils.runRemoteScript(machine.host, machine.gateway, remoteExperimentDirAsString, "pop_start", 
				null, stdout, stderr, globalLogger, "[" + ID + "]");
		
		if (exit != 0) { 
			// Should log to experiment specific log file ?
			error("Failed to start experiment " + ID + " (exit code = " + exit + ")");			
			return;
		}
		
		// Should log to experiment specific log file ?
		info("Reading output of start script: ");
	
		StringBuffer error = null;
		StringBuffer output = null;

		try { 
			error = Utils.readOutput(stderr, null);
			info("stderr: " + error.toString());
		} catch (IOException e) {
			warn("Failed to read stderr of experiment  " + ID, e);
		}
		
		try { 
			output = Utils.readOutput(stdout, null);
			info("stdout: " + output.toString());
		} catch (IOException e) {
			error("Failed to read stdout of experiment  "+ ID, e);
			return;
		}

		// We expect one of the following output here: 
		//
		// OK JOBID <scheduler specific info>
		// ERROR <scheduler specific error message>
		StringTokenizer tok = new StringTokenizer(output.toString());
		
		if (!tok.hasMoreTokens()) { 
			error("Failed to parse output of start script: " + output);
			return;
		}
		
		String status = tok.nextToken();
	
		if (status.equalsIgnoreCase("OK")) { 

			if (!tok.hasMoreTokens()) { 
				error("Failed to parse output of start script: " + output);
				return;
			}
	
			jobID = tok.nextToken();
			setState(State.SUBMITTED, jobID);		
			info("Remote job submitted succesfully.");			
			return;
			
		} else if (status.equalsIgnoreCase("ERROR")) { 
			error("Startup script returned an error: " + output);
			
		} else {  
			error("Failed to parse output of start script: " + output);
		}
	}
		
	private void monitorJob() {
		
		long time = System.currentTimeMillis();
		
		if (time <= (lastMonitorJob + MONITOR_SLEEP*1000)) {
			return;
		}
		
		lastMonitorJob = time;
		
		String tmpName = getTempFileName("monitor", null);
		
		File stdout = new File(localOutputDir + File.separator + tmpName + ".out");
		File stderr = new File(localOutputDir + File.separator + tmpName + ".err");
		
		int exit = Utils.runRemoteScript(machine.host, machine.gateway, remoteExperimentDirAsString, 
				"pop_monitor", new String [] { jobID }, stdout, stderr, globalLogger, "[" + ID + "]");
		
		if (exit != 0) { 
			// Should log to experiment specific log file ?
			error("Failed to monitor experiment " + ID + " (JOBID=" + jobID + ", exit code = " + exit + ")");
			return;
		}
		
		// Should log to experiment specific log file ?
		info("Reading output of monitor script: ");

		StringBuffer error = null;
		StringBuffer output = null;

		try { 
			error = Utils.readOutput(stderr, null);
			info("stderr: " + error.toString());
		} catch (IOException e) {
			warn("Failed to read stderr of monitor script of experiment  "+ ID, e);
		}
		
		try { 
			output = Utils.readOutput(stdout, null);
			info("stdout: " + output.toString());
		} catch (IOException e) {
			error("Failed to read stdout of monitor script of experiment  "+ ID, e);
			return;
		}
		
		// We expect one of the following here: 
		//
		// OK PENDING <scheduler specific info>
		// OK RUNNING <scheduler specific info>
		// OK SUSPENDED <scheduler specific info>
		// OK ERROR <scheduler specific info>
		// OK DELETED <scheduler specific info>
		// OK MISSING <scheduler specific info>
		// ERROR <scheduler specific info>

		StringTokenizer tok = new StringTokenizer(output.toString());
		
		if (!tok.hasMoreTokens()) { 
			error("Failed to parse output of monitor script: " + output);
			return;
		}
		
		String status = tok.nextToken();
	
		if (status.equalsIgnoreCase("OK")) { 

			if (!tok.hasMoreTokens()) { 
				error("Failed to parse output of monitor script: " + output);
				return;
			}
			
			String tmp = tok.nextToken();
			
			if (tmp.equalsIgnoreCase("RUNNING")) { 
				if (state != State.RUNNING) { 
					setState(State.RUNNING, "-");
				}
				return;
			} 
			
			if (tmp.equalsIgnoreCase("PENDING") || tmp.equalsIgnoreCase("SUSPENDED")) {
				if (state != State.SUBMITTED) { 
					setState(State.SUBMITTED, "-");
				}
				return;
			}
			
			if (tmp.equalsIgnoreCase("DELETED")) { 
				setState(State.STOPPED, "-");
				return;
			}
			
			if (tmp.equalsIgnoreCase("ERROR")) { 
				setState(State.ERROR, "-");
				return;
			}

			if (tmp.equalsIgnoreCase("MISSING")) { 
				// JobID is not / no longer in queue. Depending on previous state, this may be normal..
				
				if (state == State.STOPPED) {					
					return;
				}
				
				if (state == State.RUNNING || state == State.SUBMITTED) { 
					setState(State.STOPPED, "-");
					return;
				}
				
				error("Failed to find remote job " + jobID + " during monitoring!");
				return;
			}
			
			error("Failed to parse output of monitor script: " + output);
			return;
		}
	
		if (status.equalsIgnoreCase("ERROR")) { 
			error("Monitor script produced an error: " + output);
			return;
		}
		
		error("Failed to parse output of monitor script: " + output);
		return;
	}
	
	private void stop() {

		info("Stopping remote job.");
		
		String tmpName = getTempFileName("stop", null);
		
		File stdout = new File(localOutputDir + File.separator + tmpName + ".out");
		File stderr = new File(localOutputDir + File.separator + tmpName + ".err");
		
		int exit = Utils.runRemoteScript(machine.host, machine.gateway, remoteExperimentDirAsString, 
				"pop_stop", new String [] { jobID }, stdout, stderr, globalLogger, "[" + ID + "]");
		
		if (exit != 0) { 
			// Should log to experiment specific log file ?
			error("Failed to stop experiment " + ID + " (JOBID=" + jobID + ", exit code = " + exit + ")");
			return;
		}

		info("Reading output of stop script: ");

		StringBuffer error = null;
		StringBuffer output = null;

		try { 
			error = Utils.readOutput(stderr, null);
			info("stderr: " + error.toString());
		} catch (IOException e) {
			warn("Failed to read stderr of stop script of experiment  "+ ID, e);
		}
		
		try { 
			output = Utils.readOutput(stdout, null);
			info("stdout: " + output.toString());
		} catch (IOException e) {
			error("Failed to read stdout of stop script of experiment  "+ ID, e);
		}

		// We don't care about the output here ?
		setState(State.STOPPED, "-");
	}
	
	private HashMap<String, String> generateMachineMapping(MachineDescription md, String ID) throws Exception { 
		
		HashMap<String, String> variables = new HashMap<String, String>();		
		
		for (String s : EnsembleDescription.inputFiles) { 
		
			FileDescription fd = inputsAsConfigured.get(s);
			
			if (fd == null) { 
				throw new Exception("Failed to find value for input file variable: " + s);
			}

			variables.put(s, Utils.getPath(md.inputDir.file.URI) + File.separator + Utils.getFileName(fd.file.URI));
		} 

		for (String s : EnsembleDescription.outputFiles) { 
			
			String name = outputsAsConfigured.get(s);
			
			if (name == null) { 
				throw new Exception("Failed to find value for output file variable: " + s);
			}

			variables.put(s, Utils.getPath(md.outputDir.file.URI) + File.separator + ID + File.separator + name);
		} 
		
		// variables.put("site.cores", "" + md.cores);		
		variables.put("generated.runID", "run." + ensembleID + "." + ID);
		variables.put("generated.log", Utils.getPath(md.experimentDir.file.URI) + File.separator + ID + File.separator + ID + ".log");

		return variables;
	}

	
	private FileDescription mapInputToMachine(FileDescription source, MachineDescription machine) throws Exception {		
		String URI = machine.inputDir.file.URI + File.separator + Utils.getFileName(source.file.URI); 		
		ResourceDescription r = new ResourceDescription(URI, machine.inputDir.file);  
		return new FileDescription(r, machine.inputDir.gateway);
	}
	
	private FileDescription mapOutputToMachine(String filename, MachineDescription machine) throws Exception {		
		String URI = machine.outputDir.file.URI + File.separator + filename; 		
		ResourceDescription r = new ResourceDescription(URI, machine.outputDir.file);  
		return new FileDescription(r, machine.outputDir.gateway);
	}
	
	private void stageOut() {
		warn("stageOut not implemented yet!");
		setState(State.STAGE_OUT, "-");
	}
	
	private void monitorStageOut() {
		warn("monitorStageOut not implemented yet!");
		setState(State.STAGE_OUT_COMPLETE, "-");
	}
	
	private void cleanup() {
		warn("cleanup not implemented yet!");
		setState(State.FINISHED, "-");
	}
	
	private void finish() {
		parent.finishedExperiment(this);
	}

	private MachineDescription findTarget(List<MachineDescription> machines, String target) { 
		
		for (MachineDescription m : machines) { 
			if (m.name.equals(target)) { 
				return m;
			}
		}
		
		return null;	
	}

	public void restore(List<MachineDescription> machines) {

		EventLogger eventLogger = EventLogger.get();
		
		LinkedList<Event> events = eventLogger.findAll("[EXPERIMENT]", ID, null, null);
		
		// Traverse the list of events to restore our state.
		
		System.out.println("Events retrieved: " + events);
		
		for (Event e : events) { 
			
			if (e.match("[EXPERIMENT]", ID, "TARGET_SET", null)) { 
				MachineDescription md = findTarget(machines, e.message);
				restoreTargetMachine(md);
				setState(State.TARGET_SET, "-", false);
			} else if (e.match("[EXPERIMENT]", ID, "STAGE_IN", null)) {
				setState(State.STAGE_IN, "-", false);
			} else if (e.match("[EXPERIMENT]", ID, "STAGE_IN_COMPLETE", null)) { 
				setState(State.STAGE_IN_COMPLETE, "-", false);
			} else if (e.match("[EXPERIMENT]", ID, "SUBMITTED", null)) { 
				jobID = e.message;
				setState(State.SUBMITTED, e.message, false);
			} else if (e.match("[EXPERIMENT]", ID, "RUNNING", null)) {
				setState(State.RUNNING, "-", false);
			} else if (e.match("[EXPERIMENT]", ID, "STOPPED", null)) {
				setState(State.STOPPED, "-", false);
			} else if (e.match("[EXPERIMENT]", ID, "STAGE_OUT", null)) {
				setState(State.STAGE_OUT, "-", false);
			} else if (e.match("[EXPERIMENT]", ID, "STAGE_OUT_COMPLETE", null)) {
				setState(State.STAGE_OUT_COMPLETE, "-", false);
			} else if (e.match("[EXPERIMENT]", ID, "FINISHED", null)) {
				setState(State.FINISHED, "-", false);
			}
		}
	}
	
	public boolean run() { 
		
		//info("IN RUN");
		
		stateChanged = false;
		
		switch (state) {
		case TARGET_SET:
			stagein();			
		case STAGE_IN:
			monitorStageIn();
			break;
		case STAGE_IN_COMPLETE:
			submit();
			break;
		case SUBMITTED:
		case RUNNING:
			monitorJob();
			break;
		case STOPPED:
			stageOut();
			break;
		case STAGE_OUT:
			monitorStageOut();
			break;
		case STAGE_OUT_COMPLETE:
			cleanup();
			break;			
		case FINISHED:
		case ERROR:
			finish();
			break;
		}
		
		return stateChanged;
	}

	// Generated code
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ID == null) ? 0 : ID.hashCode());
		result = prime * result
				+ ((ensembleID == null) ? 0 : ensembleID.hashCode());
		return result;
	}

	// Generated code
	@Override
	public boolean equals(Object obj) {
		if (this == obj) 
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Experiment other = (Experiment) obj;
		if (ID == null) {
			if (other.ID != null)
				return false;
		} else if (!ID.equals(other.ID))
			return false;
		if (ensembleID == null) {
			if (other.ensembleID != null)
				return false;
		} else if (!ensembleID.equals(other.ensembleID))
			return false;
		return true;
	}

	
}
