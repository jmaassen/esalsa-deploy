package nl.esciencecenter.esalsa.deploy;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import nl.esciencecenter.esalsa.deploy.parser.DeployProperties;
import nl.esciencecenter.esalsa.util.FileDescription;
import nl.esciencecenter.esalsa.util.Utils;

public class WorkerDescription extends MarkableObject {

	public final ResourceDescription host;
	//public final ResourceDescription scheduler;
	public final ResourceDescription files;	
	public final ResourceDescription gateway;
	
	//public final int slots;
	
	public final String inputDir;
	public final String templateDir;
	
	public String outputDir;
	public String experimentDir;
	
	public final FileDescription startScript;
	public final FileDescription stopScript;
	public final FileDescription monitorScript;
	//public final FileDescription configTemplate;
	
	private ConfigurationTemplate template;

	private HashMap<String, String> values = new HashMap<String, String>(); 
	
	public WorkerDescription(String ID, DeployProperties p) { 
	
		super(ID);

		host = loadResourceDescription("job", p);
		files = loadResourceDescription("files", p);

		if (p.containsKey("gateway")) { 
			gateway = loadResourceDescription("gateway", p);
		} else { 
			gateway = null;
		}

		inputDir = getProperty(p, "inputDir");
		outputDir = getProperty(p, "outputDir");
		experimentDir = getProperty(p, "experimentDir");
		templateDir = getProperty(p, "templateDir");

		for (Entry<Object, Object> entry : p.entrySet()) { 
			
			if (!(entry.getKey() instanceof String && entry.getValue() instanceof String)) { 
				throw new Exception("Failed to parse worker desciption " + name);
			} 

			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			
			values.put("worker." + key, value);
		}
	}

	public HashMap<String, String> getMapping() { 
		return values;
	}
	
	private String getProperty(DeployProperties properties, String key) throws Exception { 
		
		String tmp = properties.getProperty(key, null);
		
		if (tmp == null) {
			throw new Exception("Missing or invalid field: " + key);
		}
		
		return tmp;
	}
	
	
	private ResourceDescription loadResourceDescription(String base, DeployProperties machines) throws Exception { 
		
		String URI = machines.getProperty(base + ".uri");
		
		if (URI == null) { 
			throw new Exception("No " + base + ".uri defined!");
		}
		
		String name = machines.getProperty(base + ".user.name", null);
		String key = machines.getProperty(base + ".user.key", null);
		String [] adaptors = machines.getStringList(base + ".adaptors", ",", null);
		
		return new ResourceDescription(URI, name, key, adaptors);
	}
	
	
		
		String basename = base + "." + resource;
		
		//int cores = getIntProperty(properties, basename + ".cores"); 
		int slots = getIntProperty(properties, basename + ".slots");
		
		
		return new MachineDescription(base + "." + resource, /*support,*/ job, gateway, files, /*slots,*/ inputDir, outputDir, experimentDir, templateDir);
	}
	
	
	
	public WorkerDescription(String name, 
			ResourceDescription host, 
			//ResourceDescription scheduler, 
			ResourceDescription gateway, 
			ResourceDescription files, 			
			//int slots, 
			String inputDir, String outputDir, 
			String experimentDir, String templateDir) {
		
		super();
		
		this.name = name;
		this.host = host;
		//this.scheduler = scheduler;
		this.gateway = gateway;
		this.files = files;
		//this.slots = slots;
		
		this.inputDir = inputDir; // createFileDescription(inputDir, files, gateway);
		this.outputDir = outputDir; // createFileDescription(outputDir, files, gateway);
		this.experimentDir = experimentDir; // createFileDescription(experimentDir, files, gateway);
		this.templateDir = templateDir; // createFileDescription(templateDir, files, gateway);
		
		this.startScript = templateDir + File.separator + "start"; //   Utils.getSubFile(this.templateDir, "start");
		this.stopScript = templateDir + File.separator + "stop"; // Utils.getSubFile(this.templateDir, "stop");
		this.monitorScript = templateDir + File.separator + "monitor"; //Utils.getSubFile(this.templateDir, "monitor");
		//this.configTemplate = Utils.getSubFile(this.templateDir, "pop_in_template");
	}
	
	private static FileDescription createFileDescription(String path, ResourceDescription files, ResourceDescription gateway) { 
		String completeURI = files.URI + File.separator + path;
		ResourceDescription tmp = new ResourceDescription(completeURI, files.username, files.userkey, files.adaptors);
		return new FileDescription(tmp, gateway);
	}
	
	public String toString() { 
		return "Machine(" + name + ", base=" + host + /*" scheduler=" + scheduler + */", files=" + files + ", gateway=" + gateway 
				/*", slots=" + slots*/ + ", inputDir=" + inputDir + ", outputDir=" + outputDir + ", experimentDir=" + experimentDir + ", templateDir=" + templateDir;  
	}

	public void addTemplate(ConfigurationTemplate t) {
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
		WorkerDescription other = (WorkerDescription) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}

