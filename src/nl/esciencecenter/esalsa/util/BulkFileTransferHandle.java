package nl.esciencecenter.esalsa.util;

import java.util.LinkedList;
import java.util.List;

public class BulkFileTransferHandle implements FileTransferObserver {
	
	private final FileTransferHandle [] handles;
	private final boolean failFast;

	public BulkFileTransferHandle(FileTransferHandle [] handles, boolean failFast) { 
		this.handles = handles;
		this.failFast = failFast;
	}
	
	public void waitUntilDone() {
		for (FileTransferHandle h : handles) { 
			h.waitUntilDone();
		}		
	}

	public boolean isDone() {
		
		for (FileTransferHandle h : handles) {
			
			if (!h.waitUntilDone(-1)) { 
				return false;
			}
		}
		
		return true;
	}

	public void cancel() {
		for (FileTransferHandle h : handles) {
			h.cancel();
		}
	}
	
	public List<Exception> getExceptions() {
		
		LinkedList<Exception> result = new LinkedList<Exception>();

		for (FileTransferHandle fth : handles) { 
			
			Exception tmp = fth.getException();
			
			if (tmp != null) { 
				result.add(tmp);
			}
		}
		
		return result;
	}

	@Override
	public void update(FileTransferDescription description, boolean done, boolean cancelled, Exception exception) {

		if (failFast && exception != null) { 
			for (FileTransferHandle h : handles) { 
				h.cancel();
			}
		}
	}

}
