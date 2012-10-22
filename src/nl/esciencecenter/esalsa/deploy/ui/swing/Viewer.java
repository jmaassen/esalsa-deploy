package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.LinkedHashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import nl.esciencecenter.esalsa.deploy.StoreableObject;
import nl.esciencecenter.esalsa.deploy.server.SimpleStub;

@SuppressWarnings("serial")
public abstract class Viewer<T extends StoreableObject> extends MyStorePanel<T> {

	protected final JPanel formPanel;
	protected LinkedHashMap<String, EditorField> elements = new LinkedHashMap<String, EditorField>();

	protected StoreListView<T> listView;
	
	protected Viewer(RootPanel parent, SimpleStub stub, RemoteStore<T> store, boolean editable) { 
		
		super(parent, stub, store);
		
		formPanel = new JPanel();
		formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.PAGE_AXIS));
		formPanel.setBorder(new EmptyBorder(5, 5, 5, 5));		
		formPanel.add(Box.createRigidArea(new Dimension(0, Utils.gapHeight)));
		
	    addField(new TextLineField("ID", editable));
		addField(new TextAreaField("Comment", "Comment", true, editable, false, -1, 5*Utils.defaultFieldHeight));
		
		container.add(formPanel, BorderLayout.NORTH);
	}

	protected void setViewer(StoreListView<T> viewer) {
		this.listView = viewer;
	}
	
	protected void addField(EditorField elt) {
		
		if (elt == null) { 
			return;
		}
		
		elements.put(elt.key, elt);
		formPanel.add(elt);
		formPanel.add(Box.createRigidArea(new Dimension(0, Utils.gapHeight)));
	}
	
	protected void setElementValue(String key, Object value) { 
		
		System.out.println("Setting " + key + " = " + value);
		
		if (key == null) { 
			return;
		}
		
		EditorField field = elements.get(key);
		
		if (field == null)  { 
			return;
		}
		
		field.setValue(value);
	}
	
	protected Object getElementValue(String key) { 
		
		if (key == null) { 
			return null;
		}
		
		EditorField field = elements.get(key);
		
		if (field == null)  { 
			return null;
		}
		
		return field.getValue();
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
			System.err.println("Failed to show! " + ex.getLocalizedMessage());
			ex.printStackTrace(System.err);
		} 
		
		if (elt == null) { 
			return;
		}

		show(elt);
	}
	
	public void setEnabled(boolean value) { 
		
		System.out.println("Viewer setEnables(" + value + ")");
		
		super.setEnabled(value);
		
		for (Component tmp : formPanel.getComponents()) { 
			tmp.setEnabled(value);
		}
	}
	
	protected abstract void show(T elt);
}
