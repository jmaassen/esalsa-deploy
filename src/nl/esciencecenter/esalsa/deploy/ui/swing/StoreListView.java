package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nl.esciencecenter.esalsa.deploy.StoreableObject;

@SuppressWarnings("serial")
public class StoreListView<T extends StoreableObject>  extends JPanel implements StoreCallback, ListSelectionListener, ActionListener {
	
	private final static int SPACER = 5;
	
	private JList list;
	
	private JPanel buttonPanel;
	
	private DefaultListModel listModel;
	
	private RemoteStore<T> store;	
	private Viewer<T> viewer;
	
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
		container.add(list, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane(container,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		add(scrollPane, BorderLayout.CENTER);
	    
/*		
		JPanel container = new JPanel(new BorderLayout());
		container.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		JScrollPane listScrollPane = new JScrollPane(list);	
	    container.add(listScrollPane, BorderLayout.CENTER);
		add(listScrollPane, BorderLayout.CENTER);
*/
		
		store.addCallBack(this);
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	    
	    add(buttonPanel, BorderLayout.SOUTH);
	    addButton(new JButton("Delete"));
	}
	
	protected void addButton(JButton button) { 		
		String command = button.getText();
		button.setActionCommand(command);;
	    button.addActionListener(this);
		buttonPanel.add(button);		
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

	@Override
	public void actionPerformed(ActionEvent e) {
		
		String command = e.getActionCommand();
		
		if (command.equals("Delete")) { 
			String id = (String) list.getSelectedValue();
			
			if (id == null) { 
				return;
			}

			String [] options = new String [] { "OK", "Cancel" };
			String selected = "Cancel";
			
			int result = JOptionPane.showOptionDialog(this, "Are you sure you want to delete " + id + "?", "WARNING", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, selected);
			
			if (result >= 0 && result < options.length) { 
				selected = options[result];
			}
			
			if (selected.equals("OK")) { 
				System.out.println("Deleting " + id + " " + selected);
				
				try {
					store.delete(id);
				} catch (Exception e1) {
					System.err.println("Failed to delete entry " + id + "! " + e1.getLocalizedMessage());
					e1.printStackTrace();
					
					JOptionPane.showMessageDialog(this, "Failed to delete entry " + id + "! " + e1.getLocalizedMessage());					
				}
				
				viewer.clear();
			}
			
		} else { 
			System.out.println("Unknown command: " + command);
		}
	} 
}
