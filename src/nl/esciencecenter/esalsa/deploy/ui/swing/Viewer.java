package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.LinkedHashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import nl.esciencecenter.esalsa.deploy.StoreableObject;

public abstract class Viewer<T extends StoreableObject> extends JPanel {

	private static final long serialVersionUID = -3880020751783711967L;

	private final static int SPACER = 5;
	
	protected RemoteStore<T> store;
	
	protected LinkedHashMap<String, EditorField> elements = new LinkedHashMap<String, EditorField>();
	
	protected JPanel formPanel;
	
	protected Viewer(RemoteStore<T> store, boolean editable) { 
		
		super(new BorderLayout(SPACER, SPACER));
		
		this.store = store;

		formPanel = new JPanel();
		formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.PAGE_AXIS));
		formPanel.setBorder(new EmptyBorder(5, 5, 5, 5));		
		formPanel.add(Box.createRigidArea(new Dimension(0, Utils.gapHeight)));
	
		addElement(new TextLineField("ID", editable));
		addElement(new TextAreaField("Comment", "Comment", true, editable, -1, 5*Utils.defaultFieldHeight));
		
		JPanel container = new JPanel(new BorderLayout());
		container.add(formPanel, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane(container,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		add(scrollPane, BorderLayout.CENTER);
	}

	protected void addElement(EditorField elt) {
		
		if (elt == null) { 
			return;
		}
		
		elements.put(elt.key, elt);
		formPanel.add(elt);
		formPanel.add(Box.createRigidArea(new Dimension(0, Utils.gapHeight)));
	}
	
	protected void setElementValue(String key, Object value) { 
		
		if (key == null) { 
			return;
		}
		
		EditorField field = elements.get(key);
		
		if (field == null)  { 
			return;
		}
		
		field.setValue(value);
	}
	
	protected abstract void show(T elt);	
	
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
}
