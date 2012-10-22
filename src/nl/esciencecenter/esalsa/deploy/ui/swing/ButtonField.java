package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ButtonField extends BorderedEditorField implements ActionListener {

	private JPanel buttonPanel;
	
	private final HashMap<String, ButtonHandler> buttonActions = new HashMap<String, ButtonHandler>();
	
	class Handler implements ButtonHandler{

		@Override
		public void clicked() {
			System.out.println("CLICK");
		} 
	}
	
	
	public ButtonField(String title, String key, boolean mayBeEmpty) { 
		super(title, key, mayBeEmpty);
		
		buttonPanel = new JPanel();
	    buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	    
		add(buttonPanel, BorderLayout.SOUTH);		
		
		addButton("TEST", new Handler());
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
	
	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setValue(Object setValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkCorrectness() {
		return true;
	}

	@Override
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
}
