package nl.esciencecenter.esalsa.deploy.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import nl.esciencecenter.esalsa.deploy.POPRunnerInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {	
	
	private static String DEFAULT_INIT_VECTOR = "DEADbeef!@#$%^&*";
	
	private static Logger globalLogger = LoggerFactory.getLogger("eSalsa");
	
	public static ServerSocket createServer(String bindAddr, int port) throws Exception { 
		
		if (bindAddr == null) {
			bindAddr = "localhost";
		}
				
		ServerSocket ss = new ServerSocket();
		ss.bind(new InetSocketAddress(bindAddr, port));
		
		return ss;		
	}
	
	public static Proxy accept(ServerSocket server, int timeout, POPRunnerInterface runner) throws Exception { 
		
		Socket s = null;
		
		try {
			server.setSoTimeout(timeout);
			s = server.accept();
		} catch (SocketTimeoutException e) {
			// allowed
		} 
		
		if (s != null) { 
			globalLogger.info("Incoming connection from " + s.getInetAddress());

			try {
				s.setTcpNoDelay(true);			
				return new Proxy(s, s.getInputStream(), s.getOutputStream(), runner);
			} catch (Exception e) {
				globalLogger.warn("Failed to set up proxy!", e);
				close(s, null, null);
			}
		}
		
		return null;
	}
	
	public static Proxy accept(ServerSocket server, int timeout, POPRunnerInterface runner,  String key, String initVector) throws Exception { 

		if (key == null) { 
			throw new Exception("No encryption key provided!");
		}
		
		if (key.length() < 8) {
			throw new Exception("Encryption key too short!");
		}

		initVector = resize(initVector);
		key = resize(key);
		
		Socket s = null;
		
		try {
			server.setSoTimeout(timeout);
			s = server.accept();
		} catch (SocketTimeoutException e) {
			// allowed
		} 
		
		if (s != null) { 
			globalLogger.info("Incoming connection from " + s.getInetAddress());

			try { 
				s.setTcpNoDelay(true);

				globalLogger.info("Initializing secret key cryptography for new connection.");
				
				SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
				IvParameterSpec ivSpec = new IvParameterSpec(initVector.getBytes());

				Cipher encrypt = Cipher.getInstance("AES/CFB8/NoPadding");
				encrypt.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

				Cipher decrypt = Cipher.getInstance("AES/CFB8/NoPadding");
				decrypt.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

				globalLogger.info("Cryptography initialization completed.");			
				
				InputStream in = new CipherInputStream(s.getInputStream(), decrypt);
				OutputStream out = new CipherOutputStream(s.getOutputStream(),encrypt);
			
				return new Proxy(s, in, out, runner);
			} catch (Exception e) {
				globalLogger.warn("Failed to create connection to " + s.getInetAddress(), e);
				close(s, null, null);
			}
		}
		
		return null;
	}
	
	
	
	
	public static void close(Socket s, InputStream in, OutputStream out) { 
		
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
	
	public static SimpleStub connect(String serverAddress, int port, int timeout) throws Exception {

		globalLogger.info("Connecting to " + serverAddress + ":" + port);			
		
		Socket socket = new Socket();
		socket.connect(new InetSocketAddress(serverAddress, port), timeout);
		socket.setTcpNoDelay(true);

		globalLogger.info("Connection created succesfully!");			

		return new SimpleStub(socket, socket.getInputStream(), socket.getOutputStream());
	} 

	public static SimpleStub connect(String serverAddress, int port, int timeout, String key, String initVector) throws Exception {
		
		if (key == null) { 
			throw new Exception("No encryption key provided!");
		}
		
		if (key.length() < 8) {
			throw new Exception("Encryption key too short!");
		}
		
		initVector = resize(initVector);
		key = resize(key);
		
		globalLogger.info("Initializing secret key cryptography for connection to " + serverAddress + ":" + port);
		
		SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(initVector.getBytes());

		Cipher encrypt = Cipher.getInstance("AES/CFB8/NoPadding");
		encrypt.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
				
		Cipher decrypt = Cipher.getInstance("AES/CFB8/NoPadding");
		decrypt.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

		globalLogger.info("Cryptography initialization completed.");			
		
		globalLogger.info("Connecting to " + serverAddress + ":" + port);			
		
		Socket socket = new Socket(serverAddress, port);
		socket.setSoTimeout(timeout);
		socket.setTcpNoDelay(true);
		
		globalLogger.info("Connection created succesfully!");			
		
		InputStream in = new CipherInputStream(socket.getInputStream(), decrypt);
		OutputStream out = new CipherOutputStream(socket.getOutputStream(),encrypt);
		
		return new SimpleStub(socket, in, out);
	}
	
	public static String resize(String in) { 

		if (in == null || in.length() == 0) { 
			return DEFAULT_INIT_VECTOR;
		}
		
		while (in.length() < 16) { 
			in = in + in;
		}
		
		if (in.length() > 16) { 
			return in.substring(0, 16);
		} else {  
			return in;
		}
	}
	
	
}
