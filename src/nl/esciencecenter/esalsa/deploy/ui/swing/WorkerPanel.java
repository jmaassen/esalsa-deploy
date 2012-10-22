package nl.esciencecenter.esalsa.deploy.ui.swing;

import nl.esciencecenter.esalsa.deploy.WorkerDescription;
import nl.esciencecenter.esalsa.deploy.server.SimpleStub;

public class WorkerPanel extends StorePanel<WorkerDescription> {

	
	
	
	public WorkerPanel(RemoteStore<WorkerDescription> store, SimpleStub stub) {

		super(list, viewer);

		
		Editor<WorkerDescription> workerEditor = new WorkerEditor(this, stub, store);
		StoreListView<WorkerDescription> workerList = new StoreListView<WorkerDescription>(this, stub, store, workerEditor, true);
		workerEditor.setViewer(workerList);
		
	}
	
}
