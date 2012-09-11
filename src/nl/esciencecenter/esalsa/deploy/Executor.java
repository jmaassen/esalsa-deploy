package nl.esciencecenter.esalsa.deploy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Executor extends Thread {

	private static final Logger globalLogger = LoggerFactory.getLogger("eSalsa");

	public final String ID;

	private final EnsembleDescription parent;
	
	private final MachineDescription md;
	
	private final FileTransferService fileTransferService;
	
	private Experiment experiment;
	
	public Executor(String ID, EnsembleDescription parent, FileTransferService fileTransferService, MachineDescription md) {
		super(ID);
		this.ID = ID;
		this.md = md;
		this.parent = parent;
		this.fileTransferService = fileTransferService;
	}
	/*
	private Experiment nextExperiment() { 

		while (true) { 
			try {	
				return parent.generateExperiment(md);
			} catch (Exception e) {
				globalLogger.error("ERROR: Failed to retrieve experiment: ", e);
			}
		}		
	}
	
	public void run() { 

		globalLogger.info("Starting executor " + ID);
		
		experiment = nextExperiment(); 
		
		while (experiment != null) { 

			experiment.run(fileTransferService);
			parent.finishedExperiment(experiment);			
			experiment = nextExperiment();
		}
		
		globalLogger.info("Finished executor " + ID);
	}
	
		*/
}
