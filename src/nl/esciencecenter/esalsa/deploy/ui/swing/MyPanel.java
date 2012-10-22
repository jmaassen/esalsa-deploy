package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public abstract class MyPanel extends JPanel implements ActionListener {

	private final static int SPACER = 5;
	
	protected final RootPanel parent;
	
	protected final JPanel container;
	private final JPanel buttonPanel;
	
	private final HashMap<String, ButtonHandler> buttonActions = new HashMap<String, ButtonHandler>();
	
	protected MyPanel(RootPanel parent) { 
		
		super(new BorderLayout(SPACER, SPACER));
		
		this.parent = parent;

		container = new JPanel(new BorderLayout());
		
		JScrollPane scrollPane = new JScrollPane(container,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		add(scrollPane, BorderLayout.CENTER);
		
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
	
	public void actionPerformed(ActionEvent e) {		
		
		// One of the buttons was clicked!
		String command = e.getActionCommand();
		
		ButtonHandler handler = buttonActions.get(command);
				
		if (handler == null) { 
			System.err.println("No handler found for button \"" + command +"\"!");
			return;
		}
		
		handler.clicked();		
	}
	
	protected void showErrorMessage(String message, Exception e) { 		
		System.err.println(message);
		System.err.println(e.getLocalizedMessage());		
		e.printStackTrace(System.err);
		
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
		
		System.out.println("MyPanel setEnables(" + value + ")");
		
		super.setEnabled(value);
		
		for (Component tmp : container.getComponents()) { 
			tmp.setEnabled(value);
		}
		
		for (Component tmp : buttonPanel.getComponents()) { 
			tmp.setEnabled(value);
		}
	}
	
	/*
	protected void disableMe() { 
		
		setEnabled(false);
	
		for (Component tmp : container.getComponents()) { 
			tmp.setEnabled(false);
		}
		
		for (Component tmp : buttonPanel.getComponents()) { 
			tmp.setEnabled(false);
		}
		
	}
	
	protected void enableMe() {
		
		setEnabled(true);
		
		for (Component tmp : container.getComponents()) { 
			tmp.setEnabled(true);
		}
		
		for (Component tmp : buttonPanel.getComponents()) { 
			tmp.setEnabled(true);
		}
	}*/
		
}
