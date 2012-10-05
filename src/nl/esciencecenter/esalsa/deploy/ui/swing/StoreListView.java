package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nl.esciencecenter.esalsa.deploy.StoreableObject;

public class StoreListView<T extends StoreableObject>  extends JPanel implements StoreCallback, ListSelectionListener {
	
	private static final long serialVersionUID = 7637905085627717438L;

	private final static int SPACER = 5;
	
	private JList list;
	
	private DefaultListModel listModel;
	
	private RemoteStore<T> store;	
	private Viewer viewer;
	
	public StoreListView(RemoteStore<T> store, Viewer<T> viewer) { 

		this.store = store;
		this.viewer = viewer;
		
		
		//setBorder(BorderFactory.createTitledBorder("Map"));
		setLayout(new BorderLayout(SPACER, SPACER));
		
		listModel = new DefaultListModel();
		list = new JList(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    list.setSelectedIndex(0);
	    list.addListSelectionListener(this);
	    list.setVisibleRowCount(5);
		
		JPanel container = new JPanel(new BorderLayout());
		container.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		JScrollPane listScrollPane = new JScrollPane(list);	
	    container.add(listScrollPane, BorderLayout.CENTER);
		add(listScrollPane, BorderLayout.CENTER);
		
		store.addCallBack(this);
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
	public void valueChanged(ListSelectionEvent e) {

	    if (e.getValueIsAdjusting() == false) {

	    	String value = (String) list.getSelectedValue();
	    	
	    	if (value != null) { 
	    		viewer.show(value);
	    	}
	    }		
	} 
}
