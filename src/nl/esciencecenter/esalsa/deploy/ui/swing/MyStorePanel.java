package nl.esciencecenter.esalsa.deploy.ui.swing;

import nl.esciencecenter.esalsa.deploy.StoreableObject;
import nl.esciencecenter.esalsa.deploy.server.SimpleStub;

@SuppressWarnings("serial")
public class MyStorePanel<T extends StoreableObject> extends MyPanel {
	
	protected RemoteStore<T> store;
	protected final SimpleStub stub;
	
	protected MyStorePanel(RootPanel parent, SimpleStub stub, RemoteStore<T> store) {
		super(parent);
		
		this.store = store;
		this.stub = stub;		
	}
	
}
