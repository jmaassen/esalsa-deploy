package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import nl.esciencecenter.esalsa.deploy.StoreableObject;
import nl.esciencecenter.esalsa.deploy.server.SimpleStub;

@SuppressWarnings("serial")
public abstract class MyPanel<T extends StoreableObject> extends JPanel implements ActionListener {

	private final static int SPACER = 5;
	
	protected RemoteStore<T> store;
	protected final SimpleStub stub;
	protected final RootPanel parent;
	
	private final JPanel container;
	private final JPanel buttonPanel;
	
	private final HashMap<String, ButtonHandler> buttonActions = new HashMap<String, ButtonHandler>();
	
	protected MyPanel(RootPanel parent, SimpleStub stub, RemoteStore<T> store, boolean editable) { 
		
		super(new BorderLayout(SPACER, SPACER));
		
		this.store = store;
		this.parent = parent;
		this.stub = stub;

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
	
	protected void showError(String message, Exception e) { 		
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
}
