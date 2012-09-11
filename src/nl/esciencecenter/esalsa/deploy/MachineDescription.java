package nl.esciencecenter.esalsa.deploy;

import java.io.File;
import java.util.HashMap;

import nl.esciencecenter.esalsa.util.Utils;

public class MachineDescription {

	public final String name;
	
	public final ResourceDescription host;
	public final ResourceDescription scheduler;
	public final ResourceDescription files;	
	public final ResourceDescription gateway;
	
	public final int slots;
	
	public final FileDescription inputDir;
	public final FileDescription templateDir;
	
	public FileDescription outputDir;
	public FileDescription experimentDir;
	
	public final FileDescription startScript;
	public final FileDescription stopScript;
	public final FileDescription monitorScript;
	public final FileDescription configTemplate;
	
	private Template template;
	
	public MachineDescription(String name, 
			ResourceDescription host, 
			ResourceDescription scheduler, 
			ResourceDescription gateway, 
			ResourceDescription files, 			
			int slots, 
			String inputDir, String outputDir, 
			String experimentDir, String templateDir) {
		
		super();
		
		this.name = name;
		this.host = host;
		this.scheduler = scheduler;
		this.gateway = gateway;
		this.files = files;
		this.slots = slots;
		
		this.inputDir = createFileDescription(inputDir, files, gateway);
		this.outputDir = createFileDescription(outputDir, files, gateway);
		this.experimentDir = createFileDescription(experimentDir, files, gateway);
		this.templateDir = createFileDescription(templateDir, files, gateway);
		
		this.startScript = Utils.getSubFile(this.templateDir, "pop_start");
		this.stopScript = Utils.getSubFile(this.templateDir, "pop_stop");
		this.monitorScript = Utils.getSubFile(this.templateDir, "pop_monitor");
		this.configTemplate = Utils.getSubFile(this.templateDir, "pop_in_template");
	}
	
	private static FileDescription createFileDescription(String path, ResourceDescription files, ResourceDescription gateway) { 
		String completeURI = files.URI + File.separator + path;
		ResourceDescription tmp = new ResourceDescription(completeURI, files.username, files.userkey, files.adaptors);
		return new FileDescription(tmp, gateway);
	}
	
	public String toString() { 
		return "Machine(" + name + ", base=" + host + " scheduler=" + scheduler + ", files=" + files + ", gateway=" + gateway 
				+ ", slots=" + slots + ", inputDir=" + inputDir + ", outputDir=" + outputDir + ", experimentDir=" + experimentDir + ", templateDir=" + templateDir;  
	}

	public void addTemplate(Template t) {
		template = t;
	}
	
	public String generateConfiguration(HashMap<String, String> variables) throws Exception {
		return template.generate(variables);
	}

	public void setExperimentDir(FileDescription experimentDir) {
		this.experimentDir = experimentDir; 
	}

	public void setOutputDir(FileDescription outputDir) {
		this.outputDir = outputDir;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MachineDescription other = (MachineDescription) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}

