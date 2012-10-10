package nl.esciencecenter.esalsa.deploy.ui.swing;

import nl.esciencecenter.esalsa.deploy.ExperimentInfo;
import nl.esciencecenter.esalsa.deploy.server.SimpleStub;

@SuppressWarnings("serial")
public class WaitingExperimentViewer extends ExperimentViewer {
	
	class StartHandler implements ButtonHandler {

		@Override
		public void clicked() {
			start();
		}		
	}
	
	public WaitingExperimentViewer(RootPanel parent, SimpleStub stub, RemoteStore<ExperimentInfo> store) { 
		super(parent, stub, store);
		addButton("Start", new StartHandler());
	}
	
	private void start() { 
		
		System.out.println("Got Start");
		
		String ID = (String) getElementValue("ID");

		if (ID == null || ID.trim().length() == 0) {
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
				showError("Failed to start experiment " + ID + "!", e);			
				return;
			}

			parent.refresh("waiting");
			parent.refresh("running");
		}
	}	
}

