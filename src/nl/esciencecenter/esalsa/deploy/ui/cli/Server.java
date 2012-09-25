package nl.esciencecenter.esalsa.deploy.ui.cli;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

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
	
	private final POPRunnerInterface runner;
	
	private final ServerSocket ss;
	
	public Server(DeployProperties p, POPRunnerInterface runner) throws Exception { 
		
		this.runner = runner;		
		
		port = p.getIntProperty("poprunner.server.port", DEFAULT_PORT); 
		String bindAddr = p.getProperty("poprunner.server.address", null); 
		timeout = p.getIntProperty("poprunner.server.timeout", DEFAULT_TIMEOUT);
		
		globalLogger.info("Socket for incoming connections: " + (bindAddr == null ? "localhost" : bindAddr) + ":" + port);
		globalLogger.info("Timeout set to " + timeout);
		
		try { 
			if (bindAddr == null) {
				bindAddr = "localhost";
			}
				
			ss = new ServerSocket();
			ss.bind(new InetSocketAddress(bindAddr, port));
			ss.setSoTimeout(DEFAULT_TIMEOUT);
			
		} catch (Exception e) {
			globalLogger.error("Socket creation failed!", e);
			throw e;
		}
		
		globalLogger.info("Socket created succesfully");
		
	}
	
	
	private void close(Socket s, InputStream in, OutputStream out) { 
		
		if (out != null) { 
			try { 
				out.close();
			} catch (Exception e) {
				// ignored
			}
		} 
		
		if (in != null) { 
			try { 
				in.close();
			} catch (Exception e) {
				// ignored
			}
		} 
		
		if (s != null) { 
			try { 
				s.close();
			} catch (Exception e) {
				// ignored
			}
		}
	}
	
	
	public void run() { 
		
		while (true) { 

			Socket s = null;
			
			try {
				globalLogger.info("Waiting for incoming connection...");
				s = ss.accept();
			} catch (SocketTimeoutException e) {
				// allowed
			} catch (IOException e) {
				globalLogger.error("Failed to accept incoming connection!", e);
				return;
			}
			
			if (s != null) { 
				globalLogger.info("Incoming connection from " + s.getInetAddress());

				try {
					new Proxy(s, runner);
				} catch (Exception e) {
					globalLogger.warn("Failed to create connection!", e);
					close(s, null, null);
				}
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
