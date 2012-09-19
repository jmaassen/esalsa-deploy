package nl.esciencecenter.esalsa.deploy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import nl.esciencecenter.esalsa.util.BulkFileTransferHandle;
import nl.esciencecenter.esalsa.util.FileTransferDescription;
import nl.esciencecenter.esalsa.util.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExperimentInstance extends MarkableObject {

	// Global logger 
	private static final Logger globalLogger = LoggerFactory.getLogger("eSalsa");
	
	// Minimum time to wait between calls to the monitor script.
	private static final int MONITOR_SLEEP = 30; 

	// The parent object;
	private final POPRunner parent;
	
	/** Unique ID for this running experiment. */
	public final String ID; 

	/** Input directory on target machine. */
	public final URI inputDir; 

	/** Template directory on target machine. */
	public final URI templateDir; 
	
	/** Experiment directory on target machine. */
	public final URI experimentDir; 

	/** Output directory on target machine. */
	public final URI outputDir; 
	
	/** StartScript in experimentDir on target machine. */
	public final URI startScript; 
	
	/** MonitorScript in experimentDir on target machine. */
	public final URI monitorScript; 
	
	/** StopScript in experimentDir on target machine. */
	public final URI stopScript; 
	
	/** The original experiment description */
	public final ExperimentDescription experiment;
	
	/** The worker on which this experiment will be run. */
	public final WorkerDescription worker; 
	
	/** The (possibly generated) pop_in configuration file.  */
	public final String configuration;
	
	/** The list of input file that must be staged in before the run. */
	public final FileSet input;
	
	/** The list of output file that must be staged out after the run. */
	public final FileSet output;
	
	// A handle for the file transfers performed at stageIn and stageOut.
	private BulkFileTransferHandle fileTransfers;
	
	// The various states that an experiment can be in.
	public enum State {
		INITIAL,
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
	
	// Current state
	private State state = State.INITIAL;
		
	// Has the state changed during the last run ?
	private boolean stateChanged = false;
		
	// Time at which the last monitorJob call was done.
	private long lastMonitorJob = 0;

	// Counter used to generate per-experiment-unique temp files.	
	private int tempFileCounter = 0;
		
	// Current JobID of remote job	
	private String jobID;
	
	// Local output directory for this experiment.	
	private final File localOutputDir;

	// Local copy of the (generated) configuration file.	
	private final File localConfig;
	
	public ExperimentInstance(String ID, ExperimentDescription experiment, POPRunner parent) throws Exception {
		
		super(ID);
		
		this.ID = ID;
		this.experiment = experiment;
		this.parent = parent;
		
		worker = parent.getWorker(experiment.worker);
		input = parent.getInputFileSet(experiment.stageIn);
		output = parent.getOutputFileSet(experiment.stageOut);
		
		localOutputDir = new File(parent.getTempDir() + File.separator + ID);
		
		if (localOutputDir.exists()) { 
			throw new Exception("Local temp dir already exists: " + localOutputDir);
		}
		
		if (!localOutputDir.mkdirs()) { 
			throw new Exception("Failed to create local temp dir: " + localOutputDir);
		}
		
		ConfigurationTemplate config = parent.getConfigurationTemplate(experiment.configuration);
		
		experimentDir = worker.fileServer.resolve(worker.experimentDir + File.separator + ID + File.separator);
		outputDir = worker.fileServer.resolve(worker.outputDir + File.separator + ID + File.separator);
		inputDir = worker.fileServer.resolve(worker.inputDir + File.separator);
		templateDir = worker.fileServer.resolve(worker.templateDir + File.separator);

		startScript = experimentDir.resolve("start.sh");
		monitorScript = experimentDir.resolve("monitor.sh");
		stopScript = experimentDir.resolve("stop.sh");
		
		HashMap<String, String> tmp = worker.getMapping();

		tmp.put("generated.runID", ID);
		tmp.put("generated.log", ID + ".log");
		tmp.put("generated.experimentDir", worker.experimentDir + File.separator + ID);
		tmp.put("generated.outputDir", worker.outputDir + File.separator + ID);
		
		configuration = config.generate(worker.getMapping());

		try {
			localConfig = new File(localOutputDir + File.separator + "pop_in");			
			BufferedWriter w = new BufferedWriter(new FileWriter(localConfig));
			w.write(configuration);
			w.close();
		} catch (Exception e) {
			throw new Exception("Failed to locally store generated config file!", e);
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
			// EventLogger.get().log("[EXPERIMENT]", ID, nextState.name(), message);
		}
	}
	
	private void stageIn() { 
		
		info("Preparing remote directories and files.");
		
		// First create the remote directories private to this experiment				
		try { 
			if (!Utils.createDir(experimentDir)) { 
				error("Failed to create remote experiment directory: " + experimentDir);
				return;
			}
		} catch (Exception e) {
			error("Failed to create remote directory: " + experimentDir, e);
			return;
		}
		
		if (!experimentDir.equals(outputDir)) { 
			try {
				if (!Utils.createDir(outputDir)) { 
					error("Failed to create remote output directory: " + outputDir);
					return;
				}
			} catch (Exception e) {
				error("Failed to create remote directory: " + outputDir, e);
				return;
			}
		}
		
		// Required input file transfers. 
		LinkedList<FileTransferDescription> inputsTransfers = new LinkedList<FileTransferDescription>();
		
//System.out.println("**** Adding input files!");		
		
		// Add all input files to the file transfer list 
		try { 	
			for (URI file : input.getFiles()) {
				URI target = inputDir.resolve(Utils.getFileName(file));
				inputsTransfers.addLast(new FileTransferDescription(file, target));

//				System.out.println("    " + file + " -> " + target);		
			} 
		} catch (Exception e) {
			error("Failed to generate transfer list for input files!", e);
			return;
		}

//System.out.println("**** Adding local config!");		
		
		// Add the local config file.
		URI source = null;
		
		try { 
			source = new URI("file://" + localConfig.getAbsolutePath());
		} catch (Exception e) {
			error("Failed create add local config file to transferlist", e);
			return;
		}
			
		URI target = experimentDir.resolve("pop_in");
		inputsTransfers.add(new FileTransferDescription(source, target));

//System.out.println("    " + source + " -> " + target);		

//System.out.println("**** Adding template dir!");		
				
		// transfers.add(new FileTransferDescription(md.templateDir, experiment.remoteExperimentDir));
		
		// Add the files in the remote experiment template dir.		
		try {
			Utils.createTransferList(templateDir, experimentDir, inputsTransfers);
		} catch (Exception e1) {
			error("Failed create transferlist for remote template directory", e1);
			return;
		}

//System.out.println("**** Done!");		
				
		info("Starting file transfer.");

		// Enqueue our list of file transfers at the file transfer service of our parent;
		fileTransfers = parent.fileTransferService.queue(inputsTransfers, true);
		
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
	
	private String getTempFileName(String name, String ext) { 
		if (ext != null) { 
			return name + "_" + tempFileCounter++ + "." + ext;
		} else { 
			return name + "_" + tempFileCounter++;
		}
	}
	
	private void submit() { 

		info("Submitting remote job.");
		
		String tmpName = getTempFileName("start", null);
		
		File stdout = new File(localOutputDir + File.separator + tmpName + ".out");
		File stderr = new File(localOutputDir + File.separator + tmpName + ".err");
		
		int exit = Utils.runRemoteScript(worker.jobServer, experimentDir.getPath(), "start.sh", 
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
		
		int exit = Utils.runRemoteScript(worker.jobServer, experimentDir.getPath(), 
				"monitor.sh", new String [] { jobID }, stdout, stderr, globalLogger, "[" + ID + "]");
		
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
	
	public boolean run() { 
		
		stateChanged = false;
		
		switch (state) {
		case INITIAL:
			stageIn();			
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
		
}
