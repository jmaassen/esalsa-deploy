package nl.esciencecenter.esalsa.deploy.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import nl.esciencecenter.esalsa.deploy.ConfigurationTemplate;
import nl.esciencecenter.esalsa.deploy.ExperimentDescription;
import nl.esciencecenter.esalsa.deploy.FileSet;
import nl.esciencecenter.esalsa.deploy.WorkerDescription;

public class DescriptionParser {

	private static String getProperty(DeployProperties properties, String key, String description) throws Exception { 
		
		String tmp = properties.getProperty(key, null);
		
		if (tmp == null) {
			throw new Exception("Missing or invalid field in " + description + ": " + key + " (fields found: " + properties.keySet() + ")");
		}
		
		return tmp;
	}
	
	public static WorkerDescription readWorker(String file) throws Exception { 
		
		DeployProperties p = new DeployProperties();
		p.loadFromFile(file);
		
		System.out.println("Loading file: " + file);
		
		String ID = getProperty(p, "ID", "WorkerDescription");
		
		URI jobServer = new URI(getProperty(p, "uri", "WorkerDescription"));
		URI fileServer = jobServer;
		
		if (p.containsKey("file.uri")) { 
			fileServer = new URI(getProperty(p, "file.uri", "WorkerDescription"));
		}
				
		//ResourceDescription host = loadResourceDescription("job", p);
		//ResourceDescription files = loadResourceDescription("file", p);
		//ResourceDescription gateway = null;
		
		//if (p.containsKey("gateway")) { 
		//   gateway = loadResourceDescription("gateway", p);
		//} 

		String inputDir = getProperty(p, "inputDir", "WorkerDescription");
		String outputDir = getProperty(p, "outputDir", "WorkerDescription");
		String experimentDir = getProperty(p, "experimentDir", "WorkerDescription");
		String templateDir = getProperty(p, "templateDir", "WorkerDescription");

		HashMap<String, String> values = new HashMap<String, String>(); 
		
		for (Entry<Object, Object> entry : p.entrySet()) { 
			
			if (!(entry.getKey() instanceof String && entry.getValue() instanceof String)) { 
				throw new Exception("Failed to parse worker description " + file);
			} 

			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			
			values.put("worker." + key, value);
		}
		
		return new WorkerDescription(ID, jobServer, fileServer, inputDir, outputDir, experimentDir, templateDir, values);
	}
	
	public static ExperimentDescription readExperimentDescription(String file) throws Exception { 

		DeployProperties p = new DeployProperties();
		p.loadFromFile(file);
	
		String ID = getProperty(p, "ID", "ExperimentDescription");
		String config = getProperty(p, "configuration", "ExperimentDescription");
		String worker = getProperty(p, "worker", "ExperimentDescription");
		String input = getProperty(p, "input", "ExperimentDescription");
		
		return new ExperimentDescription(ID, config, worker, input);
	}
	
	public static FileSet readFileSet(String file) throws IOException, URISyntaxException { 
		
		BufferedReader r = new BufferedReader(new FileReader(new File(file)));

		LinkedList<URI> files = new LinkedList<URI>();
		
		String ID = r.readLine();
		
		String line = r.readLine();
		
		while (line != null) { 
			files.add(new URI(line));
			line = r.readLine();
		}
		
		r.close();

		return new FileSet(ID, files);		
	}
	
	public static ConfigurationTemplate readConfigurationTemplate(String file) throws Exception { 
		return new TemplateParser(new File(file)).parse();
	} 	
}
