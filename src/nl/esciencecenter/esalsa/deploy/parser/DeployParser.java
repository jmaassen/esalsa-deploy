package nl.esciencecenter.esalsa.deploy.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nl.esciencecenter.esalsa.deploy.EnsembleDescription;
import nl.esciencecenter.esalsa.deploy.FileDescription;
import nl.esciencecenter.esalsa.deploy.MachineDescription;
import nl.esciencecenter.esalsa.deploy.ResourceDescription;
import nl.esciencecenter.esalsa.deploy.Template;

public class DeployParser extends ConfigurationParser {

	public DeployParser(String source) {
		super(new File(source));
	}

	private String getProperty(DeployProperties properties, String key) throws Exception { 
		
		String tmp = properties.getProperty(key, null);
		
		if (tmp == null) {
			throw new Exception("Missing or invalid field: " + key);
		}
		
		return tmp;
	}
	
	private int getIntProperty(DeployProperties properties, String key) throws Exception { 
		
		int tmp = properties.getIntProperty(key, 0); 
		
		if (tmp <= 0) {
			throw new Exception("Missing or invalid field: " + key);
		}
		
		return tmp;
	}
	
	private List<String> getListProperty(DeployProperties properties, String key) throws Exception { 
		
		List<String> tmp = properties.getStringListProperty(key);
		
		if (tmp == null || tmp.size() == 0) {
			throw new Exception("Missing or invalid field: " + key);
		}
		
		return tmp;
	}
	
	private ResourceDescription loadResourceDescription(String base, DeployProperties machines, String defaultName, String defaultKey) throws Exception { 
		
		String URI = machines.getProperty(base + ".uri");
		
		if (URI == null) { 
			throw new Exception("No " + base + ".uri defined!");
		}
		
		String name = machines.getProperty(base + ".user.name", defaultName);
		String key = machines.getProperty(base + ".user.key", defaultKey);
		String [] adaptors = machines.getStringList(base + ".adaptors", ",", null);
		
		return new ResourceDescription(URI, name, key, adaptors);
	}
	
	
	private MachineDescription loadMachineDescription(String base, String resource, DeployProperties properties, DeployProperties machines) throws Exception {
		
		String name = machines.getProperty(resource + ".user.name", null);
		String key = machines.getProperty(resource + ".user.key", null);
		
		ResourceDescription support = loadResourceDescription(resource + ".support", machines, name, key);
		ResourceDescription job = loadResourceDescription(resource + ".job", machines, name, key);
		ResourceDescription files = loadResourceDescription(resource + ".file", machines, name, key);
		
		ResourceDescription gateway = null;
		
		if (machines.containsKey(resource + ".gateway")) {
			gateway = loadResourceDescription(resource + ".support", machines, name, key);
		}
		
		String basename = base + "." + resource;
		
		//int cores = getIntProperty(properties, basename + ".cores"); 
		int slots = getIntProperty(properties, basename + ".slots");
		
		String inputDir = getProperty(properties, basename + ".inputDir");
		String outputDir = getProperty(properties, basename + ".outputDir");
		String experimentDir = getProperty(properties, basename + ".experimentDir");
		String templateDir = getProperty(properties, basename + ".templateDir");
		
		return new MachineDescription(base + "." + resource, support, job, gateway, files, slots, inputDir, outputDir, experimentDir, templateDir);
	}

	private List<MachineDescription> loadMachineDescriptions(String base, List<String> resources, String resourceFile, DeployProperties properties) throws Exception { 
		
		if (resources == null || resources.size() == 0 || resourceFile == null) { 
			throw new Exception("Ensemble description " + source.getAbsolutePath() + " does not refer to any resources!");
		}
		
		File tmp = new File(resourceFile);
		
		if (!(tmp.exists() && tmp.isFile() && tmp.canRead())) {
			String path = new File(source.getAbsolutePath()).getParent();
			tmp = new File(path + File.separator + resourceFile);
		}
			
		if (!(tmp.exists() && tmp.isFile() && tmp.canRead())) {
			throw new FileNotFoundException("Machine description file " + resourceFile + " not found!");
		}
		
		DeployProperties machines = new DeployProperties();
		machines.loadFromFile(tmp.getAbsolutePath());
		
		LinkedList<MachineDescription> result = new LinkedList<MachineDescription>();
		
		for (String resource : resources) { 
			MachineDescription m = loadMachineDescription(base, resource, properties, machines);
			result.add(m);
		}
		
		return result;
	}

	private void loadFile(String base, String field, DeployProperties properties, ResourceDescription defaultGateway, Map<String, FileDescription> result) throws Exception {

		String basename = base + "." + field;
		
		String URI = properties.getProperty(basename);
		
		if (URI == null) { 
			throw new Exception("No field " + basename + " defined!");
		}
		
		String name = properties.getProperty(basename + ".user.name", null);
		String key = properties.getProperty(basename + ".user.key", null);
		String [] adaptors = properties.getStringList(basename + ".adaptors", ",", null);
		
		ResourceDescription file = new ResourceDescription(URI, name, key, adaptors);
		
		String gatewayURI = properties.getProperty(field + ".gateway.uri");
		
		ResourceDescription gateway = defaultGateway;
		
		if (gatewayURI != null) { 
			gateway = loadResourceDescription(basename + ".gateway", properties, null, null);
		}
		
		result.put(field, new FileDescription(file, gateway));		
	}
	
	private Map<String, FileDescription> loadFiles(String base, DeployProperties properties, ResourceDescription defaultGateway, String [] names) throws Exception {

		Map<String, FileDescription> result = new HashMap<String, FileDescription>();
		
		for (String s : names) { 
			if (s != null) { 
				loadFile(base, s, properties, defaultGateway, result);
			}
		}
		
		return result;
	}
	
	private Map<String, FileDescription> loadFiles(String base, DeployProperties properties, String [] names) throws Exception { 
		
		ResourceDescription gateway = null;
		
		if (properties.containsKey(base + ".gateway.uri")) { 
			gateway = loadResourceDescription(base + ".gateway", properties, null, null);
		}
		
		return loadFiles(base, properties, gateway, names);
	}

	private Map<String, String> loadFileNames(String base, DeployProperties properties, String [] names) throws Exception { 

		Map<String, String> result = new HashMap<String, String>();
		
		for (String s : names) { 
			if (s != null) { 
				String basename = base + "." + s;		
				String name = properties.getProperty(basename);
				
				if (name == null) { 
					throw new Exception("No field " + basename + " defined!");
				}
				
				result.put(s, name);		
			}
		}
		
		return result;
	}
	
	
	private Template loadConfigurationTemplate(String template) throws Exception {
		
		File tmp = new File(template);
		
		if (!(tmp.exists() && tmp.isFile() && tmp.canRead())) {
			String path = new File(source.getAbsolutePath()).getParent();
			tmp = new File(path + File.separator + template);
		}
			
		if (!(tmp.exists() && tmp.isFile() && tmp.canRead())) {
			throw new FileNotFoundException("Configuration template " + template + " not found!");
		}
		
		return new TemplateParser(tmp).parse(); 
	}
	
	@Override
	public EnsembleDescription parse() throws Exception {
		
		DeployProperties properties = new DeployProperties();
		
		properties.loadFromFile(source.getAbsolutePath());
		
		String tmp = properties.getProperty("ensembles");
		
		if (tmp == null) { 
			throw new IOException("Ensemble description " + source.getAbsolutePath() + " does not contain field \"ensembles\"");
		}

		// Load the generic ensemble description		
		int size = getIntProperty(properties, tmp + ".size");
		
		String baseID = getProperty(properties, tmp + ".baseID");
		
		String template = getProperty(properties, tmp + ".template");

		List<String> resources = getListProperty(properties, tmp + ".resources");
		
		String resourceFile = getProperty(properties, tmp + ".resources.file");
		
		// Load the input file properties		
		Map<String, FileDescription> inputs = loadFiles(tmp, properties, EnsembleDescription.inputFiles); 

		// Load the output file properties
		Map<String, String> outputs = loadFileNames(tmp, properties, EnsembleDescription.outputFiles); 

		
		// Load the machine descriptions		
		List<MachineDescription> machines = loadMachineDescriptions(tmp, resources, resourceFile, properties);
		
		// Load the configuration template
		//Template t = loadConfigurationTemplate(template);
		
		return new EnsembleDescription(tmp, size, baseID, inputs, outputs, template, /*t,*/ machines);
	}

}
