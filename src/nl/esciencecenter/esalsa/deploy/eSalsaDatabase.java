package nl.esciencecenter.esalsa.deploy;

import java.io.File;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

public class eSalsaDatabase {
	
	private static final String CLASS_CATALOG = "java_class_catalog";
	
	private static final String WORKER_STORE                 = "worker_description_store";
	private static final String INPUTS_STORE                 = "inputs_store";
	private static final String TEMPLATE_STORE               = "template_store";
	private static final String EXPERIMENT_DESCRIPTION_STORE = "experiment_decription_store";
	private static final String RUNNING_EXPERIMENT_STORE     = "running_experiment_store";
	private static final String COMPLETED_EXPERIMENT_STORE   = "completed_experiment_store";
	
	private Environment env;

	private StoredClassCatalog javaCatalog;

	private Database workerDb;
	private Database inputsDb;
	private Database templatesDb;
	private Database experimentDescriptionDb;
	private Database runningExperimentsDb;
	private Database completedExperimentsDb;
	
	private Store<WorkerDescription> workerDescriptions;
	private Store<FileSet> inputSets;
	private Store<ConfigurationTemplate> configurationTemplates;
	private Store<ExperimentDescription> experimentDescriptions;
	private Store<ExperimentInfo> runningExperiments;
	private Store<ExperimentInfo> completedExperiments;
	
	/**
     * Open all storage containers, indices, and catalogs.
     */
	public eSalsaDatabase(String homeDirectory) throws DatabaseException {

		// Open the Berkeley DB environment in transactional mode.
        System.out.println("Opening eSalsa DB in: " + homeDirectory);
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setTransactional(true);
        envConfig.setAllowCreate(true);
        env = new Environment(new File(homeDirectory), envConfig);

        // Set the Berkeley DB config for opening all stores.
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setTransactional(true);
        dbConfig.setAllowCreate(true);

        // Create the Serial class catalog.  This holds the serialized class
        // format for all database records of serial format.
        Database catalogDb = env.openDatabase(null, CLASS_CATALOG, dbConfig);
        javaCatalog = new StoredClassCatalog(catalogDb);

        // Open the various Berkeley DB databases. The stores are opened with 
        // no duplicate keys allowed.
        workerDb = env.openDatabase(null, WORKER_STORE, dbConfig);
        inputsDb = env.openDatabase(null, INPUTS_STORE, dbConfig);
        templatesDb = env.openDatabase(null, TEMPLATE_STORE, dbConfig);
        experimentDescriptionDb = env.openDatabase(null, EXPERIMENT_DESCRIPTION_STORE, dbConfig);
        runningExperimentsDb = env.openDatabase(null, RUNNING_EXPERIMENT_STORE, dbConfig);
        completedExperimentsDb = env.openDatabase(null, COMPLETED_EXPERIMENT_STORE, dbConfig);
        
        // The stored key and data entries are used directly rather than mapping 
        // them to separate objects. Therefore, no binding classes are defined 
        // here and the SerialBinding class is used.
        TupleBinding<String> keyBinding = TupleBinding.getPrimitiveBinding(String.class);
        
        EntryBinding<WorkerDescription> workerBinding = new SerialBinding<WorkerDescription>(javaCatalog, WorkerDescription.class);
        EntryBinding<FileSet> inputsBinding = new SerialBinding<FileSet>(javaCatalog, FileSet.class);
        
        EntryBinding<ConfigurationTemplate> templatesBinding = new SerialBinding<ConfigurationTemplate>(javaCatalog, ConfigurationTemplate.class);
        EntryBinding<ExperimentDescription> experimentsBinding = new SerialBinding<ExperimentDescription>(javaCatalog, ExperimentDescription.class);
        
        EntryBinding<ExperimentInfo> infoBinding = new SerialBinding<ExperimentInfo>(javaCatalog, ExperimentInfo.class);
                
        // Create map views for all stores and indices.
        workerDescriptions = new Store<WorkerDescription>(workerDb, keyBinding, workerBinding, "Worker Description Store");
        inputSets = new Store<FileSet>(inputsDb, keyBinding, inputsBinding, "Input Set Store");
        configurationTemplates = new Store<ConfigurationTemplate>(templatesDb, keyBinding, templatesBinding, "Configuration Template Store");
        experimentDescriptions = new Store<ExperimentDescription>(experimentDescriptionDb, keyBinding, experimentsBinding, "Experiment Description Store");	
        runningExperiments = new Store<ExperimentInfo>(runningExperimentsDb, keyBinding, infoBinding, "Running Experiment Store");
        completedExperiments = new Store<ExperimentInfo>(completedExperimentsDb, keyBinding, infoBinding, "Completed Experiment Store");
	}
	
	/**
     * Close all databases and the environment.
     */
    public void close() throws DatabaseException {

    	workerDb.close();
        inputsDb.close();
        templatesDb.close();
        experimentDescriptionDb.close();
        runningExperimentsDb.close();
        completedExperimentsDb.close();
        javaCatalog.close();
        env.close();
    }
    
    public Store<WorkerDescription> getWorkerDescriptions() {
		return workerDescriptions;
	}

	public Store<FileSet> getInputSets() {
		return inputSets;
	}

	public Store<ConfigurationTemplate> getConfigurationTemplates() {
		return configurationTemplates;
	}

	public Store<ExperimentDescription> getExperimentDescriptions() {
		return experimentDescriptions;
	}

	public Store<ExperimentInfo> getRunningExperiments() {
		return runningExperiments;
	}
	
	public Store<ExperimentInfo> getCompletedExperiments() {
		return completedExperiments;
	}
}
