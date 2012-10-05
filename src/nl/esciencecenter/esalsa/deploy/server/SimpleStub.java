package nl.esciencecenter.esalsa.deploy.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class SimpleStub implements Protocol {

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
		} else if (name.equalsIgnoreCase("running")) { 
			return Protocol.RUNNING;
		} else if (name.equalsIgnoreCase("completed")) { 
			return Protocol.COMPLETED;
		}
		
		throw new Exception("Unknown key: " + name);
	}
	
	
	public SimpleStub(Socket socket) throws IOException { 
		this.socket = socket;
		in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
		out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		out.flush();
	}
	
	public void close() { 
		
		try { 
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
	
		System.err.println("Sending RPC " + opcode + " " + type + " " + data);
		
		out.writeByte(opcode);
		out.writeByte(type);
		out.writeObject(data);
		out.flush();
		
		int status = in.readInt();
		Object result = in.readObject();
		
		System.err.println("Received reply " + status + " " + result);
		
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
	
	public String start(String ID) throws Exception {
		return (String) rpc(START, 0, ID);
	}
	
	public void stop(String ID) throws Exception {
		rpc(STOP, 0, ID);
	}
}
