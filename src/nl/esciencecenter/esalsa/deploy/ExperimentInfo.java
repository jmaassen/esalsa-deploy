package nl.esciencecenter.esalsa.deploy;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map.Entry;

public class ExperimentInfo extends StoreableObject implements Serializable {

	private static final long serialVersionUID = 5478689255341274192L;

	/** The ID of the experiment description used to create this experiment */
	public final String experimentDescriptionID;
	
	/** The ID of the worker description used to create this experiment */
	public final String workerDescriptionID;
	
	/** The ID of the input set description used to create this experiment */
	public final String inputsID;
	
	/** The ID of the configuration template used to create this experiment */
	public final String configurationTemplateID;
	
	/** The number of times pop needs to be restarted. */
	public final int totalRestarts;
	
	/** The jobserver used to access the target machine */
	public final URI jobServer;

	/** The file server used to access the files on the target machine */
	public final URI fileServer;
	
	/** The list of input files that must me copied to the target machine */
	public final URI [] inputFiles;

	/** Input directory on target machine. */
	public final String inputDir; 

	/** Template directory on target machine. */
	public final String templateDir; 
	
	/** Experiment directory on target machine. */
	public final String experimentDir; 

	/** Output directory on target machine. */
	public final String outputDir; 

	/** StartScript in experimentDir on target machine. */
	public final String startScript; 
	
	/** MonitorScript in experimentDir on target machine. */
	public final String monitorScript; 
	
	/** StopScript in experimentDir on target machine. */
	public final String stopScript; 
	
	/** Name of pop log file in experimentDir on target machine. */
	public final String popLogFile; 
	
	/** Input directory on target machine as URI. */
	public final URI inputDirURI; 

	/** Template directory on target machine as URI. */
	public final URI templateDirURI; 
	
	/** Experiment directory on target machine as URI. */
	public final URI experimentDirURI; 

	/** Output directory on target machine as URI. */
	public final URI outputDirURI; 
	
	/** StartScript in experimentDir on target machine as URI. */
	public final URI startScriptURI; 
	
	/** MonitorScript in experimentDir on target machine as URI. */
	public final URI monitorScriptURI; 
	
	/** StopScript in experimentDir on target machine as URI. */
	public final URI stopScriptURI; 

	/** The generated pop_in configuration file. */
	public final String configuration;
		
	/** POP log file in experimentDir on target machine. */
	public final URI popLogURI; 
	
	/** The last known state of the experiment */
	private String state;
	
	/** The log of the running experiment */
	private String logDeploy;
	
	/** The log of the total experiment */
	private String logPOPTotal = "";
	
	/** The log of the currently running experiment */
	private String logPOPcurrent;
	
	/** The current run number (1 .. totalRestarts) */
	private int currentRun = 1;
	
	/** The current JOB ID generated pop_in configuration file. */
	private String jobID;
	
	/** A time stamp for the last update. */
	private String lastTimeStamp;;
	
	public ExperimentInfo(String ID, String experimentDescriptionID, int totalRestarts, 
			WorkerDescription worker, FileSet inputs, ConfigurationTemplate template) throws Exception {
	
		super(ID, "Experiment generated from ExperimentDescription " + experimentDescriptionID);
		
		this.experimentDescriptionID = experimentDescriptionID;
		this.totalRestarts = totalRestarts;
		this.workerDescriptionID = worker.ID;
		this.inputsID = inputs.ID;
		this.configurationTemplateID = template.ID;

		// Copy all relevant info from the worker.
		jobServer = worker.jobServer;
		fileServer = worker.fileServer;
		
		experimentDir = worker.experimentDir + File.separator + ID;
		outputDir = worker.outputDir + File.separator + ID;
		inputDir = worker.inputDir;
		templateDir = worker.templateDir;

		startScript = worker.startScript;
		monitorScript = worker.monitorScript;
		stopScript = worker.stopScript;

		// Resolve the various URIs	
		experimentDirURI = fileServer.resolve(experimentDir + File.separator);
		outputDirURI = fileServer.resolve(outputDir + File.separator);
		inputDirURI = fileServer.resolve(inputDir + File.separator);
		templateDirURI = fileServer.resolve(templateDir + File.separator);

		startScriptURI = experimentDirURI.resolve(startScript);
		monitorScriptURI = experimentDirURI.resolve(monitorScript);
		stopScriptURI = experimentDirURI.resolve(stopScript);

		// Generate a name for the log produced by pop file.  	
		popLogFile = ID + ".log";
		popLogURI = fileServer.resolve(experimentDir + File.separator + popLogFile);

		// Retrieve a list of input files.
		inputFiles = inputs.getFilesAsArray();

		// Generate the configuration by expanding the variables in the template.
		HashMap<String, String> tmp = new HashMap<String, String>();
		
		tmp.put("generated.runID", ID);
		tmp.put("generated.log", ID + ".log");
		
		tmp.put("generated.experimentDir", experimentDir);
		tmp.put("generated.outputDir", outputDir);		
		
		tmp.put("worker.inputDir", inputDir);
		
		//tmp.put("worker.outputDir", worker.outputDir + File.separator + ID + File.separator);
		//tmp.put("worker.experimentDir", worker.experimentDir + File.separator + ID + File.separator);
		//tmp.put("worker.templateDir", worker.templateDir + File.separator);
		
		addWorkerProperties(tmp, worker.getMapping());
		
		configuration = template.generate(tmp);

		this.state = "INITIAL";
		this.logDeploy = "";
		this.lastTimeStamp = getTimeStamp();		
	}

	private void addWorkerProperties(HashMap<String, String> dest, HashMap<String, String> source) { 
		for (Entry<String, String> e : source.entrySet()) { 
			dest.put("worker." + e.getKey(), e.getValue());
		}
	}

	// FIXME? 
	public boolean incrementCurrentRunNumber() throws Exception {
		
		if (currentRun < totalRestarts) {
			
			if (logPOPcurrent != null) { 
				logPOPTotal = logPOPTotal.concat("\n------ LOG OF RUN " + currentRun + "------ \n"); 
				logPOPTotal = logPOPTotal.concat(logPOPcurrent);
			}

			currentRun++;
			logPOPcurrent = null;
			return true;
		} 
		
		return false;		
	}

	
	private String format(int number, int pos) { 
		
		String tmp = "" + number;
		
		while (tmp.length() < pos) { 
			tmp = "0" + tmp;
		}

		return tmp;
	}
	
	private String getTimeStamp() { 
		
		Calendar c = Calendar.getInstance();
		
		StringBuilder b = new StringBuilder();
		b.append(c.get(Calendar.YEAR));
		b.append("-");
		b.append(format(c.get(Calendar.MONTH)+1, 2));
		b.append("-");
		b.append(format(c.get(Calendar.DAY_OF_MONTH), 2));
		b.append(" ");
		b.append(format(c.get(Calendar.HOUR_OF_DAY), 2));
		b.append(":");
		b.append(format(c.get(Calendar.MINUTE), 2));
		b.append(":");
		b.append(format(c.get(Calendar.SECOND), 2));
		return b.toString();
		
	}
	
	public void info(String message) {
		lastTimeStamp = getTimeStamp();
		logDeploy = logDeploy.concat(lastTimeStamp + " " + message + "\n");
	}
	
	public void warn(String message, Throwable e) {
		lastTimeStamp = getTimeStamp();
		logDeploy = logDeploy.concat(lastTimeStamp + " WARNING: " + message + "\n");
	}

	public void error(String message, Throwable e) {
		lastTimeStamp = getTimeStamp();
		logDeploy = logDeploy.concat(lastTimeStamp +" ERROR: " + message + "\n");
	}
	
	public void setLogPOP(String log) {
		lastTimeStamp = getTimeStamp();
		logPOPcurrent = "\n------ LOG OF RUN " + currentRun + "------ \n" + log;
	}
	
	public String getLogPOP() {
		
		StringBuilder tmp = new StringBuilder("");
		
		if (logPOPTotal != null) { 
			tmp.append(logPOPTotal);
		}
		
		if (logPOPcurrent != null) { 
			tmp.append(logPOPcurrent);
		}
		
		return tmp.toString();		
	}
	
	public void setJobID(String jobID) {
		lastTimeStamp = getTimeStamp();
		this.jobID = jobID;
	}
	
	public String getJobID() {
		return jobID;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		lastTimeStamp = getTimeStamp();
		this.state = state;
	}	

	/*	
	public URI getPOPLogURI() {
		// TODO Auto-generated method stub
		return null;
	}
	 */
	
	public int getCurrentRun() {
		return currentRun;
	}

	public String getDeployLog() {
		return logDeploy;
	}

	public String getLastUpdate() {
		return lastTimeStamp;
	}
	
	@Override
	public String toString() {
		return "Experiment: " + experimentDescriptionID + ", state =" + state + ", log:\n"
				+ logDeploy + "\n\n";
	}
}
