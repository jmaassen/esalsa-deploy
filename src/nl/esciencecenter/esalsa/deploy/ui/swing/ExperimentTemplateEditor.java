package nl.esciencecenter.esalsa.deploy.ui.swing;

import javax.swing.JOptionPane;

import nl.esciencecenter.esalsa.deploy.ExperimentTemplate;
import nl.esciencecenter.esalsa.deploy.server.SimpleStub;

@SuppressWarnings("serial")
public class ExperimentTemplateEditor extends Editor<ExperimentTemplate> {

	class CreateHandler implements ButtonHandler {
		@Override
		public void clicked() {
			create();
		}
	}
	
	public ExperimentTemplateEditor(RootPanel parent, SimpleStub stub, RemoteStore<ExperimentTemplate> store) { 
		super(parent, stub, store);

		addField(new TextLineField("Worker", true));
		addField(new TextLineField("Configuration", true));
		addField(new TextLineField("Input", true));
		
		addButton("Create", new CreateHandler());
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
			System.err.println("Failed to store experiment! (" + e1.getLocalizedMessage() + ")");
			e1.printStackTrace(System.err);
			JOptionPane.showMessageDialog(this, "Failed to store experiment!\n(" + e1.getLocalizedMessage() + ")");
		}
	}

	private void create() { 
		
		System.out.println("Got Create");
		
		String ID = (String) getElementValue("ID");

		if (ID == null || ID.trim().length() == 0) {
			return;
		}
			
		if (!store.contains(ID)) { 
			return;
		}
		
		try {
			String newID = stub.create(ID);
			JOptionPane.showMessageDialog(this, "Created new experiment " + newID);
			
		} catch (Exception e) {
			System.err.println("Failed to create experiment using template " + ID + " ! (" + e.getLocalizedMessage() + ")");
			e.printStackTrace(System.err);
			JOptionPane.showMessageDialog(this, "Failed to create experiment using template " + ID + " !\n(" + e.getLocalizedMessage() + ")");
		}

		try { 
			parent.refresh("waiting");
		} catch (Exception e) {
			System.err.println("Failed to refresh waiting experiment store! (" + e.getLocalizedMessage() + ")");
			e.printStackTrace(System.err);
		}
	}		
}