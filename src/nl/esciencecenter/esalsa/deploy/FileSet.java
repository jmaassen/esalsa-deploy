package nl.esciencecenter.esalsa.deploy;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

public class FileSet extends StoreableObject {

	private static final long serialVersionUID = -340371175122825906L;
	
	private final LinkedList<URI> files; 
	
	public FileSet(String ID, String comment, LinkedList<URI> files) { 
		super(ID, comment);
		this.files = files;
	}
	
	public FileSet(String ID, String comment, URI [] files) { 
		super(ID, comment);
		this.files = new LinkedList<URI>();
		
		for (URI uri : files) {
			this.files.add(uri);
		}
	}
	
	public List<URI> getFiles() { 
		return files;
	}
	
	public URI [] getFilesAsArray() {
		return files.toArray(new URI[files.size()]);
	}	
}
