package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.event.ActionListener;

import nl.esciencecenter.esalsa.deploy.StoreableObject;
import nl.esciencecenter.esalsa.deploy.server.SimpleStub;

@SuppressWarnings("serial")
public abstract class Editor<T extends StoreableObject> extends Viewer<T> implements ActionListener {
	
	class SaveHandler implements ButtonHandler {
		@Override
		public void clicked() {
			try { 
				save();
			} catch (Exception ex) {
				GUI.globalLogger.error("Failed to save! ", ex);
			}
		}		
	}
	
	class ClearHandler implements ButtonHandler {
		@Override
		public void clicked() {
			clear();
		}		
	}
	
	protected Editor(RootPanel parent, SimpleStub stub, RemoteStore<T> store) { 
		
		super(parent, stub, store, true);
		
		addButton("Save", new SaveHandler());
	    addButton("Clear", new ClearHandler());	    
	}	
	
	protected abstract void save();
}
