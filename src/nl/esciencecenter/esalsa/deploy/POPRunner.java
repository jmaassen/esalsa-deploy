package nl.esciencecenter.esalsa.deploy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

//import org.gridlab.gat.URI;

import nl.esciencecenter.esalsa.deploy.parser.DeployProperties;
import nl.esciencecenter.esalsa.deploy.parser.TemplateParser;
import nl.esciencecenter.esalsa.util.FileTransferService;

public class POPRunner {
	
	private final Object lock = new Object();
	
	protected final FileTransferService fileTransferService; 
	
	private MarkableMap<ConfigurationTemplate> configurations = new MarkableMap<ConfigurationTemplate>(lock, "Configuration");
	
	private MarkableMap<WorkerDescription> workers = new MarkableMap<WorkerDescription>(lock, "Worker");
	
	private MarkableMap<FileSet> inputs = new MarkableMap<FileSet>(lock, "InputFileSet");
	private MarkableMap<FileSet> outputs = new MarkableMap<FileSet>(lock, "OutputFileSet");
	
	private MarkableMap<ExperimentDescription> experimentDescriptions = new MarkableMap<ExperimentDescription>(lock, "Experiment Descriptions");

	private MarkableMap<ExperimentInstance> runningExperiments = new MarkableMap<ExperimentInstance>(lock, "Running Experiments");
	
	// FIXME!!!
	private static int counter = 0;

	private POPRunner() {
		fileTransferService = new FileTransferService(1);
	}
	
	public String getTempDir() {
		return "/tmp";
	}
	
	/* Configuration */
	
	public void addConfigurationTemplate(ConfigurationTemplate template) throws Exception { 
		configurations.add(template);
	}
	
	public List<String> listConfigurations() { 
		return configurations.getKeys();
	}
	
	public ConfigurationTemplate getConfigurationTemplate(String ID) throws Exception { 
		return configurations.get(ID);
	}
	
	public void removeConfigurationTemplate(String ID) throws Exception { 
		configurations.remove(ID);
	}
	
	/* Worker */
	
	public void addWorker(WorkerDescription worker) throws Exception { 
		workers.add(worker);
	}
	
	public List<String> listWorkers() { 
		return workers.getKeys();
	}
	
	public WorkerDescription getWorker(String ID) throws Exception { 
		return workers.get(ID);
	}
	
	public void removeWorker(String ID) throws Exception { 
		workers.remove(ID);
	}

	/* Inputs */
	
	public void addInputFileSet(FileSet f) throws Exception { 
		inputs.add(f);
	}

	public List<String> listInputFileSets() { 
		return inputs.getKeys();
	}
	
	public FileSet getInputFileSet(String ID) throws Exception { 
		return inputs.get(ID);
	}

	public void removeInputFileSet(String ID) throws Exception { 
		inputs.remove(ID);
	}
	
	/* Outputs */
	
	public void addOutputFileSet(FileSet f) throws Exception { 
		outputs.add(f);
	}

	public List<String> listOutputFileSets() { 
		return outputs.getKeys();
	}
	
	public FileSet getOutputFileSet(String ID) throws Exception { 
		return outputs.get(ID);
	}
	
	public void removeOutputFileSet(String ID) throws Exception { 
		outputs.remove(ID);
	}
	
	/* Experiment Descriptions */
	
	public void addExperimentDescription(ExperimentDescription exp) throws Exception { 
		experimentDescriptions.add(exp);
	}

	public List<String> listExperimentDescriptions() { 
		return experimentDescriptions.getKeys();
	}

	public ExperimentDescription ExperimentDescription(String ID) throws Exception { 
		return experimentDescriptions.get(ID);
	}
	
	public void removeExperimentDescription(String ID) throws Exception { 
		experimentDescriptions.remove(ID);
	}
	
	/* Experiments */
	
	public String startExperiment(String ID) throws Exception { 
		ExperimentDescription exp = experimentDescriptions.get(ID);
		String runID = generateID(ID);
	
		ExperimentInstance e = new ExperimentInstance(runID, exp, this);
		runningExperiments.add(e);
		return e.ID;
	}

	public void finishedExperiment(ExperimentInstance experimentInstance) {
		try { 
			removeExperimentDescription(experimentInstance.ID);
		} catch (Exception e) {
			System.out.println("Failed to remove experiment " + experimentInstance.ID);
		}
	}
	
	public ExperimentInstance getRunningExperiment(String ID) throws Exception { 
		return runningExperiments.get(ID);
	}
	
	public List<String> listRunningExperiments() {
		return runningExperiments.getKeys();
	}
	
	public synchronized void stopRunningExperiment(String ID) { 
		
	}

	public void run() { 
		
		while (true) { 

			boolean stateChanged = false;
			
			List<String> tmp = listRunningExperiments();
				
			if (tmp.size() > 0) { 
				System.out.println("Running " + tmp.size() + " experiments: ");
					
				for (String ID : tmp) { 
					try { 
						ExperimentInstance e = getRunningExperiment(ID);
						System.out.println(" " + e.ID + " on " + e.worker.ID);
						boolean change = e.run();
						stateChanged = stateChanged | change;
					} catch (Exception e) {
						System.out.println("Failed to retrieve experiment: " + ID);
						System.out.println(e.getLocalizedMessage());
						e.printStackTrace();
					}
				}

				System.out.println();
			} 

			if (!stateChanged) { 
				sleep(1);
			}
		}
		
	}
	
	/* Miscellaneous utility methods to parse the input */
	
	private static String getProperty(DeployProperties properties, String key) throws Exception { 
		
		String tmp = properties.getProperty(key, null);
		
		if (tmp == null) {
			throw new Exception("Missing or invalid field: " + key);
		}
		
		return tmp;
	}
	
	/*
	private static ResourceDescription loadResourceDescription(String base, DeployProperties machines) throws Exception { 
		
		String URI = machines.getProperty(base + ".uri");
		
		if (URI == null) { 
			throw new Exception("No " + base + ".uri defined!");
		}
		
		String name = machines.getProperty(base + ".user.name", null);
		String key = machines.getProperty(base + ".user.key", null);
		String [] adaptors = machines.getStringList(base + ".adaptors", ",", null);
		
		return new ResourceDescription(URI, name, key, adaptors);
	}
	*/
	
	public static WorkerDescription readWorker(String file) throws Exception { 
		
		DeployProperties p = new DeployProperties();
		p.loadFromFile(file);
		
		String ID = getProperty(p, "ID");
		
		URI jobServer = new URI(getProperty(p, "job.uri"));
		URI fileServer = jobServer;
		
		if (p.containsKey("file.uri")) { 
			fileServer = new URI(getProperty(p, "file.uri"));
		}
				
		//ResourceDescription host = loadResourceDescription("job", p);
		//ResourceDescription files = loadResourceDescription("file", p);
		//ResourceDescription gateway = null;
		
		//if (p.containsKey("gateway")) { 
		//   gateway = loadResourceDescription("gateway", p);
		//} 

		String inputDir = getProperty(p, "inputDir");
		String outputDir = getProperty(p, "outputDir");
		String experimentDir = getProperty(p, "experimentDir");
		String templateDir = getProperty(p, "templateDir");

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
	
		String baseID = getProperty(p, "baseID");
		String config = getProperty(p, "configuration");
		String worker = getProperty(p, "worker");
		String inputs = getProperty(p, "stageIn");
		String outputs = getProperty(p, "stageOut");
		
		return new ExperimentDescription(baseID, config, worker, inputs, outputs);
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
		
	private synchronized static String generateID(String baseID) {
		return baseID + "." + counter++;
	}
	
	public static void sleep(int seconds) { 
		try { 
			Thread.sleep(seconds*1000);
		} catch (Exception e) {
			// ignored
		}
	}

	
	
	public static void main(String [] args) throws FileNotFoundException, Exception { 
		
		POPRunner runner = new POPRunner();
	
		for (int i=0;i<args.length;i++) { 
			if (args[i].equals("--config")) { 
				runner.addConfigurationTemplate(new TemplateParser(new File(args[++i])).parse());
			} else if (args[i].equals("--worker")) {
				runner.addWorker(readWorker(args[++i]));
			} else if (args[i].equals("--inputs")) {
				runner.addInputFileSet(readFileSet(args[++i]));
			} else if (args[i].equals("--outputs")) {
				runner.addOutputFileSet(readFileSet(args[++i]));
			} else if (args[i].equals("--experiment")) {
				runner.addExperimentDescription(readExperimentDescription(args[++i]));
			} else { 
				System.err.println("Unknown option: " + args[i]);
			}
		}
		
		for (String id : runner.listExperimentDescriptions()) {
			runner.startExperiment(id);
		}
		
		runner.run();
	}
}
