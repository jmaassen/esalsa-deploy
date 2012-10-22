package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nl.esciencecenter.esalsa.deploy.StoreableObject;
import nl.esciencecenter.esalsa.deploy.server.SimpleStub;

@SuppressWarnings("serial")
public class StoreListView<T extends StoreableObject> extends MyStorePanel<T> implements ListSelectionListener {
	
	protected JList list;
	
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
	
	class StoreListModel extends AbstractListModel implements StoreCallback {

		private ArrayList<String> cache = new ArrayList<String>();
		//private RemoteStore<T> store;
		
		StoreListModel(RemoteStore<T> store) { 
			//this.store = store;
			store.addCallBack(this);
		}
	
		@Override
		public void add(String item) {
			if (item != null && item.length() > 0) {
				cache.add(item);
				Collections.sort(cache);
				fireContentsChanged(this, 0, cache.size());
			} 				
		}

		@Override
		public void remove(String item) {
			if (item != null && item.length() > 0) {
				cache.remove(item);
				Collections.sort(cache);
				fireContentsChanged(this, 0, cache.size());
			} 		
		}

		@Override
		public void clear() {
			cache.clear();
			fireContentsChanged(this, 0, cache.size());
		}
		
		@Override
		public Object getElementAt(int index) {
			
			if (index < 0 || index >= cache.size()) { 
				return null;
			}
			
			return cache.get(index);
		}

		@Override
		public int getSize() {
			return cache.size();
		} 		
	}
	
	public StoreListView(RootPanel parent, SimpleStub stub, RemoteStore<T> store, Viewer<T> viewer, boolean allowDelete) { 

		super(parent, stub, store);

		this.viewer = viewer;
		
		list = new JList(new StoreListModel(store));
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    list.setSelectedIndex(0);
	    list.addListSelectionListener(this);
	    list.setVisibleRowCount(5);
	    list.clearSelection();
	    
	    container.add(list, BorderLayout.NORTH);
		
		addButton("Refresh", new RefreshHandler());
		
		if (allowDelete) { 
			addButton("Delete", new DeleteHandler());
		}
	}

		
	@Override
	public void valueChanged(ListSelectionEvent e) {

	    if (e.getValueIsAdjusting() == false) {
	    	
	    	String value = (String) list.getSelectedValue();
	    	
	    	System.out.println("Got valueChanged " + value);
	    	
	    	if (value != null) { 
	    		viewer.show(value);
	    	}
	    }		
	}

	private void delete() { 

		String id = (String) list.getSelectedValue();

		System.out.println("Got Delete " + id);
		
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
			
			list.clearSelection();			
			viewer.clear();	
		}
	}
	
	private void refresh() {
		
		String value = (String) list.getSelectedValue();
	    
		System.out.println("Got refresh " + value);
		
		parent.refresh("all");
		
		if (value != null) { 
		
			if (store.contains(value)) { 
				list.setSelectedValue(value, true);
				viewer.show(value);
			} else { 
				list.clearSelection();
			}
		}
	}


	public void clear() {
		list.clearSelection();
	}
}
