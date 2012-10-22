package nl.esciencecenter.esalsa.deploy.parser;

public class ParseException extends Exception {

	private static final long serialVersionUID = 119211535446638546L;

	private final String source;
	private final int line;
	
	public ParseException(String source, int line, String message) { 
		super(message);
		this.line = line;
		this.source = source;
	}
	
	public ParseException(String source, int line, String message, Throwable cause) { 
		super(message, cause);
		this.line = line;
		this.source = source;
	}
	
	public int getLine() { 
		return line;
	}
	
	public String getSource() { 
		return source;
	}

}
