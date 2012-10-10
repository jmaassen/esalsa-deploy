package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nl.esciencecenter.esalsa.deploy.StoreableObject;
import nl.esciencecenter.esalsa.deploy.server.SimpleStub;

@SuppressWarnings("serial")
public class StoreListView<T extends StoreableObject>  extends MyPanel<T> implements StoreCallback, ListSelectionListener {
	
	protected JList list;
	private DefaultListModel listModel;
	
	private Viewer<T> viewer;
	
	class DeleteHandler implements ButtonHandler {
		@Override
		public void clicked() {
			delete();
		}		
	}
	
	class RefreshHandler implements ButtonHandler {
		@Override
		public void clicked() {
			refresh();
		}		
	}
	
	public StoreListView(RootPanel parent, SimpleStub stub, RemoteStore<T> store, Viewer<T> viewer, boolean allowDelete) { 

		super(parent, stub, store);

		this.viewer = viewer;
		
		listModel = new DefaultListModel();
		list = new JList(listModel);
		
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    list.setSelectedIndex(0);
	    list.addListSelectionListener(this);
	    list.setVisibleRowCount(5);
		
	    container.add(list, BorderLayout.NORTH);
		
		store.addCallBack(this);
		
		addButton("Refresh", new RefreshHandler());
		
		if (allowDelete) { 
			addButton("Delete", new DeleteHandler());
		}
	}

	@Override
	public void add(String item) {
		if (item != null && item.length() > 0) { 
			listModel.addElement(item);
		} 
	}

	@Override
	public void remove(String item) {
		if (item != null && item.length() > 0) { 
			listModel.removeElement(item);
		} 		
	}

	@Override
	public void clear() {
		listModel.removeAllElements();
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {

	    if (e.getValueIsAdjusting() == false) {

	    	String value = (String) list.getSelectedValue();
	    	
	    	if (value != null) { 
	    		viewer.show(value);
	    	}
	    }		
	}

	private void delete() { 
		
		System.out.println("Got Delete");

		String id = (String) list.getSelectedValue();
		
		if (id == null) { 
			return;
		}
		
		boolean confirm = askConfirmation("Are you sure you want to delete " + id + "?");
		
		if (confirm) { 
	
			System.out.println("Deleting " + id);
			
			try {
				store.delete(id);
			} catch (Exception e) {
				showErrorMessage("Failed to delete entry " + id + "!", e);
				return;
			}
			
			viewer.clear();	
		}
	}
	
	private void refresh() { 
		System.out.println("Got refresh");
		parent.refresh("all");
	}
}
