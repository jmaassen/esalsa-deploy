package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import nl.esciencecenter.esalsa.deploy.StoreableObject;

public abstract class Editor<T extends StoreableObject> extends Viewer<T> implements ActionListener {

	private static final long serialVersionUID = -3880020751783711967L;

	protected Editor(RemoteStore<T> store) { 
		
		super(store, true);
		
		JPanel buttonPanel = new JPanel();
	    buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	    add(buttonPanel, BorderLayout.SOUTH);
		
	    JButton clear = new JButton("Clear");
	    clear.setActionCommand("Clear");;
	    clear.addActionListener(this);
	    
	    JButton save = new JButton("Save");
	    save.setActionCommand("Save");;
	    save.addActionListener(this);
	    
	    JButton delete = new JButton("Delete");
	    delete.setActionCommand("Delete");;
	    delete.addActionListener(this);
	    
		buttonPanel.add(clear);
		buttonPanel.add(save);
		buttonPanel.add(delete);
	}
	
	public Object getElementValue(String key) { 
		
		if (key == null) { 
			return null;
		}
		
		EditorField field = elements.get(key);
		
		if (field == null)  { 
			return null;
		}
		
		return field.getValue();
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
	public abstract void save() throws Exception;
		
	public void actionPerformed(ActionEvent e) {
		
		String command = e.getActionCommand();
		
		if (command.equals("Clear")) { 
			for (EditorField ef : elements.values()) { 
				ef.setValue(null);
			}
		} else if (command.equals("Save")) { 
			
			try { 
				save();
			} catch (Exception ex) {
				System.err.println("Failed to save! " + ex.getLocalizedMessage());
				ex.printStackTrace(System.err);
			}
			
		} else if (command.equals("Delete")) { 
			
		}
	} 
}
