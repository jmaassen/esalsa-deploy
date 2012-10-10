package nl.esciencecenter.esalsa.deploy.ui.swing;

import nl.esciencecenter.esalsa.deploy.ExperimentInfo;
import nl.esciencecenter.esalsa.deploy.server.SimpleStub;

@SuppressWarnings("serial")
public class RunningExperimentViewer extends ExperimentViewer {

	class StopHandler implements ButtonHandler {
		@Override
		public void clicked() {
			stop();
		}		
	}
	
	public RunningExperimentViewer(RootPanel parent, SimpleStub stub, RemoteStore<ExperimentInfo> store) { 
		super(parent, stub, store);
		addButton("Stop", new StopHandler());
	}
	
	private void stop() { 
		
		System.out.println("Got Stop");
		
		String ID = (String) getElementValue("ID");

		if (ID == null || ID.trim().length() == 0) {
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
				showError("Failed to stop experiment " + ID + "!", e);			
				return;
			}

			parent.refresh("running");
			parent.refresh("completed");
		}
	}
	
}
