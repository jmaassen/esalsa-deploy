package nl.esciencecenter.esalsa.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FileTransferService {
	
	private static final Logger globalLogger = LoggerFactory.getLogger("eSalsa");
	
	// Simple thread used for asynchronous file transfers.
	private class FileTransferThread extends Thread {
		
		public FileTransferThread(int number) { 
			super("FileTransfer-" + number);
		}
		
		public void run() { 
			while (performNextTransfer());
		}
	}
	
	// List of pending file transfers.
	private final LinkedList<FileTransferHandle> pending = new LinkedList<FileTransferHandle>();
	
	// File transfer history, used to prevent repeated transfers of the same files. 
	private final HashMap<FileDescription, FileTransferHandle> history = new HashMap<FileDescription, FileTransferHandle>();
	
	// The file transfer threads
	private final FileTransferThread [] threads;
	
	// Boolean to signal termination
	private boolean done = false;
	
	public FileTransferService(int threads) {
		
		if (threads < 1) { 
			throw new IllegalArgumentException("FileTransferServie requires at least one thread!");
		}
		
		this.threads = new FileTransferThread[threads];
		
		for (int i=0;i<threads;i++) { 
			this.threads[i] = new FileTransferThread(i);
			this.threads[i].start();
		}
	}
	
	public synchronized void done() { 
		this.done = true;
		notifyAll();
	}
	
	private boolean performNextTransfer() {

		FileTransferHandle handle = dequeue();
		
		if (handle == null) { 
			return false;
		}
		
		performTransfer(handle);
		
		return true;
	}
			
	private void performTransfer(FileTransferHandle handle) {
	
		globalLogger.info("[FileTransferService] Performing file copy: " + handle.description.toString());

		try {
			if (Utils.exists(handle.description.to)) { 
				// File already exists!
				// FIXME: should at least check size etc. 
				handle.done();
				return;
			}

			Utils.copy(handle.description);
			handle.done();
			return;
	
		} catch (Exception e) {
			handle.failed(e);
		}
	}
	
	private synchronized FileTransferHandle dequeue() { 
		
		while (!done && pending.size() == 0) { 
			try { 
				wait();
			} catch (InterruptedException e) {
				// ignored
			}
		}
		
		if (done) { 
			return null;
		}
		
		return pending.removeFirst();
	}
	
	private synchronized BulkFileTransferHandle enqueue(LinkedList<FileTransferHandle> queue, List<FileTransferDescription> descriptions, boolean failFast) { 

		FileTransferHandle [] transfers = new FileTransferHandle[descriptions.size()];
		
		int index = 0;
		
		for (FileTransferDescription ftd : descriptions) { 
			transfers[index++] = enqueue(queue, ftd);
		}
		
		return new BulkFileTransferHandle(transfers, failFast);
	}
	
	private synchronized FileTransferHandle enqueue(LinkedList<FileTransferHandle> queue, FileTransferDescription description) {

		// FIXME: Try to retrieve a matching transfer from the history. Only checks destination URI, so this will fail 
		// miserably if source URI, user credentials or gateway info do not match with existing entry!
		FileTransferHandle t = history.get(description.to);
		
		if (t == null) { 			
			globalLogger.info("[FileTransferService] Queuing file transfer: " + description);			
			t = new FileTransferHandle(description);
			history.put(description.to, t);
			queue.add(t);		
			notifyAll();
		} else { 
			globalLogger.info("[FileTransferService] Skipping file transfer: " + description);
		}
		
		return t;
	}
	
	public BulkFileTransferHandle queue(List<FileTransferDescription> descriptions, boolean failFast) {
		return enqueue(pending, descriptions, failFast);
	}
	
	public FileTransferHandle queue(FileTransferDescription description) {
		return enqueue(pending, description);
	}
}
