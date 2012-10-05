package nl.esciencecenter.esalsa.deploy.ui.swing;

import nl.esciencecenter.esalsa.deploy.ExperimentInfo;

public class ExperimentViewer extends Viewer<ExperimentInfo> {

	private static final long serialVersionUID = -8580838957929000835L;
	
	public ExperimentViewer(RemoteStore<ExperimentInfo> store) { 
	
		super(store, false);

		addElement(new TextLineField("State", false));
		addElement(new TextAreaField("Log", "Log", true, false, -1, 15*Utils.defaultFieldHeight));
	}

	@Override
	public void show(ExperimentInfo elt) {
		setElementValue("ID", elt.ID);
		setElementValue("Comment", elt.getComment());
		setElementValue("State", elt.state);
		setElementValue("Log", elt.log);
	}
}
