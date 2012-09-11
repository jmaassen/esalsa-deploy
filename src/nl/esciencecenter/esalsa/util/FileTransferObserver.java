package nl.esciencecenter.esalsa.util;

public interface FileTransferObserver {
	public void update(FileTransferDescription description, boolean done, boolean cancelled, Exception exception);
}
