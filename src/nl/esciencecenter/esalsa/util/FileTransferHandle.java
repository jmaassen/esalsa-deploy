package nl.esciencecenter.esalsa.util;

import java.util.LinkedList;

public class FileTransferHandle {

	public final FileTransferDescription description;
	
	private boolean cancel = false;
	private boolean done = false;
	private Exception exception; 		

	private LinkedList<FileTransferObserver> observers;
	
	public FileTransferHandle(FileTransferDescription description) { 
		this.description = description;
	}
	
	// Wait until the file transfer has finished or failed.
	public void waitUntilDone() {
		waitUntilDone(0);
	}

	// Wait until the file transfer has finished or failed, or until a timeout has expired.
	public synchronized boolean waitUntilDone(long timeout) {
		
		while (!cancel && !done && timeout >= 0) { 
			try {
				wait(timeout);
			} catch (InterruptedException e) {
				// ignored
			}
		}
		
		return done;
	}
	
	// Retrieve the exception produced by the transfer, if any. Will only return a value 
	// if the transfer has finished (and failed).   
	public synchronized Exception getException() {
		
		if (!done) { 
			return null;
		}
		
		return exception;
	}

	// Cancel the transfer (if possible).
	public synchronized void cancel() {
		if (!done) { 
			cancel = true;
			notifyAll();
		}
	}	
	
	private void informObservers() { 
		if (observers != null) { 
			for (FileTransferObserver o : observers) { 
				o.update(description, done, cancel, exception);
			}
			
			observers = null;
		}
	}
	
	public synchronized void done() {
		done = true;
		informObservers();
		notifyAll();
	}
	
	public synchronized void failed(Exception e) {
		done = true;
		exception = e;
		informObservers();
		notifyAll();
	}

	public synchronized boolean isCancelled() {
		return cancel;
	}

	public synchronized void addObserver(FileTransferObserver observer) {
		if (done) { 
			observer.update(description, done, cancel, exception);
		} else { 
			if (observers == null) { 
				observers = new LinkedList<FileTransferObserver>();
			}
	
			observers.add(observer);
		}
	}
}
