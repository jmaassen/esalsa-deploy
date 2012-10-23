package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.Component;

import nl.esciencecenter.esalsa.deploy.StoreableObject;
import nl.esciencecenter.esalsa.deploy.server.SimpleStub;

@SuppressWarnings("serial")
public abstract class Viewer<T extends StoreableObject> extends MyStorePanel<T> {

	protected StoreListView<T> listView;
	
	protected Viewer(RootPanel parent, SimpleStub stub, RemoteStore<T> store, boolean editable) { 
		
		super(parent, stub, store);
		
	    addField(new TextLineField("ID", editable));
		addField(new TextAreaField("Comment", "Comment", true, editable, false, -1, 5*Utils.defaultFieldHeight));
	}

	protected void setViewer(StoreListView<T> viewer) {
		this.listView = viewer;
	}
	
	protected void clear() {
		
		if (listView != null) { 
			listView.clear();
		}
		
		if (elements.size() == 0) { 
			return;
		}
 		
		for (EditorField ef : elements.values()) { 
			ef.clear();
		}
	}
	
	public void show(String ID) {
		
		if (ID == null) { 
			return;
		}
	
		T elt = null;
		
		try { 
			elt = store.get(ID);
		} catch (Exception ex) {
			GUI.globalLogger.error("Failed retrieve " + ID + " from store! ", ex);
			return;
		} 
		
		if (elt != null) { 
			show(elt);
		}
	}
	
	public void setEnabled(boolean value) { 
		
		//System.out.println("Viewer setEnables(" + value + ")");
		
		super.setEnabled(value);
		
		for (Component tmp : formPanel.getComponents()) { 
			tmp.setEnabled(value);
		}
	}
	
	protected abstract void show(T elt);
}
