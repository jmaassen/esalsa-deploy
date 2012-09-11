package nl.esciencecenter.esalsa.util;

import java.util.StringTokenizer;

public class Event { 

	public final String tag;
	public final String ID;
	public final String state;
	public final String message;

	public Event(String tag, String ID, String state, String message) {
		this.tag = tag;
		this.ID = ID;
		this.state = state;
		this.message = message;
	}

	public boolean match(String tag, String ID, String state, String message) {

		if (tag != null && !tag.equals(this.tag)) { 
			return false;
		}
		
		if (ID != null && !ID.equals(this.ID)) { 
			return false;
		}
		
		if (state != null && !state.equals(this.state)) { 
			return false;
		}
	
		if (message != null && !message.equals(this.message)) { 
			return false;
		}
		
		return true;
	}

	public static Event parse(String s) throws Exception { 
		
		StringTokenizer tok = new StringTokenizer(s);
		
		if (tok.countTokens() < 4) { 
			throw new Exception("Failed to parse event: number of tokens too small!");
		} 
		
		String tag = tok.nextToken();
		String ID = tok.nextToken(); 
		String state = tok.nextToken();
		
		String message = tok.nextToken();
		
		while (tok.hasMoreTokens()) {
			message += " " + tok.nextToken();
		}
		
		return new Event(tag, ID, state, message);
	}

	
	
	@Override
	public String toString() {
		return tag + " " + ID + " " + state + " " + message;
	}
}

