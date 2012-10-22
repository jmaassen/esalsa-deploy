package nl.esciencecenter.esalsa.deploy.ui.swing;

import nl.esciencecenter.esalsa.deploy.ConfigurationTemplate;
import nl.esciencecenter.esalsa.deploy.parser.ParseException;
import nl.esciencecenter.esalsa.deploy.parser.TemplateParser;
import nl.esciencecenter.esalsa.deploy.server.SimpleStub;

@SuppressWarnings("serial")
public class ConfigurationEditor extends Editor<ConfigurationTemplate> {
	
	private TextLineFieldWithButton configuration;
	
	class Parser implements EditorListener {

		@Override
		public void parse(String text) throws ParseException {
			
			try { 
				new TemplateParser(new ConfigurationTemplate("dummy", "dummy"), text).parse();
			} catch (ParseException e1) {
				throw e1;
			} catch (Exception e2) { 
				throw new ParseException("(unknown)", 1, "Failed to parse configuration!");
			}
		}

		@Override
		public void startedEditing() {
			disableParent();
		}

		@Override
		public void stoppedEditing() {
			enableParent();
		} 
	}
	
	public ConfigurationEditor(RootPanel parent, SimpleStub stub, RemoteStore<ConfigurationTemplate> store) { 
		super(parent, stub, store);

		configuration = new TextLineFieldWithButton("Configuration", "pop_in_template", "edit", false, true, new Parser());
		addField(configuration);
	}
	
	private void disableParent() { 
		parent.disableMe();
	}
	
	private void enableParent() { 
		parent.enableMe();
	}

	@Override
	public void show(ConfigurationTemplate elt) {
		setElementValue("ID", elt.ID);
		setElementValue("Comment", elt.getComment());
		setElementValue("Configuration", elt.toString());
		configuration.setLine("pop_in_template");
	}

	private void showParseError(int line, String message) { 
		showError("Configuration", "Error on line " + line + ": " + message);
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