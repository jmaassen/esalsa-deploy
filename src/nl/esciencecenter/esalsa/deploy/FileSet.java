package nl.esciencecenter.esalsa.deploy;

import java.util.LinkedList;

import org.gridlab.gat.URI;

public class FileSet extends MarkableObject {

	private final LinkedList<URI> files; 
	
	public FileSet(String ID, LinkedList<URI> files) { 
		super(ID);
		this.files = files;
	}
}
