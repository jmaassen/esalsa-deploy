package nl.esciencecenter.esalsa.deploy;

import java.io.Serializable;

public class MarkableObject implements Serializable {
	
	private static final long serialVersionUID = -1859876463445764959L;

	public final String ID;
	
	private boolean inUse = false;
		
	public MarkableObject(String ID) {
		this.ID = ID;
	}

	public synchronized void setInUse(boolean value) { 
		inUse = value;  
	}
	
	public synchronized boolean isInUse() { 
		return inUse;  
	}
}
