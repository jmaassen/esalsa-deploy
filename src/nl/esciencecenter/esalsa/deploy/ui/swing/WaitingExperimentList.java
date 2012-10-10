package nl.esciencecenter.esalsa.deploy.ui.swing;

import nl.esciencecenter.esalsa.deploy.ExperimentInfo;
import nl.esciencecenter.esalsa.deploy.server.SimpleStub;

@SuppressWarnings("serial")
public class WaitingExperimentList extends StoreListView<ExperimentInfo> {

	class StartHandler implements ButtonHandler {
		@Override
		public void clicked() {
			start();
		}		
	}
	
	public WaitingExperimentList(RootPanel parent, SimpleStub stub, RemoteStore<ExperimentInfo> store, Viewer<ExperimentInfo> viewer) {
		super(parent, stub, store, viewer, true);
		addButton("Start", new StartHandler());	
	}
	
	private void start() { 
		
		System.out.println("Got Start");
		
		String ID = (String) list.getSelectedValue();
		
		if (ID == null) {
			return;
		}
			
		if (!store.contains(ID)) { 
			return;
		}
		
		boolean confirm = askConfirmation("Are you sure you want to start experiment " + ID + "?");
		
		if (confirm) { 

			try {
				stub.start(ID);
				showMessage("Started experiment " + ID);
			} catch (Exception e) {			
				showErrorMessage("Failed to start experiment " + ID + "!", e);			
				return;
			}

			parent.refresh("waiting");
			parent.refresh("running");
		}
	}	
	
}
