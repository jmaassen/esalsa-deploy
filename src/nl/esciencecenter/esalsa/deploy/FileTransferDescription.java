package nl.esciencecenter.esalsa.deploy;

public class FileTransferDescription {

	public final FileDescription from;
	public final FileDescription to;
	
	public FileTransferDescription(FileDescription from, FileDescription to) {
		this.from = from;
		this.to = to;
	}
	
	public String toString() { 
		return from.file.URI + " -> " + to.file.URI;
	}
}
