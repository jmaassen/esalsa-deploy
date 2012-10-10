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
	
	/** The jobserver used to access the target machine */
	public final URI jobServer;

	/** The list of input files that must me copied to the target machine */
	public final URI [] inputFiles;
	
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
	
	/** POP log file in experimentDir on target machine. */
	public final URI popLog; 
	
	/** The generated pop_in configuration file. */
	public final String configuration;

	/** The last known state of the experiment */
	public String state;
	
	/** The log of the running experiment */
	public String log;
	
	public ExperimentInfo(String ID, String experimentDescriptionID, WorkerDescription worker, FileSet inputs, ConfigurationTemplate template) throws Exception {
	
		super(ID, "Experiment generated from ExperimentDescription " + experimentDescriptionID);
		
		this.experimentDescriptionID = experimentDescriptionID;
		this.workerDescriptionID = worker.ID;
		this.inputsID = inputs.ID;
		this.configurationTemplateID = template.ID;

		jobServer = worker.jobServer;
		
		experimentDir = worker.fileServer.resolve(worker.experimentDir + File.separator + ID + File.separator);
		outputDir = worker.fileServer.resolve(worker.outputDir + File.separator + ID + File.separator);
		inputDir = worker.fileServer.resolve(worker.inputDir + File.separator);
		templateDir = worker.fileServer.resolve(worker.templateDir + File.separator);

		popLog = worker.fileServer.resolve(worker.experimentDir + File.separator + ID + File.separator + ID + ".log");
		
		startScript = experimentDir.resolve("start.sh");
		monitorScript = experimentDir.resolve("monitor.sh");
		stopScript = experimentDir.resolve("stop.sh");
		
		inputFiles = inputs.getFilesAsArray();
		
		HashMap<String, String> tmp = new HashMap<String, String>();
		
		tmp.put("generated.runID", ID);
		tmp.put("generated.log", ID + ".log");
		tmp.put("generated.experimentDir", worker.experimentDir + File.separator + ID);
		tmp.put("generated.outputDir", worker.outputDir + File.separator + ID);
		
		tmp.put("worker.inputDir", worker.inputDir);
		tmp.put("worker.outputDir", worker.outputDir + File.separator + ID + File.separator);
		tmp.put("worker.experimentDir", worker.experimentDir + File.separator + ID + File.separator);
		tmp.put("worker.templateDir", worker.templateDir + File.separator);
		
		addWorkerProperties(tmp, worker.getMapping());
		
		//System.out.println("Generated hashmap " + tmp);
		
		configuration = template.generate(tmp);

		// FIXME TODO check if config is correct here!
		
		this.state = "INITIAL";
		this.log = "";
	}

	private void addWorkerProperties(HashMap<String, String> dest, HashMap<String, String> source) { 
		for (Entry<String, String> e : source.entrySet()) { 
			dest.put("worker." + e.getKey(), e.getValue());
		}
	}
	
	public void setState(String state) { 
		this.state = state;
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
		b.append(format(c.get(Calendar.MONTH), 2));
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
		log = log.concat(getTimeStamp() + " " + message + "\n");
	}
	
	public void warn(String message, Throwable e) { 
		log = log.concat(getTimeStamp() +" WARNING: " + message + "\n");
	}

	public void error(String message, Throwable e) { 
		log = log.concat(getTimeStamp() +" ERROR: " + message + "\n");
	}
	
	@Override
	public String toString() {
		return "Experiment: " + experimentDescriptionID + ", state =" + state + ", log:\n"
				+ log + "\n\n";
	}
}
