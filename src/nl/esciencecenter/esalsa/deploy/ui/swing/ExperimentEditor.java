package nl.esciencecenter.esalsa.deploy.ui.swing;

import nl.esciencecenter.esalsa.deploy.ExperimentDescription;

public class ExperimentEditor extends Editor<ExperimentDescription> {

	private static final long serialVersionUID = -8580838957929000835L;
	
	public ExperimentEditor(RemoteStore<ExperimentDescription> store) { 

		super(store);
		
		addElement(new TextLineField("Worker", true));
		addElement(new TextLineField("Configuration", true));
		addElement(new TextLineField("Input", true));
	}

	@Override
	public void show(ExperimentDescription exp) {
		setElementValue("ID", exp.ID);
		setElementValue("Comment", exp.getComment());
		setElementValue("Worker", exp.worker);
		setElementValue("Configuration", exp.configuration);
		setElementValue("Input", exp.inputs);		
	}

	@Override
	public void save() throws Exception {

		if (checkForEmptyFields()) { 
			return;
		}
		
		String ID = (String) getElementValue("ID");
		
		if (store.contains(ID)) { 
			showError("ID", "Field must be unique!");
			return;
		}

		String configuration = (String) getElementValue("Configuration");
		String worker = (String) getElementValue("Worker");
		String inputs = (String) getElementValue("Input");
		String comment = (String) getElementValue("Comment");
		
		ExperimentDescription e = new ExperimentDescription(ID, configuration, worker, inputs, comment);
		store.add(e);
	}
}