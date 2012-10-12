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
import nl.esciencecenter.esalsa.deploy.ExperimentTemplate;
import nl.esciencecenter.esalsa.deploy.FileSet;
import nl.esciencecenter.esalsa.deploy.WorkerDescription;

public class DescriptionParser {

	private static String ID = "ID"; 
	private static String WORKER_URI = "uri";
	private static String WORKER_FILE_URI = "fileserver.uri";
	private static String WORKER_INPUTDIR = "inputDir";
	private static String WORKER_OUTPUTDIR = "outputDir";
	private static String WORKER_TEMPLATEDIR = "templateDir";
	private static String WORKER_EXPERIMENTDIR = "experimentDir";
	private static String WORKER_STARTSCRIPT = "start.sh";
	private static String WORKER_MONITORSCRIPT = "monitor.sh";
	private static String WORKER_STOPSCRIPT = "stop.sh";
	
	private static String [] WORKER_FIELDS = new String [] { ID, WORKER_URI, WORKER_FILE_URI, WORKER_INPUTDIR, WORKER_OUTPUTDIR, WORKER_TEMPLATEDIR, WORKER_EXPERIMENTDIR };
	
	private static String getProperty(DeployProperties properties, String key, String def, String description) throws Exception { 
		
		String tmp = properties.getProperty(key, def);
		
		if (tmp == null) {
			throw new Exception("Missing or invalid field in " + description + ": " + key + " (fields found: " + properties.keySet() + ")");
		}
		
		return tmp;
	}		
	
	private static String getProperty(DeployProperties properties, String key, String description) throws Exception { 
		return getProperty(properties, key, null, description);
	}
	
	public static WorkerDescription readWorker(String file) throws Exception { 
		
		DeployProperties p = new DeployProperties();
		p.loadFromFile(file);
		
		System.out.println("Loading file: " + file);
		
		String id = getProperty(p, ID, "WorkerDescription");
		
		URI jobServer = new URI(getProperty(p, WORKER_URI, "WorkerDescription"));
		URI fileServer = jobServer;
		
		if (p.containsKey("file.uri")) { 
			fileServer = new URI(getProperty(p, WORKER_FILE_URI, "WorkerDescription"));
		}
				
		//ResourceDescription host = loadResourceDescription("job", p);
		//ResourceDescription files = loadResourceDescription("file", p);
		//ResourceDescription gateway = null;
		
		//if (p.containsKey("gateway")) { 
		//   gateway = loadResourceDescription("gateway", p);
		//} 

		String inputDir = getProperty(p, WORKER_INPUTDIR, "WorkerDescription");
		String outputDir = getProperty(p, WORKER_OUTPUTDIR, "WorkerDescription");
		String experimentDir = getProperty(p, WORKER_EXPERIMENTDIR, "WorkerDescription");
		String templateDir = getProperty(p, WORKER_TEMPLATEDIR, "WorkerDescription");

		String startScript = getProperty(p, WORKER_STARTSCRIPT, "start.sh", "WorkerDescription");
		String monitorScript = getProperty(p, WORKER_MONITORSCRIPT, "monitor.sh", "WorkerDescription");
		String stopScript = getProperty(p, WORKER_STOPSCRIPT, "stop.sh", "WorkerDescription");
		
		
		HashMap<String, String> values = new HashMap<String, String>(); 
		
		for (Entry<Object, Object> entry : p.entrySet()) { 
			
			if (!(entry.getKey() instanceof String && entry.getValue() instanceof String)) { 
				throw new Exception("Failed to parse worker description " + file);
			} 

			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			
			boolean found = false;
			
			for (String tmp : WORKER_FIELDS) { 
				
				if (!found && key.equals(tmp)) { 
					found = true;
				}
			}
			
			if (!found) { 
				values.put(key, value);
			} 
		}
		
		return new WorkerDescription(id, jobServer, fileServer, inputDir, outputDir, experimentDir, templateDir, 
				startScript, monitorScript, stopScript, "No comment", values);
	}
	
	public static ExperimentTemplate readExperimentDescription(String file) throws Exception { 

		DeployProperties p = new DeployProperties();
		p.loadFromFile(file);
	
		String ID = getProperty(p, "ID", "ExperimentDescription");
		String config = getProperty(p, "configuration", "ExperimentDescription");
		String worker = getProperty(p, "worker", "ExperimentDescription");
		String input = getProperty(p, "input", "ExperimentDescription");
		
		int resubmits = Integer.parseInt(getProperty(p, "resubmits", "1", "ExperimentDescription"));
		
		return new ExperimentTemplate(ID, config, worker, input, resubmits, "This is a comment");
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

		return new FileSet(ID, "This is a comment", files);		
	}
	
	public static ConfigurationTemplate readConfigurationTemplate(String file) throws Exception {
		
		File tmp = new File(file);
		
		return new TemplateParser(new ConfigurationTemplate(tmp.getName(), ""), tmp).parse();
	} 	
}
