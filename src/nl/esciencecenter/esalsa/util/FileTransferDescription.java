package nl.esciencecenter.esalsa.util;

import java.net.URI;

public class FileTransferDescription {

	public final URI from;
	public final URI to;
	
	public FileTransferDescription(URI from, URI to) {
		this.from = from;
		this.to = to;
	}
	
	public String toString() { 
		return from + " -> " + to;
	}
}
