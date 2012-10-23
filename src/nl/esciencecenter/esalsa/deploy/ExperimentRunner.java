package nl.esciencecenter.esalsa.deploy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import nl.esciencecenter.esalsa.util.BulkFileTransferHandle;
import nl.esciencecenter.esalsa.util.FileTransferDescription;
import nl.esciencecenter.esalsa.util.FileTransferHandle;
import nl.esciencecenter.esalsa.util.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExperimentRunner {

	// Global logger 
	private static final Logger globalLogger = LoggerFactory.getLogger("eSalsa");
	
	// Minimum time to wait between calls to the monitor script (in seconds).
	private static final int MONITOR_SLEEP = 60;

	// Maximum time to wait for a pop log file to be retrieved (in seconds).
	private static final long MAXIMUM_LOG_RETRIEVAL_TIME = 10; 

	// The message POP prints upon termination.
	private static final String POP_TERMINATION_MESSAGE = "Successful completion of POP run";
	
	// The parent object;
	private final POPRunner parent;

	// Info, as store in database!
	private final ExperimentInfo info;
	
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
		ERROR, 
		STOPPED_BY_USER;
	}
	
	// Current state
	private State state = State.INITIAL;
		
	// Has the state changed during the last run ?
	private boolean stateChanged = false;

	// Has the log changed during the last run ?
	private boolean logChanged = false;
	
	// Time at which the last monitorJob call was done.
	private long lastMonitorJob = 0;

	// Counter used to generate per-experiment-unique temp files.	
	private int tempFileCounter = 0;
		
	// Local output directory for this experiment.	
	private final File localOutputDir;

	// Local copy of the (generated) configuration file.	
	private File localConfig;
	
	// Flag to stop this experiment when it is running
	private boolean mustStop = false;
	
	// Log describing the progress of the job;
	//private final StringBuilder log = new StringBuilder();
	
	public ExperimentRunner(ExperimentInfo info, POPRunner parent) throws Exception {
		
		this.parent = parent;
		this.info = info;
		
		this.state = State.valueOf(info.getState());
		
		if (this.state != State.INITIAL) { 
			info("Recovering job " + info.ID + ". Current state " + this.state);
		}

		localOutputDir = new File(parent.getTempDir() + File.separator + info.ID);
		
		if (localOutputDir.exists()) {
			warn("Local temp dir already exists: " + localOutputDir);
		} else if (!localOutputDir.mkdirs()) { 
			throw new Exception("Failed to create local temp dir: " + localOutputDir);
		}
		
		try {
			localConfig = new File(localOutputDir + File.separator + "pop_in");			
			BufferedWriter w = new BufferedWriter(new FileWriter(localConfig));
			w.write(info.configuration);
			w.close();
		} catch (Exception e) {
			throw new Exception("Failed to locally store generated config file!", e);
		}
	}
	
	public ExperimentInfo getInfo() {
		return info;
	}
	
	public String getID() {
		return info.ID;
	}
	
	private void info(String message) { 
		logChanged = true;
		globalLogger.info("[" + info.ID + "] " + message);
		info.info(message);
	}
	
	private void warn(String message, Throwable e) {
		logChanged = true;
		globalLogger.warn("[" + info.ID + "] " + message, e);
		info.warn(message, e);
	}

	private void warn(String message) {
		logChanged = true;
		warn(message, null);
	}
	
	private void error(String message, Throwable e) {
		logChanged = true;
		globalLogger.error("[" + info.ID + "] " + message, e);
		info.error(message, e);

		setState(State.ERROR, message + " " + e.getMessage());
	}
	
	private void error(String message) {
		logChanged = true;
		globalLogger.error("[" + info.ID + "] " + message);
		info.error(message, null);
		setState(State.ERROR, message);
	}
	
	private void setState(State nextState, String message) {
		setState(nextState, message, true);
	} 
	
	// Needs to be synchronized, as we can receive concurrent requests to read the state/message	
	private synchronized void setState(State nextState, String message, boolean log) {
	
		// This switch contains all legal state transitions in the state machine.
		switch (state) {
		case INITIAL:
			if (nextState == State.ERROR || nextState == State.STOPPED_BY_USER || nextState == State.STAGE_IN) { 
				state = nextState;
				stateChanged = true;
			} else { 
				throw new Error("Illegal state transition: " + state + " -> " + nextState);
			}
			break;
		case STAGE_IN:
			if (nextState == State.ERROR || nextState == State.STOPPED_BY_USER || nextState == State.STAGE_IN_COMPLETE) { 
				state = nextState;
				stateChanged = true;
			} else { 
				throw new Error("Illegal state transition: " + state + " -> " + nextState);
			}
			break;
		case STAGE_IN_COMPLETE:
			if (nextState == State.ERROR || nextState == State.STOPPED_BY_USER || nextState == State.SUBMITTED) { 
				state = nextState;
				stateChanged = true;
			} else { 
				throw new Error("Illegal state transition: " + state + " -> " + nextState);
			}
			break;
		case SUBMITTED:
			if (nextState == State.ERROR || nextState == State.STOPPED_BY_USER || nextState == State.RUNNING) { 
				state = nextState;
				stateChanged = true;
			} else { 
				throw new Error("Illegal state transition: " + state + " -> " + nextState);
			}
			break;
		case RUNNING:
			if (nextState == State.ERROR || nextState == State.STOPPED_BY_USER || nextState == State.STOPPED) { 
				state = nextState;
				stateChanged = true;
			} else { 
				throw new Error("Illegal state transition: " + state + " -> " + nextState);
			}
			break;
		case STOPPED:
			if (nextState == State.ERROR || nextState == State.STOPPED_BY_USER || nextState == State.STAGE_OUT || nextState == State.STAGE_IN_COMPLETE) { 
				state = nextState;
				stateChanged = true;
			} else { 
				throw new Error("Illegal state transition: " + state + " -> " + nextState);
			}
			break;
		case STAGE_OUT:
			if (nextState == State.ERROR || nextState == State.STOPPED_BY_USER || nextState == State.STAGE_OUT_COMPLETE) { 
				state = nextState;
				stateChanged = true;
			} else { 
				throw new Error("Illegal state transition: " + state + " -> " + nextState);
			}
			break;
		case STAGE_OUT_COMPLETE:
			if (nextState == State.ERROR || nextState == State.STOPPED_BY_USER || nextState == State.FINISHED) { 
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
		
		info.setState(state.name());
		info.info("Changed state to " + state.name() + ": " + message);		
	}
	
	private void stageIn() { 
		info("Preparing remote directories and files.");
		
		// First create the remote directories private to this experiment				
		try { 
			if (!Utils.createDir(info.experimentDirURI)) { 
				error("Failed to create remote experiment directory: " + info.experimentDir);
				return;
			}
		} catch (Exception e) {
			error("Failed to create remote directory: " + info.experimentDir, e);
			return;
		}
		
		if (!info.experimentDir.equals(info.outputDir)) { 
			try {
				if (!Utils.createDir(info.outputDirURI)) { 
					error("Failed to create remote output directory: " + info.outputDir);
					return;
				}
			} catch (Exception e) {
				error("Failed to create remote directory: " + info.outputDir, e);
				return;
			}
		}
		
		// Required input file transfers. 
		LinkedList<FileTransferDescription> inputsTransfers = new LinkedList<FileTransferDescription>();
		
//System.out.println("**** Adding input files!");		
		
		// Add all input files to the file transfer list 
		try { 	
			for (URI file : info.inputFiles) {
				URI target = info.inputDirURI.resolve(Utils.getFileName(file));
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
			
		URI target = info.experimentDirURI.resolve("pop_in");
		inputsTransfers.add(new FileTransferDescription(source, target));

//System.out.println("    " + source + " -> " + target);		

//System.out.println("**** Adding template dir!");		
				
		// transfers.add(new FileTransferDescription(md.templateDir, experiment.remoteExperimentDir));
		
		// Add the files in the remote experiment template dir.		
		try {
			Utils.createTransferList(info.templateDirURI, info.experimentDirURI, inputsTransfers);
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
	
	private void cancelStageIn() { 
		fileTransfers.cancel();
	}
	
	private void monitorStageIn() { 
		
		// FIXME: should interrupt file transfer! 
		
		if (!fileTransfers.isDone()) {
			return;
		}

		if (mustStop) { 
			setState(State.STOPPED_BY_USER, "-");
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
		if (!Utils.accessibleFile(info.startScriptURI)) { 
			error("Failed to find the remote start script: " + info.startScript);
			return;
		}

		if (!Utils.accessibleFile(info.stopScriptURI)) { 
			error("Failed to find the remote stop script: " + info.stopScript);
			return;
		}

		if (!Utils.accessibleFile(info.monitorScriptURI)) { 
			error("Failed to find the remote monitor script: " + info.monitorScript);
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
		
		int exit = Utils.runRemoteScript(info.jobServer, info.experimentDir, "start.sh", 
				null, stdout, stderr, globalLogger, "[" + info.ID + "]");
		
		if (exit != 0) { 
			// Should log to experiment specific log file ?
			error("Failed to start experiment " + info.ID + " (exit code = " + exit + ")");			
			return;
		}
		
		// Should log to experiment specific log file ?
		info("Reading output of start script: ");
	
		StringBuffer error = null;
		StringBuffer output = null;

		try { 
			error = Utils.readOutput(stderr, null);
			info("stderr: " + error.toString().trim());
		} catch (IOException e) {
			warn("Failed to read stderr of experiment  " + info.ID, e);
		}
		
		try { 
			output = Utils.readOutput(stdout, null);
			info("stdout: " + output.toString().trim());
		} catch (IOException e) {
			error("Failed to read stdout of experiment  "+ info.ID, e);
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
	
			String jobID = tok.nextToken();
			info.setJobID(jobID);
			setState(State.SUBMITTED, jobID);
			info("Remote job submitted succesfully.");			
			return;
			
		} else if (status.equalsIgnoreCase("ERROR")) { 
			error("Startup script returned an error: " + output);
			
		} else {  
			error("Failed to parse output of start script: " + output);
		}
	}

	private void stopJob() { 

		info("Stopping remote job.");
		
		String tmpName = getTempFileName("stop", null);
		
		File stdout = new File(localOutputDir + File.separator + tmpName + ".out");
		File stderr = new File(localOutputDir + File.separator + tmpName + ".err");
		
		String jobID = info.getJobID();
		
		if (jobID == null || jobID.length() == 0) { 
			error("Failed to retrieve JOBID of experiment  "+ info.ID);
			return;
		}
		
		int exit = Utils.runRemoteScript(info.jobServer, info.experimentDir, 
				"stop.sh", new String [] { jobID }, stdout, stderr, globalLogger, "[" + info.ID + "]");
		
		if (exit != 0) { 
			// Should log to experiment specific log file ?
			error("Failed to stop experiment " + info.ID + " (exit code = " + exit + ")");			
			return;
		}
		
		// Should log to experiment specific log file ?
		info("Reading output of stop script: ");
	
		StringBuffer error = null;
		StringBuffer output = null;

		try { 
			error = Utils.readOutput(stderr, null);
			info("stderr: " + error.toString().trim());
		} catch (IOException e) {
			warn("Failed to read stderr of experiment  " + info.ID, e);
		}
		
		try { 
			output = Utils.readOutput(stdout, null);
			info("stdout: " + output.toString().trim());
		} catch (IOException e) {
			error("Failed to read stdout of experiment  "+ info.ID, e);
			return;
		}

		// We expect one of the following output here: 
		//
		// OK <scheduler specific info>
		// ERROR <scheduler specific error message>
		StringTokenizer tok = new StringTokenizer(output.toString());
		
		if (!tok.hasMoreTokens()) { 
			error("Failed to parse output of stop script: " + output);
			return;
		}
		
		String status = tok.nextToken();
	
		if (status.equalsIgnoreCase("OK")) { 
			info("Remote job stopped succesfully.");			
		} else if (status.equalsIgnoreCase("ERROR")) { 
			error("Stop script returned an error: " + output);
		} else {  
			error("Failed to parse output of stop script: " + output);
		}
	}

	private void runMonitorScript(String tmpName) { 

		File stdout = new File(localOutputDir + File.separator + tmpName + ".out");
		File stderr = new File(localOutputDir + File.separator + tmpName + ".err");
		
		String jobID = info.getJobID();
		
		if (jobID == null || jobID.length() == 0) { 
			error("Failed to retrieve JOBID of experiment  "+ info.ID);
			return;
		}
		
		int exit = Utils.runRemoteScript(info.jobServer, info.experimentDir, 
				"monitor.sh", new String [] { jobID }, stdout, stderr, globalLogger, "[" + info.ID + "]");
		
		if (exit != 0) { 
			// Should log to experiment specific log file ?
			error("Failed to monitor experiment " + info.ID + " (JOBID=" + jobID + ", exit code = " + exit + ")");
			return;
		}
		
		// Should log to experiment specific log file ?
		info("Reading output of monitor script: ");

		StringBuffer error = null;
		StringBuffer output = null;

		try { 
			error = Utils.readOutput(stderr, null);
			info("stderr: " + error.toString().trim());
		} catch (IOException e) {
			warn("Failed to read stderr of monitor script of experiment  "+ info.ID, e);
		}
		
		try { 
			output = Utils.readOutput(stdout, null);
			info("stdout: " + output.toString().trim());
		} catch (IOException e) {
			error("Failed to read stdout of monitor script of experiment  "+ info.ID, e);
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

	private void retrieveLog(String tmpName) { 
		
		File tmpFile = new File(localOutputDir + File.separator + tmpName + ".log");
		
		FileTransferDescription ft = null;
		
		try { 
			ft = new FileTransferDescription(info.popLogURI, new URI("file://localhost/" + tmpFile.getAbsolutePath()));
		} catch (Exception e) {
			warn("Failed to create pop log URI for " + tmpFile.getAbsolutePath(), e);
			return;
		}
			
		FileTransferHandle handle = parent.fileTransferService.queue(ft);
		
		boolean done = handle.waitUntilDone(MAXIMUM_LOG_RETRIEVAL_TIME * 1000);
		
		if (!done) { 
			handle.cancel();
			warn("Failed to retrieve pop log " + info.popLogURI + " (timeout after " + MAXIMUM_LOG_RETRIEVAL_TIME + " sec.)");
			return;
		}
		
		Exception e = handle.getException();
		
		if (e != null) {
			warn("Failed to retrieve pop log " + info.popLogURI + "!", e);
			return;
		} 
		
		try { 
			StringBuffer tmp = Utils.readOutput(tmpFile, null);
				
			if (tmp != null) { 
				info.setLogPOP(tmp.toString());
			}				
		} catch (Exception ex) {
			error("Failed to read local copy of pop log " + tmpFile.getAbsolutePath() + "!", e);
			return;
		}
	}
	
	private void monitorJob() { 
		
		long time = System.currentTimeMillis();
		
		if (time <= (lastMonitorJob + MONITOR_SLEEP*1000)) {
			return;
		}
		
		lastMonitorJob = time;
		
		String tmpName = getTempFileName("monitor", null);

		runMonitorScript(tmpName);
		retrieveLog(tmpName);
	}
	
	private void moveLogFile() { 		
		
		URI from = info.popLogURI;
		URI to = null;
		
		try { 
			to = new URI(from.toString() + "." + info.getCurrentRun());
		} catch (Exception e) {
			warn("Failed to backup pop log file " + from, e);
			return;
		}
		
		try { 
			
			info("Copying pop log file from " + from + " to " + to);
			
			Utils.copy(from, to);
		} catch (Exception e) {
			warn("Failed to copy pop log file from " + from + " to " + to, e);
		}
	}
	
	private void stopped() {
		// Once out job has stopped, we need to decide if we need to resubmit, stage out, or go to error stage. 
		String popLog = info.getLogPOP();
		
		int index = popLog.indexOf(POP_TERMINATION_MESSAGE);
		
		if (index == -1) { 
			// Apparently, POP did not terminate correctly, so we go to error state
			error("POP run seems to have terminated with an error!");
			return;
		}
		
		moveLogFile();
		
		// POP terminated correctly. Check if we need to resubmit.
		boolean ok = false;
		
		try { 
			ok = info.incrementCurrentRunNumber();
		} catch (Exception e) {
			error("Failed to increment run number for POP resubmit!", e);
			return;
		}
			
		if (ok) {
			setState(State.STAGE_IN_COMPLETE, "(RESUBMIT)");
		} else { 
			info("Maximum POP resubmits reached.");
			stageOut();
		}
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
	
	public synchronized void mustStop() {
		mustStop = true;
	}
	
	private synchronized boolean getMustStop() {
		return mustStop;
	}
	
	public boolean run() { 
		
		stateChanged = false;
		logChanged = false;
		
		boolean stop = getMustStop();
		
		switch (state) {
		case INITIAL:
			if (stop) { 
				setState(State.STOPPED_BY_USER, "-");
			} else { 
				stageIn();
			}
			break;
		case STAGE_IN:
			if (stop) {
				cancelStageIn();
				setState(State.STOPPED_BY_USER, "-");
			} else { 
				monitorStageIn();
			}
			break;
		case STAGE_IN_COMPLETE:
			if (stop) { 
				setState(State.STOPPED_BY_USER, "-");
			} else { 
				submit();
			}
			break;
		case SUBMITTED:
		case RUNNING:			
			if (stop) {
				stopJob();
				setState(State.STOPPED_BY_USER, "-");
			} else { 
				monitorJob();
			}
			break;
		case STOPPED:
			stopped();
			break;
		case STAGE_OUT:
			monitorStageOut();
			break;
		case STAGE_OUT_COMPLETE:
			cleanup();
			break;			
		case FINISHED:
		case STOPPED_BY_USER:
		case ERROR:
			finish();
			break;
		}
		
		return stateChanged || logChanged;
	}

	@Override
	public int hashCode() {
		return info.ID.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExperimentRunner other = (ExperimentRunner) obj;
		return info.ID.equals(other.info.ID);
	}	
}
