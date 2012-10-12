package nl.nlesc.esalsa.deploy.ws;

import java.net.URL;
import java.security.ProtectionDomain;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class EmbeddedServer {

	static Server server;

	public static void main(String args[]) throws Exception {
		int port = Integer.parseInt(System.getProperty("port", "8088"));
		server = new Server(port);
		WebAppContext webapp = new WebAppContext("webapp/webapp", "/");
		server.setHandler(webapp);
		server.start();
		server.join();
	}
}
