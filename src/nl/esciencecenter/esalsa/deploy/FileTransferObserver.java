package nl.esciencecenter.esalsa.deploy;

public interface FileTransferObserver {
	public void update(FileTransferDescription description, boolean done, boolean cancelled, Exception exception);
}
