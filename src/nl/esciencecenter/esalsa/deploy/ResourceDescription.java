package nl.esciencecenter.esalsa.deploy;

public class ResourceDescription {

	public final String URI;
	public final String username;
	public final String userkey;
	public final String [] adaptors;
	
	public final ResourceDescription gateway;

	public ResourceDescription(String URI, String username, String userkey, String[] adaptors, ResourceDescription gateway) {
		this.URI = URI;
		this.username = username;
		this.userkey = userkey;
		this.adaptors = adaptors;
		this.gateway = gateway;
	}

	public ResourceDescription(String URI) {
		this(URI, null, null, null, null);
	}
	
	public ResourceDescription(String URI, String username, String userkey, String[] adaptors) {
		this(URI, username, userkey, adaptors, null);		
	}

	public ResourceDescription(ResourceDescription other) {
		this(other.URI, other.username, other.userkey, other.adaptors, other.gateway);
	}
	
	public ResourceDescription(String URI, ResourceDescription other) {
		this(URI, other.username, other.userkey, other.adaptors, other.gateway);
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

