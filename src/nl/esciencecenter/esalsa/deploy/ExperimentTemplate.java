package nl.esciencecenter.esalsa.deploy;

import java.io.Serializable;

public class ExperimentTemplate extends StoreableObject implements Serializable {

	private static final long serialVersionUID = 6381589133534903106L;
	
	public final String configuration;
	public final String worker;
	public final String inputs; 
	public final int resubmits;
	
	public ExperimentTemplate(String ID, String configuration, String worker, String inputs, int resubmits, String comment) {
		
		super(ID, comment);

		this.configuration = configuration;
		this.worker = worker;
		this.inputs = inputs;
		this.resubmits = resubmits;		
		
		System.out.println("Created Template " + ID + " " + configuration + " " + worker + " " + resubmits + " " + inputs + " " + comment);		
	}
}
