package nl.esciencecenter.esalsa.deploy;

import java.io.Serializable;

public class StoreableObject implements Serializable {
	
	private static final long serialVersionUID = -1859876463445764959L;

	public final String ID;
	private String comment;
		
	public StoreableObject(String ID, String comment) {
		this.ID = ID;
		this.comment = comment;
	}

	public String getID() {
		return ID;
	}
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
