package nl.esciencecenter.esalsa.deploy.ui.swing;

import nl.esciencecenter.esalsa.deploy.ExperimentInfo;
import nl.esciencecenter.esalsa.deploy.server.SimpleStub;

@SuppressWarnings("serial")
public class ExperimentViewer extends Viewer<ExperimentInfo> {

	public ExperimentViewer(RootPanel parent, SimpleStub stub, RemoteStore<ExperimentInfo> store) { 	
		super(parent, stub, store, false);
		addField(new TextLineField("State", false));
		addField(new TextAreaField("Log", "Log", true, false, -1, 15*Utils.defaultFieldHeight));
	}

	@Override
	public void show(ExperimentInfo elt) {
		setElementValue("ID", elt.ID);
		setElementValue("Comment", elt.getComment());
		setElementValue("State", elt.state);
		setElementValue("Log", elt.log);
	}	
}
