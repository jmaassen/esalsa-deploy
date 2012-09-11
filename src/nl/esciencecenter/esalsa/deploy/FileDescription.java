package nl.esciencecenter.esalsa.deploy;

public class FileDescription {

	public final ResourceDescription file;
	public final ResourceDescription gateway;
	
	public FileDescription(ResourceDescription file, ResourceDescription gateway) {
		this.file = file;
		this.gateway = gateway;
	}
	
	public String toString() { 
		return "[" + file.URI + " / " + (gateway == null ? "(no gateway)" : gateway.URI) + "]";
	}

	@Override
	public int hashCode() {
		return file.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;

		FileDescription other = (FileDescription) obj;
		
		if (file == null) return (other.file == null);
		
		return file.equals(other.file);
	}
}
