package nl.esciencecenter.esalsa.deploy.ui.swing;

import nl.esciencecenter.esalsa.deploy.ExperimentTemplate;
import nl.esciencecenter.esalsa.deploy.server.SimpleStub;

@SuppressWarnings("serial")
public class ExperimentTemplateEditor extends Editor<ExperimentTemplate> {

	public ExperimentTemplateEditor(RootPanel parent, SimpleStub stub, RemoteStore<ExperimentTemplate> store) { 
		super(parent, stub, store);

		addField(new TextLineField("Worker", true));
		addField(new TextLineField("Configuration", true));
		addField(new TextLineField("Input", true));
	}

	@Override
	public void show(ExperimentTemplate exp) {
		setElementValue("ID", exp.ID);
		setElementValue("Comment", exp.getComment());
		setElementValue("Worker", exp.worker);
		setElementValue("Configuration", exp.configuration);
		setElementValue("Input", exp.inputs);		
	}

	@Override
	public void save() {

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
		
		ExperimentTemplate e = new ExperimentTemplate(ID, configuration, worker, inputs, comment);
		
		try {
			store.add(e);
		} catch (Exception e1) {
			showErrorMessage("Failed to store experiment " + ID + "!", e1);
		}
	}	
}