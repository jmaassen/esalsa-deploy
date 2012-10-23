package nl.esciencecenter.esalsa.deploy.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleStub implements Protocol {

	private static Logger globalLogger = LoggerFactory.getLogger("eSalsa");
	
	private final Socket socket;
	private final ObjectInputStream in;
	private final ObjectOutputStream out;
	
	public static int getKey(String name) throws Exception { 
		
		if (name.equalsIgnoreCase("worker")) { 
			return Protocol.WORKER;
		} else if (name.equalsIgnoreCase("inputs")) { 
			return Protocol.INPUTS;
		} else if (name.equalsIgnoreCase("configuration")) { 
			return Protocol.CONFIG;
		} else if (name.equalsIgnoreCase("experiment")) {			
			return Protocol.EXPERIMENT;
		} else if (name.equalsIgnoreCase("waiting")) { 
			return Protocol.WAITING;		
		} else if (name.equalsIgnoreCase("running")) { 
			return Protocol.RUNNING;
		} else if (name.equalsIgnoreCase("completed")) { 
			return Protocol.COMPLETED;
		}
		
		throw new Exception("Unknown key: " + name);
	}
	
	
	public SimpleStub(Socket socket, InputStream in, OutputStream out) throws IOException { 
		this.socket = socket;
		this.in = new ObjectInputStream(new BufferedInputStream(in));
		this.out = new ObjectOutputStream(new BufferedOutputStream(out));
		this.out.flush();
	}
	
	public void close() { 
		
		globalLogger.debug("Closing connection to " + socket.getRemoteSocketAddress());
		
		try { 
			out.writeByte(EXIT);
			out.writeByte(0);
			out.writeObject(null);
			out.flush();
			out.close();
		} catch (Exception e) {
			// ignored
		}
		
		try { 
			in.close();
		} catch (Exception e) {
			// ignored
		}
		
		try { 
			socket.close();
		} catch (Exception e) {
			// ignored
		}
	}
	
	private Object rpc(int opcode, int type, Object data) throws Exception { 
		
		globalLogger.debug("Sending RPC " + opcode + " " + type + " " + data);
		
		out.writeByte(opcode);
		out.writeByte(type);
		out.writeObject(data);
		out.flush();
		
		int status = in.readInt();
		Object result = in.readObject();
		
		globalLogger.debug("Received reply " + status + " " + result);
		
		if (status == 0) { 
			return result;
		} else {
			throw (Exception) result;
		}		
	} 
	
	public List<String> list(int type) throws Exception {
		return (List<String>) rpc(LIST, type, null);
	}

	public void add(int type, Object data) throws Exception {

		if (type == RUNNING || type == COMPLETED) { 
			throw new Exception("Illegal add of type " + type + "!");
		}
		
		rpc(ADD, type, data);
	}
		
	public void remove(int type, String ID) throws Exception {
		rpc(REMOVE, type, ID);
	}

	public Object get(int type, String ID) throws Exception {
		return rpc(GET, type, ID);
	}
	
	public String create(String ID) throws Exception {
		return (String) rpc(CREATE, 0, ID);
	}
	
	
	public void start(String ID) throws Exception {
		rpc(START, 0, ID);
	}
	
	public void stop(String ID) throws Exception {
		rpc(STOP, 0, ID);
	}
}
