package nl.esciencecenter.esalsa.deploy;

import java.io.File;
import java.io.FileNotFoundException;

import nl.esciencecenter.esalsa.deploy.parser.TemplateParser;

public class POPRunner {
	
	private final Object lock = new Object();
	
	private MarkableMap<ConfigurationTemplate> configurations = new MarkableMap<ConfigurationTemplate>(lock, "Configuration");
	
	private MarkableMap<WorkerDescription> workers = new MarkableMap<WorkerDescription>(lock, "Worker");
	
	private MarkableMap<FileSet> inputs = new MarkableMap<FileSet>(lock, "InputFileSet");
	private MarkableMap<FileSet> outputs = new MarkableMap<FileSet>(lock, "OutputFileSet");
	
	private MarkableMap<ExperimentDescription> experiments = new MarkableMap<ExperimentDescription>(lock, "Experiment");

	// FIXME!!!
	private int counter = 0;

	private POPRunner() {
		
	}
	
	private synchronized String generateID(String baseID) {
		return baseID + "." + counter++;
	}
	
	public void addConfigurationTemplate(ConfigurationTemplate template) throws Exception { 
		configurations.add(template);
	}
	
	public void removeConfigurationTemplate(String ID) throws Exception { 
		configurations.remove(ID);
	}
	
	public void addWorker(WorkerDescription worker) throws Exception { 
		workers.add(worker);
	}
	
	public void removeWorker(String ID) throws Exception { 
		workers.remove(ID);
	}

	public void addInputFileSet(FileSet f) throws Exception { 
		inputs.add(f);
	}

	public void removeInputFileSet(String ID) throws Exception { 
		inputs.remove(ID);
	}
	
	public void addOutputFileSet(FileSet f) throws Exception { 
		outputs.add(f);
	}

	public void removeOutputFileSet(String ID) throws Exception { 
		outputs.remove(ID);
	}
	
	public void addExperiment(ExperimentDescription exp) throws Exception { 
		experiments.add(exp);
	}

	public void removeExperimentDescription(String ID) throws Exception { 
		experiments.remove(ID);
	}
	
	public String startExperiment(String ID) throws Exception { 

		// Retrieve all info about the experiment. Each of these will throw an 
		// exception if an entry is missing.
		ExperimentDescription exp = experiments.get(ID);
		WorkerDescription worker = workers.get(exp.worker);
		ConfigurationTemplate config = configurations.get(exp.configuration);
		FileSet input = inputs.get(exp.stageIn);
		FileSet output = outputs.get(exp.stageOut);
		
		String configuration = config.generate(worker.getMapping());
		String newID = generateID(exp.ID);
		
		ExperimentInstance e = new ExperimentInstance(newID, worker, configuration, input, output);
		
		return e.ID;
	}
	
	public synchronized void stopExperiment(String ID) { 
		
	}

	
	
	
	public static void main(String [] args) throws FileNotFoundException, Exception { 
		
		POPRunner runner = new POPRunner();
		
		ConfigurationTemplate c = new TemplateParser(new File(args[0])).parse();
		runner.addConfigurationTemplate(c);
		
		WorkerDescription w = readWorker();
		runner.addWorker(w);
		
		FileSet inputs = readFileSet();
		runner.addInputFileSet(inputs);
		
		FileSet outputs = readFileSet();
		runner.addOutputFileSet(outputs);
		
		ExperimentDescription e = readExperiment();
		runner.addExperiment(e);
	
		runner.startExperiment(e.ID);		
	}
}
