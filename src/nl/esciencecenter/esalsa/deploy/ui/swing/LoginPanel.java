package nl.esciencecenter.esalsa.deploy.ui.swing;

@SuppressWarnings("serial")
public class LoginPanel extends MyPanel {
	
	class OK implements ButtonHandler {
		@Override
		public void clicked() {
			login();
			
		} 		
	}

	class Cancel implements ButtonHandler {
		@Override
		public void clicked() {
			cancel();
		} 		
	}
	
	protected LoginPanel(RootPanel parent, String server, int port) {
		super(parent);
		
		addField(new TextLineField("Server address", "Server address", false, true));
		addField(new TextLineField("Server port", "Server port", true, true));
		addField(new TextLineField("Password", "Password", false, true));
		
		if (server != null  && server.length() > 0) { 
			setElementValue("Server address", server);
		} 
		
		setElementValue("Server port", "" + port);
		
		addButton("OK", new OK());
		addButton("Cancel", new Cancel());
	}
	
	private void login() { 

		if (checkForEmptyFields()) { 
			return;
		}
		
		String server = (String) getElementValue("Server address");
		String portTmp = (String) getElementValue("Server port");
		
		int port = GUI.DEFAULT_PORT; 
		
		if (portTmp != null && portTmp.trim().length() >= 0) {
			
			try { 
				port = Integer.parseInt(portTmp);
			} catch (Exception e) {
				showError("Server port", "Invalid port value!");
				return;
			}
			
			if (port <= 0 || port > 65535) { 
				showError("Server port", "Invalid port value!");
				return;
			}			
		}
		
		String password = (String) getElementValue("Password");
		
		parent.login(server, port, password);		
	}
	
	private void cancel() { 
		parent.cancelLogin();
	}
}
