package nl.esciencecenter.esalsa.deploy;

public class ResourceDescription {

	public final String URI;
	public final String username;
	public final String userkey;
	public final String [] adaptors;
	
	public ResourceDescription(String URI) {
		this(URI, null, null, null);
	}
	
	public ResourceDescription(String URI, String username, String userkey, String[] adaptors) {
		this.URI = URI;
		this.username = username;
		this.userkey = userkey;
		this.adaptors = adaptors;
	}

	public ResourceDescription(ResourceDescription other) {
		this.URI = other.URI;
		this.username = other.username;
		this.userkey = other.userkey;
		this.adaptors = other.adaptors;
	}
	
	public ResourceDescription(String URI, ResourceDescription other) {
		this.URI = URI;
		this.username = other.username;
		this.userkey = other.userkey;
		this.adaptors = other.adaptors;
	}
	
	@Override
	public int hashCode() {
		return URI.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj) return true;
		if (obj == null) return false;		
		if (getClass() != obj.getClass()) return false;
		
		ResourceDescription other = (ResourceDescription) obj;
		
		if (URI == null) return (other.URI == null);  

		return URI.equals(other.URI);
	}
	
	
}

