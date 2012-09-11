package nl.esciencecenter.esalsa.deploy;

import java.io.File;

import nl.esciencecenter.esalsa.deploy.parser.DeployParser;
import nl.esciencecenter.esalsa.util.Event;
import nl.esciencecenter.esalsa.util.EventLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	
	public static void main(String [] args) { 
		
		if (args.length != 3) { 
			System.err.println("Usage: Main [experiment-dir] [output-dir] [restart-log]");
			System.exit(1);
		}
		
		Logger globalLogger = LoggerFactory.getLogger("eSalsa");

		// Start by creating the restart log (or reading an existing one). 
		EventLogger eventLogger = null;
				
		File log = new File(args[2]);
		
		try { 
			eventLogger = EventLogger.get(log);
		} catch (Exception e) {
			globalLogger.error("[Main] Failed to create/read restart log " + log.getAbsolutePath());
		} 
		
		// Check if the experiment was restarted (or already done). If the previous run crashed during 
		// initialization we expect the user to clean up manually.
		if (eventLogger.restart) { 
			Event e = eventLogger.findOne("[MAIN]", "-", "START", "-");
		
			if (e == null) { 
				globalLogger.info("[Main] Experiment terminated before start according to log: " + log.getAbsolutePath());
				globalLogger.info("[Main] Please clean up manually!");
				System.exit(1);
			}
			
			e = eventLogger.findOne("[MAIN]", "-", "DONE", "-");
			
			if (e != null) { 
				globalLogger.info("[Main] Already finished according to log: " + log.getAbsolutePath());
				System.exit(0);
			}
		}
		
		// Check if the ensembleDir exists
		File ensembleDir = new File(args[0]);
	
		globalLogger.info("[Main] INFO: Loading ensemble description from: " + ensembleDir.getAbsolutePath());

		if (!(ensembleDir.exists() && ensembleDir.isFile() && ensembleDir.canRead())) { 
			globalLogger.error("[Main] ERROR: Cannot access ensemble configuration: " + ensembleDir.getAbsolutePath());
			System.exit(1);
		}
		
		// Check if the output dir is accessible
		File output = new File(args[1]);
		
		globalLogger.info("[Main] INFO: Writing output to: " + output.getAbsolutePath());
		
		if (!(output.exists() && output.isDirectory() && output.canWrite() && output.canRead())) { 
			globalLogger.error("[Main] ERROR: Cannot access output directory: " + output.getAbsolutePath());
			System.exit(1);
		}
		
		if (eventLogger.restart) { 
			
			Event e = eventLogger.findOne("[MAIN]", "-", "LOADING_ENSEMBLE", null);
		
			if (e == null) { 
				globalLogger.error("[Main] ERROR: Log seems to be corrupt!?");
				System.exit(1);
			}
				
			if (!e.message.equals(ensembleDir.getAbsolutePath())) { 
				globalLogger.error("[Main] ERROR: Ensemble mismatch " + e.message + " != " + ensembleDir.getAbsolutePath());
				System.exit(1);
			}
			
		} else { 
			eventLogger.log("[MAIN]", "-", "LOADING_ENSEMBLE", ensembleDir.getAbsolutePath());
		}
			
		// Parse the ensemble description
		EnsembleDescription ensemble = null;
				
		try { 
			ensemble = new DeployParser(args[0]).parse();
		} catch (Exception e) {
			globalLogger.error("[Main] ERROR: Failed to load ensemble!", e);
			System.exit(1);
		}
		
		globalLogger.info("[Main] INFO: Succesfully loaded ensemble description: " + ensemble.baseID);
		
		FileTransferService fileTransferService = new FileTransferService(1); 

		ensemble.setFileTransferService(fileTransferService);
		ensemble.verify();
		ensemble.createDirectories(output);
		ensemble.generateTemplates();
		ensemble.generateExperiments();
		
		eventLogger.log("[MAIN]", "-", "START", "-");		
		
		ensemble.run();
		
		fileTransferService.done();
		
		eventLogger.log("[MAIN]", "-", "DONE", "-");		
	}
}
