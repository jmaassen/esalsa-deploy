package nl.esciencecenter.esalsa.deploy.server;

public interface Protocol {
	
	public final static byte ADD    = 1;
	public final static byte GET    = 2;
	public final static byte REMOVE = 3;
	public final static byte LIST   = 4;
	
	public final static byte CREATE = 5;
	public final static byte START  = 6;
	public final static byte STOP   = 7;
	public final static byte EXIT   = 8;
	
	public final static byte WORKER     = 1;
	public final static byte INPUTS     = 2;
	public final static byte CONFIG     = 3;
	public final static byte EXPERIMENT = 4;
	public final static byte WAITING    = 5;
	public final static byte RUNNING    = 6;
	public final static byte COMPLETED  = 7;
}
