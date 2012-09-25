package nl.esciencecenter.esalsa.deploy;

import java.io.Serializable;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

public class FileSet extends MarkableObject implements Serializable {

	private static final long serialVersionUID = -340371175122825906L;
	
	private final LinkedList<URI> files; 
	
	public FileSet(String ID, LinkedList<URI> files) { 
		super(ID);
		this.files = files;
	}
	
	public List<URI> getFiles() { 
		return files;
	}
}
