package nl.esciencecenter.esalsa.deploy;

public class ExperimentDescription extends MarkableObject {

	public final String configuration;
	public final String worker;
	public final String stageIn; 
	public final String stageOut; 
	
	public ExperimentDescription(String ID, String configuration, String worker, String stageIn, String stageOut) {
		
		super(ID);

		this.configuration = configuration;
		this.worker = worker;
		this.stageIn = stageIn;
		this.stageOut = stageOut;
	}
}
