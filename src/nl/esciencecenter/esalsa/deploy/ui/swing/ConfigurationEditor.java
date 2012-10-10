package nl.esciencecenter.esalsa.deploy.ui.swing;

import nl.esciencecenter.esalsa.deploy.ConfigurationTemplate;
import nl.esciencecenter.esalsa.deploy.parser.ParseException;
import nl.esciencecenter.esalsa.deploy.parser.TemplateParser;
import nl.esciencecenter.esalsa.deploy.server.SimpleStub;

@SuppressWarnings("serial")
public class ConfigurationEditor extends Editor<ConfigurationTemplate> {
	
	private TextAreaField configuration;
	
	public ConfigurationEditor(RootPanel parent, SimpleStub stub, RemoteStore<ConfigurationTemplate> store) { //, JobTableModel model) {
		super(parent, stub, store);
		configuration = new TextAreaField("Configuration", "Configuration", false, true,  true, -1, 15*Utils.defaultFieldHeight); 
		addField(configuration);
	}

	@Override
	public void show(ConfigurationTemplate elt) {
		setElementValue("ID", elt.ID);
		setElementValue("Comment", elt.getComment());
		setElementValue("Configuration", elt.toString());
	}

	private void showParseError(int line, String message) { 
		
		System.out.println("Got parse error on line " + line + " : " + message);
		
		showError("Configuration", "Error on line " + line + ": " + message);
		
		configuration.setErrorLine(line);
		
		
	}
	
	@Override
	public void save() {
		
		if (checkForEmptyFields()) { 
			return;
		}
		
		if (!checkForCorrectness()) { 
			return;
		}
		
		String ID = (String) getElementValue("ID");
		
		if (store.contains(ID)) { 
			showError("ID", "Field must be unique!");
			return;
		}

		String comment = (String) getElementValue("Comment");
		String config = (String) getElementValue("Configuration");
		
		ConfigurationTemplate ct = null; 
		
		try { 
			ct = new TemplateParser(new ConfigurationTemplate(ID, comment), config).parse();		
		} catch (ParseException pe) {
			showParseError(pe.getLine(), pe.getMessage());
			return;			
		} catch (Exception e) { 
			showErrorMessage("Failed to parse configuration!", e);
			return;
		}

		if (ct != null) { 
			try {
				store.add(ct);
			} catch (Exception e) {
				showErrorMessage("Failed to store configuration!", e);
			}
		}
	}
}