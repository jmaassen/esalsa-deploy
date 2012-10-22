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
				System.err.println("Failed to save! " + ex.getLocalizedMessage());
				ex.printStackTrace(System.err);
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
		
	
	
	public void showError(String key, String message) { 
	
		if (key == null) { 
			return;
		}
		
		EditorField field = elements.get(key);
		
		if (field == null)  { 
			return;
		}
		
		field.setError(message);		
	}	
	
	public boolean checkForCorrectness() { 
		
		//System.out.println("Editor.checkForCorrectness()");
		
		boolean result = true;
		
		for (EditorField ef : elements.values()) { 			
			if (!ef.checkCorrectness()) { 
				result = false;
			} 
		}
		
		//System.out.println("Editor.checkForCorrectness() result = " + result);
		
		return result;		
	}
	
	
	public boolean checkForEmptyFields() { 
		
		boolean result = false;
		
		for (EditorField ef : elements.values()) { 
			
			if (!ef.mayBeEmpty && ef.isEmpty()) { 
				ef.setError("Field cannot be empty!");
				result = true;
			} 
		}
		
		return result;
	}
	
	public abstract void show(T elt);	
	public abstract void save();
}
