package nl.esciencecenter.esalsa.deploy.ui.cli;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;
import java.util.StringTokenizer;

import nl.esciencecenter.esalsa.deploy.ConfigurationTemplate;
import nl.esciencecenter.esalsa.deploy.ExperimentTemplate;
import nl.esciencecenter.esalsa.deploy.FileSet;
import nl.esciencecenter.esalsa.deploy.WorkerDescription;
import nl.esciencecenter.esalsa.deploy.parser.DescriptionParser;
import nl.esciencecenter.esalsa.deploy.server.Stub;
import nl.esciencecenter.esalsa.deploy.server.Util;

public class Client {
	
	private static int DEFAULT_PORT = 50656;
	private static int DEFAULT_TIMEOUT = 30000;
	
	private static String PROMPT = "> ";
	
	private final BufferedReader in;
	private final BufferedWriter out;
	
	private final String server;
	
	private StringTokenizer currentTokenizer;

	private Tokens currentToken;
	private Targets currentTarget;
	private String currentValue;
	private String currentLine;
	private String parsedSoFar;

	private Object currentParameter;
	
	private boolean done = false;
	
	private Socket socket; 
	
	private Stub stub;

	public enum Tokens {
		ADD, GET, REMOVE, LIST, CREATE, START, STOP, EXIT, HELP;
	}

	public enum Targets {
		TEMPLATE, WORKER, INPUTS, EXPERIMENT, RUNNING, STOPPED;
	}
	
	private Client(String serverAddress, int port) throws Exception { 
		
		// connect to server here...
		this.server = serverAddress;
		
		try { 
			stub = new Stub(Util.connect(serverAddress, port, DEFAULT_TIMEOUT));
		} catch (Exception e) {
			System.err.println("Failed to connect to server: " + serverAddress + ":" + port + " " + e);
			e.printStackTrace(System.err);
			throw e;
		} 
		
		in = new BufferedReader(new InputStreamReader(System.in));
		out = new BufferedWriter(new OutputStreamWriter(System.out));
	}

	private boolean parseToken() throws IOException { 

		if (!currentTokenizer.hasMoreTokens()) { 
			out.write("Failed to read any command\n");
			return false;
		}
		
		String tmp = currentTokenizer.nextToken();
		
		try { 
			currentToken = Tokens.valueOf(tmp.toUpperCase());
			parsedSoFar += tmp;
			return true;
		} catch (Throwable e) {
			out.write("Unknown command: \"" + tmp + "\"\n");
		}
		
		return false;
	}
	
	private boolean parseTarget() throws IOException { 

		if (!currentTokenizer.hasMoreTokens()) { 
			out.write("Failed to read target of command \"" + parsedSoFar + "\"\n");
			return false;
		}
		
		String tmp = currentTokenizer.nextToken();
		
		try { 
			currentTarget = Targets.valueOf(tmp.toUpperCase());
			parsedSoFar += " " + tmp;
			return true;
		} catch (Throwable e) {
			out.write("Invalid target " + tmp + " for command \"" + parsedSoFar + "\"\n");
		}
	
		return false;
	}

	private boolean parseValue() throws IOException { 

		if (!currentTokenizer.hasMoreTokens()) { 
			out.write("Failed to read value for command \"" + parsedSoFar + "\"!\n");
			return false;
		}
		
		currentValue = currentTokenizer.nextToken();
		parsedSoFar += " " + currentValue;
		return true;
	}

	private boolean checkEmptyLine() throws IOException { 

		if (currentTokenizer.hasMoreTokens()) { 
			out.write("Unexpected input after command \"" + parsedSoFar + "\": " + currentTokenizer.nextToken() + "\n");
			return false;
		}
		
		return true;
	}
	
	private boolean parse(String line) throws IOException {
	
		currentToken = null;
		currentTarget = null;
		currentValue = null;
		currentTokenizer = null;
		parsedSoFar = "";
		
		currentLine = line.trim();
		
		if (line.length() == 0) {
			return false;
		}
		
		currentTokenizer = new StringTokenizer(currentLine);
		
		if (!parseToken()) { 
			return false;
		}
		
		switch (currentToken) { 
		case ADD:
		case GET:
		case REMOVE:
			return parseTarget() && parseValue() && checkEmptyLine();
		case LIST:
			return parseTarget() && checkEmptyLine();
		case CREATE:
		case START:
		case STOP:
			return parseValue() && checkEmptyLine();
		case EXIT:
		case HELP:
			return checkEmptyLine();
		}

		return false;
	}
	
	private boolean readFile() throws IOException {
		
		File tmp = new File(currentValue);
		
		if (!tmp.exists() || !tmp.isFile()) { 
			out.write("File \"" + tmp.getAbsolutePath() + "\" not found!\n");
			return false;
		}
		
		if (!tmp.canRead()) { 
			out.write("File \"" + tmp.getAbsolutePath() + "\" not readable!\n");
			return false;
		}

		try {
			switch (currentTarget) { 
			case TEMPLATE:
				currentParameter = DescriptionParser.readConfigurationTemplate(currentValue);
				break;
			case EXPERIMENT:
				currentParameter = DescriptionParser.readExperimentDescription(currentValue);
				break;
			case INPUTS:
				currentParameter = DescriptionParser.readFileSet(currentValue);
				break;
			case WORKER:
				currentParameter = DescriptionParser.readWorker(currentValue);
				break;
			case RUNNING:
				throw new Exception("INTERNAL ERROR: Cannot ADD RUNNING");				
			}			 
		} catch (Exception e) {
			out.write("FAILED: " + e.getLocalizedMessage() + "\n");
			return false;
		}			
		
		return true;
	}

	private boolean forwardAdd() throws IOException { 
		
		try { 
			switch (currentTarget) { 
			case TEMPLATE:
				stub.addConfigurationTemplate((ConfigurationTemplate) currentParameter);
				break;
			case EXPERIMENT:
				stub.addExperimentDescription((ExperimentTemplate) currentParameter);
				break;
			case INPUTS:
				stub.addInputFileSet((FileSet) currentParameter);				
				break;		
			case WORKER:
				stub.addWorkerDescription((WorkerDescription) currentParameter);
				break;
			case RUNNING:
			case STOPPED:
				throw new Exception("INTERNAL ERROR: Cannot ADD " + currentTarget.name() + " " + currentValue);				
			}

		} catch (Exception e) {
			out.write("FAILED: " + e.getLocalizedMessage() + "\n");
			return false;
		}

		out.write("OK\n");
		return true;
	} 
		
	private boolean forwardGet() throws IOException { 

		Object result = null;
		
		try { 
			switch (currentTarget) { 
			case TEMPLATE:
				result = stub.getConfigurationTemplate(currentValue);
				break;
			case EXPERIMENT:
				result = stub.getExperimentDescription(currentValue);
				break;
			case INPUTS:
				result = stub.getInputFileSet(currentValue);
				break;
			case WORKER:
				result = stub.getWorkerDescription(currentValue);
				break;
			case RUNNING:
				result = stub.getRunningExperiment(currentValue);
				break;
			case STOPPED:
				result = stub.getStoppedExperiment(currentValue);
				break;
			}

		} catch (Exception e) {
			out.write("FAILED: " + e.getLocalizedMessage() + "\n");
			return false;
		}

		out.write(result + "\n");
		return true;		
	} 

	private boolean forwardRemove() throws IOException { 

		try { 
			switch (currentTarget) { 
			case TEMPLATE:
				stub.removeConfigurationTemplate(currentValue);
				break;
			case EXPERIMENT:
				stub.removeExperimentDescription(currentValue);
				break;
			case INPUTS:
				stub.removeInputFileSet(currentValue);
				break;
			case WORKER:
				stub.removeWorkerDescription(currentValue);
				break;
			case RUNNING:
				throw new Exception("INTERNAL ERROR: Cannot REMOVE RUNNING " + currentValue);
			case STOPPED:
				stub.removeStoppedExperiment(currentValue);
				break;
			}

		} catch (Exception e) {
			out.write("FAILED: " + e.getLocalizedMessage() + "\n");
			return false;
		}

		out.write("OK\n");
		return true;		
	} 

	
	private boolean forwardList() throws IOException { 

		List<String> result = null;
		
		try { 
			switch (currentTarget) { 
			case TEMPLATE:
				result = stub.listConfigurationTemplates();
				break;
			case EXPERIMENT:
				result = stub.listExperimentDescriptions();
				break;
			case INPUTS:
				result = stub.listInputFileSets();
				break;
			case WORKER:
				result = stub.listWorkerDescriptions();
				break;
			case RUNNING:
				result = stub.listRunningExperiments();
				break;
			case STOPPED:
				result = stub.listStoppedExperiments();
				break;
			}

		} catch (Exception e) {
			out.write("FAILED: " + e.getLocalizedMessage() + "\n");
			return false;
		}

		out.write(result + "\n");
		return true;		
	} 

	
	private boolean forwardCommand() throws IOException { 
		//out.write("forward command: " + currentToken + " " + currentTarget + " " + currentValue + "\n");

		switch (currentToken) {
		case ADD:
			return forwardAdd();
		case GET:
			return forwardGet();
		case LIST:
			return forwardList();
		case REMOVE:
			return forwardRemove();
		case CREATE:
			try { 
				String ID = stub.createExperiment(currentValue);
				out.write("OK: " + ID + "\n");
				return true;
			} catch (Exception e) {
				out.write("FAILED: " + e.getLocalizedMessage() + "\n");
				return false;
			}			
		case START:
			try { 
				stub.startExperiment(currentValue);
				return true;
			} catch (Exception e) {
				out.write("FAILED: " + e.getLocalizedMessage() + "\n");
				return false;
			}			
		case STOP:
			try { 
				stub.stopRunningExperiment(currentValue);
				out.write("OK\n");
				return true;
			} catch (Exception e) {
				out.write("FAILED: " + e.getLocalizedMessage() + "\n");
				return false;
			}
		case EXIT: 
		case HELP:
			out.write("INTERNAL ERROR: Cannot forward " + currentToken.name() + "\n");				
			return false;
		}
		
		return true;
	}
	
	private void processCommand() throws IOException { 

		if (currentToken == Tokens.EXIT) {
			out.write("Goodbye\n");
			done = true;
			return;
		}
		
		if (currentToken == Tokens.HELP) {
			out.write("No help -- oh dear!\n");
			return;
		} 

		// Semantic check -- ensure we don't ADD/GET/REMOVE a RUNNING target or ADD a STOPPED target.
		if (currentTarget == Targets.RUNNING && (currentToken == Tokens.ADD || currentToken == Tokens.REMOVE)) {  
			out.write("ERROR: Cannot " + currentToken.name() + " RUNNING!\n");
			return;
		}
		
		if (currentTarget == Targets.STOPPED && currentToken == Tokens.ADD) {  
			out.write("ERROR: Cannot ADD STOPPED!\n");
			return;
		}
	
		if (currentToken == Tokens.ADD) {
			if (!readFile()) { 
				return;
			}
		}
		
		forwardCommand();
	}
	
	public void run() throws IOException { 

		out.write("Connected to server: " + server + "\n");
		out.write(PROMPT);
		out.flush();
		
		String line = in.readLine();
		
		while (!done && line != null) {
			if (parse(line)) { 
				out.write("Parsed command: " + currentToken + " " + currentTarget + " " + currentValue + "\n");
				processCommand();			
			}
			
			if (!done) { 
				out.write(PROMPT);
			} 
			
			out.flush();
				
			if (!done) { 
				line = in.readLine();
			} 
		}
	}
	
	private void close() throws IOException { 
		out.flush();
		out.close();
		in.close();
		
		stub.close();
	}
	
	public static void main(String [] args) {
		
		if (args.length == 0) { 
			System.out.println("CLI [server address]");
			System.exit(1);
		}
		
		try {
			
			Client c = new Client(args[0], DEFAULT_PORT);
			
			if (args.length == 1) { 
				c.run();
			} else { 			
				StringBuilder line = new StringBuilder(args[1]);
				
				for (int i=2;i<args.length;i++) { 
					line.append(" ");
					line.append(args[i]); 
				}
				
				if (c.parse(line.toString())) { 
					c.processCommand();
					c.close();
				} else { 
					System.out.println("Failed to parse command: \"" + line + "\"");
				}
			}
			
		} catch (Exception e) {
			System.err.println("CLI Failed unexpectedly: " + e);
			e.printStackTrace(System.err);
		}
	}
}
