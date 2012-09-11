package nl.esciencecenter.esalsa.deploy;

public class ExperimentInstance {

	public final String ID; 

	private final WorkerDescription worker; 
	private final String configuration;
	private final FileSet input;
	private final FileSet output;
	
	
	
	public ExperimentInstance(String ID, WorkerDescription worker, String configuration, FileSet input, FileSet output) {
		super();
		
		this.ID = ID;
		this.worker = worker;
		this.configuration = configuration;
		this.input = input;
		this.output = output;
	}
	
	
}
