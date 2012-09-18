package nl.esciencecenter.esalsa.deploy;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

public class WorkerDescription extends MarkableObject {

	public final URI jobServer;
	public final URI fileServer;
	
	public final String inputDir;
	public final String templateDir;	
	public final String outputDir;
	public final String experimentDir;
	
	/*
	public final String startScript;
	public final String stopScript;
	public final String monitorScript;
	*/
	
	private HashMap<String, String> values;  
	
	public WorkerDescription(String ID, 
			/*ResourceDescription host, ResourceDescription files, ResourceDescription gateway,*/
			URI jobServer, URI fileServer, 
			String inputDir, String outputDir, String experimentDir, String templateDir, 
			HashMap<String, String> values) {
		
		super(ID);
	
		this.jobServer = jobServer;
		this.fileServer = fileServer;
		
		this.inputDir = inputDir;
		this.outputDir = outputDir;
		this.experimentDir = experimentDir;
		this.templateDir = templateDir;

		/*
		startScript = templateDir + File.separator + "start"; 
		stopScript = templateDir + File.separator + "stop"; 
		monitorScript = templateDir + File.separator + "monitor"; 
		*/
		
		this.values = values;
	}
	
	/*
	public URI getTemplateDirURI() throws URISyntaxException { 
		return new URI(fileServer + templateDir);
	}

	public URI getInputDirURI() throws URISyntaxException { 
		return new URI(fileServer + inputDir);
	}
	
	public URI getOutputDirURI() throws URISyntaxException { 
		return new URI(fileServer + outputDir);
	}
	
	public URI getExperimentDirURI() throws URISyntaxException { 
		return new URI(fileServer + outputDir);
	}
	*/
	
	public HashMap<String, String> getMapping() { 
		return values;
	}
	
	public String toString() { 
		return "Machine(" + ID + ", host=" + jobServer + ", files=" + fileServer 
				+ ", inputDir=" + inputDir + ", outputDir=" + outputDir + ", experimentDir=" + experimentDir + ", templateDir=" + templateDir;  
	}
}

