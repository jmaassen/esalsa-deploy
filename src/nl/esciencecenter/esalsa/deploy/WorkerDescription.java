package nl.esciencecenter.esalsa.deploy;

import java.net.URI;
import java.util.HashMap;

public class WorkerDescription extends StoreableObject {

	private static final long serialVersionUID = -2173753719544719562L;
	
	public final URI jobServer;
	public final URI fileServer;
	
	public final String inputDir;
	public final String templateDir;	
	public final String outputDir;
	public final String experimentDir;
	
	public final String startScript;
	public final String stopScript;
	public final String monitorScript;
	
	private HashMap<String, String> values;  
	
	public WorkerDescription(String ID, 
			URI jobServer, URI fileServer, 
			String inputDir, String outputDir, String experimentDir, String templateDir,
			String startScript, String monitorScript, String stopScript, 
			String comment, 
			HashMap<String, String> values) {
		
		super(ID, comment);
	
		this.jobServer = jobServer;
		this.fileServer = fileServer;
		
		this.inputDir = inputDir;
		this.outputDir = outputDir;
		this.experimentDir = experimentDir;
		this.templateDir = templateDir;
		
		this.startScript = startScript;
		this.monitorScript = monitorScript;
		this.stopScript = stopScript;
		
		this.values = values;
	}
		
	public HashMap<String, String> getMapping() {
		return values;
	}
	
	public String toString() { 
		return "Worker(" + ID + ", host=" + jobServer + ", files=" + fileServer 
				+ ", inputDir=" + inputDir + ", outputDir=" + outputDir + ", experimentDir=" + experimentDir + ", templateDir=" + templateDir;  
	}
}

