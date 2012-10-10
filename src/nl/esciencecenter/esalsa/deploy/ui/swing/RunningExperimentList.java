package nl.esciencecenter.esalsa.deploy.ui.swing;

import nl.esciencecenter.esalsa.deploy.ExperimentInfo;
import nl.esciencecenter.esalsa.deploy.server.SimpleStub;

@SuppressWarnings("serial")
public class RunningExperimentList extends StoreListView<ExperimentInfo> {

	class StopHandler implements ButtonHandler {
		@Override
		public void clicked() {
			stop();
		}		
	}
	
	public RunningExperimentList(RootPanel parent, SimpleStub stub, RemoteStore<ExperimentInfo> store, Viewer<ExperimentInfo> viewer) { 
		super(parent, stub, store, viewer, false);
		addButton("Stop", new StopHandler());
	}
	
	private void stop() { 
		
		System.out.println("Got Stop");
		
		String ID = (String)  list.getSelectedValue();

		if (ID == null) {
			return;
		}
			
		if (!store.contains(ID)) { 
			return;
		}
		
		boolean confirm = askConfirmation("Are you sure you want to stop experiment " + ID + "?");
		
		if (confirm) { 
			try {
				stub.stop(ID);
				showMessage("Stopped experiment " + ID);
			} catch (Exception e) {			
				showErrorMessage("Failed to stop experiment " + ID + "!", e);			
				return;
			}

			parent.refresh("running");
			parent.refresh("completed");
		}
	}
	
	
}
