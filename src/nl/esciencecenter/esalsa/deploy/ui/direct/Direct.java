package nl.esciencecenter.esalsa.deploy.ui.direct;

import java.io.File;

import nl.esciencecenter.esalsa.deploy.POPRunner;
import nl.esciencecenter.esalsa.deploy.parser.DeployProperties;
import nl.esciencecenter.esalsa.deploy.parser.DescriptionParser;

public class Direct {
	
	public static void main(String [] args) { 
	
		// Single argument pointing to configuration file.
		if (args.length < 1) { 
			System.err.println("Usage: POPRunner [configuration file] <options>");
			System.exit(1);
		}
	
		File file = new File(args[0]);
	
		if (!(file.exists() && file.isFile() && file.canRead())) { 
			System.err.println("Cannot read configuration file: " + args[1]);
			System.exit(1);
		}

		try { 
			DeployProperties p = new DeployProperties();
			p.loadFromFile(args[0]);

			POPRunner runner = new POPRunner(p); 

			for (int i=1;i<args.length;i++) {
				if (args[i].equals("--template")) { 
					runner.addConfigurationTemplate(DescriptionParser.readConfigurationTemplate(args[++i]));
				} else if (args[i].equals("--worker")) {
					runner.addWorkerDescription(DescriptionParser.readWorker(args[++i]));
				} else if (args[i].equals("--inputs")) {
					runner.addInputFileSet(DescriptionParser.readFileSet(args[++i]));
				//} else if (args[i].equals("--outputs")) {
//					runner.addOutputFileSet(DescriptionParser.readFileSet(args[++i]));
				} else if (args[i].equals("--experiment")) {
					runner.addExperimentDescription(DescriptionParser.readExperimentDescription(args[++i]));
				} else { 
					System.err.println("Unknown option: " + args[i]);
				}
			}

			for (String id : runner.listExperimentDescriptions()) {
				System.out.println("Starting experiment " + id);
				runner.startExperiment(id);
			}
		
			runner.run();
		} catch (Exception e) {
			System.err.println("DirectRunner failed unexpectedly!: " + e.getLocalizedMessage());
			e.printStackTrace(System.err);
		}
	} 
}
