package nl.esciencecenter.esalsa.deploy.server;

public interface Protocol {

	/*
	public final static int ADD_CONFIG = 0;
	public final static int GET_CONFIG = 1;
	public final static int REMOVE_CONFIG = 2;
	public final static int LIST_CONFIG = 3;

	public final static int ADD_WORKER = 4;
	public final static int GET_WORKER = 5;
	public final static int REMOVE_WORKER = 6;
	public final static int LIST_WORKER = 7;

	public final static int ADD_INPUTS = 8;
	public final static int GET_INPUTS = 9;
	public final static int REMOVE_INPUTS = 10;
	public final static int LIST_INPUTS = 11;
	
	public final static int ADD_EXPERIMENT = 16;
	public final static int GET_EXPERIMENT = 17;
	public final static int REMOVE_EXPERIMENT = 18;
	public final static int LIST_EXPERIMENT = 19;

	public final static int START_RUNNING = 20;
	public final static int STOP_RUNNING = 21;
	public final static int LIST_RUNNING = 22;
	public final static int GET_RUNNING = 23;

	public final static int LIST_STOPPED = 24;
	public final static int GET_STOPPED = 25;
	public final static int REMOVE_STOPPED = 26;
*/
	
	public final static byte ADD    = 1;
	public final static byte GET    = 2;
	public final static byte REMOVE = 3;
	public final static byte LIST   = 4;
	public final static byte START  = 5;
	public final static byte STOP   = 6;
	public final static byte EXIT   = 7;
	
	public final static byte WORKER     = 1;
	public final static byte INPUTS     = 2;
	public final static byte CONFIG     = 3;
	public final static byte EXPERIMENT = 4;
	public final static byte RUNNING    = 5;
	public final static byte COMPLETED  = 6;

}
