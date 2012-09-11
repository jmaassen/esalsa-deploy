package nl.esciencecenter.esalsa.deploy;

public class MarkableObject {
	
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
