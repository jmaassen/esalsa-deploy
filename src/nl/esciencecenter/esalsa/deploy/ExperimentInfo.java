package nl.esciencecenter.esalsa.deploy;

import java.io.Serializable;

public class ExperimentInfo extends MarkableObject implements Serializable {

	private static final long serialVersionUID = 5478689255341274192L;
	
	public final String experimentDescriptionID;
	public final String state;
	public final String log;
	
	public ExperimentInfo(String ID, String experimentDescriptionID, String state, String log) {
		super(ID);
		this.experimentDescriptionID = experimentDescriptionID;
		this.state = state;
		this.log = log;
	}

	@Override
	public String toString() {
		return "Experiment: " + experimentDescriptionID + ", state =" + state + ", log:\n"
				+ log + "\n\n";
	}
}
