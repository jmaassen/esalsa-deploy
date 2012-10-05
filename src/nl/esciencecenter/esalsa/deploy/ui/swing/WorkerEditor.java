package nl.esciencecenter.esalsa.deploy.ui.swing;

import nl.esciencecenter.esalsa.deploy.WorkerDescription;

public class WorkerEditor extends Editor<WorkerDescription> {

	private static final long serialVersionUID = -8580838957929000835L;
	
	public WorkerEditor(RemoteStore<WorkerDescription> store) { //, JobTableModel model) {

		super(store);

		addElement(new TextLineField("URI", true));
		addElement(new TextLineField("Template Directory", true));
		addElement(new TextLineField("Experiment Directory", true));
		addElement(new TextLineField("Input Directory", true));
		addElement(new TextLineField("Output Directory", true));
		addElement(new PropertyField("Additional Properties", true));		
	}

	@Override
	public void show(WorkerDescription elt) {
		setElementValue("ID", elt.ID);
		setElementValue("Comment", elt.getComment());
		setElementValue("URI", elt.jobServer.toString());
		setElementValue("Template Directory", elt.templateDir);
		setElementValue("Experiment Directory", elt.experimentDir);
		setElementValue("Input Directory", elt.inputDir);
		setElementValue("Output Directory", elt.outputDir);
		setElementValue("Additional Properties", elt.getMapping());
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub
		
	}
}