package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public abstract class MyPanel extends JPanel implements ActionListener {

	private static Logger globalLogger = LoggerFactory.getLogger("eSalsa");
	
	private final static int SPACER = 5;
	
	protected final RootPanel parent;
	
	protected final JPanel container;
	private final JPanel buttonPanel;
	
	private final HashMap<String, ButtonHandler> buttonActions = new HashMap<String, ButtonHandler>();

	protected final JPanel formPanel;
	protected LinkedHashMap<String, EditorField> elements = new LinkedHashMap<String, EditorField>();
	
	protected MyPanel(RootPanel parent) { 
		
		super(new BorderLayout(SPACER, SPACER));
		
		this.parent = parent;

		container = new JPanel(new BorderLayout());
		
		JScrollPane scrollPane = new JScrollPane(container,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		add(scrollPane, BorderLayout.CENTER);
		
		formPanel = new JPanel();
		formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.PAGE_AXIS));
		formPanel.setBorder(new EmptyBorder(5, 5, 5, 5));		
		formPanel.add(Box.createRigidArea(new Dimension(0, Utils.gapHeight)));
		
		container.add(formPanel, BorderLayout.NORTH);
		
		buttonPanel = new JPanel();
	    buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	    
		add(buttonPanel, BorderLayout.SOUTH);		
	}
	
	protected void addButton(String command, ButtonHandler handler) {
		
		if (command == null) { 
			throw new IllegalArgumentException("Cannot add a button without a name!");
		}
		
		if (buttonActions.containsKey(command)) { 
			throw new IllegalArgumentException("Panel already contains button with name \"" + command + "\"!");
		}
		
		JButton button = new JButton(command);
		
		button.setActionCommand(command);;
	    button.addActionListener(this);
	    buttonActions.put(command, handler);
		buttonPanel.add(button);
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
		
		globalLogger.debug("Setting " + key + " = " + value);
		
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
		
		boolean result = true;
		
		for (EditorField ef : elements.values()) { 			
			if (!ef.checkCorrectness()) { 
				result = false;
			} 
		}
	
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
	
	
	public void actionPerformed(ActionEvent e) {		
		
		// One of the buttons was clicked!
		String command = e.getActionCommand();
		
		ButtonHandler handler = buttonActions.get(command);
				
		if (handler == null) {
			globalLogger.warn("No handler found for button \"" + command +"\"!");
			return;
		}
		
		handler.clicked();		
	}
	
	protected void showErrorMessage(String message, Exception e) {
		JOptionPane.showMessageDialog(this, message + "\n(" + e.getLocalizedMessage() + ")");
	}
	
	protected void showMessage(String message) { 		
		JOptionPane.showMessageDialog(this, message);
	}
	
	protected boolean askConfirmation(String message) { 		
		
		String [] options = new String [] { "OK", "Cancel" };
		String selected = "Cancel";
		
		int result = JOptionPane.showOptionDialog(this, message, "WARNING", JOptionPane.OK_CANCEL_OPTION, 
				JOptionPane.WARNING_MESSAGE, null, options, selected);

		return (result == 0);
	}

	public void setEnabled(boolean value) { 
		
		//System.out.println("MyPanel setEnables(" + value + ")");
		
		super.setEnabled(value);
		
		for (Component tmp : container.getComponents()) { 
			tmp.setEnabled(value);
		}
		
		for (Component tmp : buttonPanel.getComponents()) { 
			tmp.setEnabled(value);
		}
	}
}
