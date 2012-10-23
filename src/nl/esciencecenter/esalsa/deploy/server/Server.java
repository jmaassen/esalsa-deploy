package nl.esciencecenter.esalsa.deploy.server;

import java.io.File;
import java.net.ServerSocket;

import nl.esciencecenter.esalsa.deploy.POPRunner;
import nl.esciencecenter.esalsa.deploy.POPRunnerInterface;
import nl.esciencecenter.esalsa.deploy.parser.DeployProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

	private static final int DEFAULT_PORT = 50656;
	private static final int DEFAULT_TIMEOUT = 10000;
	
	private static Logger globalLogger = LoggerFactory.getLogger("eSalsa");

	private final int port;
	private final int timeout;
	
	private final String initVector;	
	private final String key; 
//	private final String cert; 
	
	private final POPRunnerInterface runner;
	
	private final ServerSocket ss;
	
	public Server(DeployProperties p, POPRunnerInterface runner) throws Exception { 
		
		this.runner = runner;		
		
		port = p.getIntProperty("poprunner.server.port", DEFAULT_PORT); 
		String bindAddr = p.getProperty("poprunner.server.address", null); 
		timeout = p.getIntProperty("poprunner.server.timeout", DEFAULT_TIMEOUT);
		
		key = p.getProperty("poprunner.server.crypto.key", null);
	//	cert = p.getProperty("poprunner.server.crypto.cert", null);
		initVector = Util.resize(p.getProperty("poprunner.server.crypto.iv"));
		
		if (key == null) { 
			globalLogger.error("No security credentials provided!");
			System.exit(1);
		}
		
		if (bindAddr == null || bindAddr.length() == 0) { 
			bindAddr = "localhost";
		}
		
		globalLogger.info("Socket for incoming connections: " + bindAddr + ":" + port);
		globalLogger.info("Timeout set to " + timeout);

		try { 
			ss = Util.createServer(bindAddr, port);
		} catch (Exception e) {
			globalLogger.error("ServerSocket creation failed!", e);
			throw e;
		}
		
		globalLogger.info("Socket created succesfully");
		
	}
	
	public void run() { 
		
		globalLogger.info("Waiting for incoming connections...");

		while (true) { 
			try {
				Util.accept(ss, DEFAULT_TIMEOUT, runner, key, initVector);
			} catch (Exception e) {
				globalLogger.error("Failed to accept incoming connection!", e);
				return;
			}
		}
	}
	
	
	public static void main(String [] args) { 
		
		// Single argument pointing to configuration file.
		if (args.length != 1) { 
			System.err.println("Usage: POPRunner [configuration file]");
			System.exit(1);
		}
		
		File file = new File(args[0]);
		
		if (!(file.exists() && file.isFile() && file.canRead())) { 
			System.err.println("Cannot read configuration file: " + args[1]);
			System.exit(1);
		}
		
		DeployProperties p = new DeployProperties();
		p.loadFromFile(args[0]);

		try { 
			POPRunner runner = new POPRunner(p);
			Thread t = new Thread(runner);
			t.start();
			
			Server s = new Server(p, runner);
			s.run();
			
		} catch (Exception e) {
			System.err.println("SimpleServer failed unexpectedly!: " + e.getLocalizedMessage());
			e.printStackTrace(System.err);
		}
	}
}
