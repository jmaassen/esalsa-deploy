package nl.esciencecenter.esalsa.deploy.ui.swing;

import nl.esciencecenter.esalsa.deploy.ExperimentTemplate;
import nl.esciencecenter.esalsa.deploy.server.SimpleStub;

@SuppressWarnings("serial")
public class ExperimentTemplateList extends StoreListView<ExperimentTemplate> {

	class CreateHandler implements ButtonHandler {
		@Override
		public void clicked() {
			create();
		}		
	}
	
	public ExperimentTemplateList(RootPanel parent, SimpleStub stub, RemoteStore<ExperimentTemplate> store, Viewer<ExperimentTemplate> viewer) { 
		super(parent, stub, store, viewer, true);
		addButton("Create", new CreateHandler());
	}
		
	private void create() { 
	
		//System.out.println("Got Create");
		
		String ID = (String) list.getSelectedValue();

		if (ID == null) {
			return;
		}
			
		if (!store.contains(ID)) { 
			return;
		}
		
		try {
			String newID = stub.create(ID);			
			showMessage("Created new experiment " + newID);			
		} catch (Exception e) {
			showErrorMessage("Failed to create experiment using template " + ID + "!", e);
		}

		parent.refresh("waiting");
	}		

}
