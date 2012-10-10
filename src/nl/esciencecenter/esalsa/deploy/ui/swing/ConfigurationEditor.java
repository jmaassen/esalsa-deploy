package nl.esciencecenter.esalsa.deploy.ui.swing;

import nl.esciencecenter.esalsa.deploy.ConfigurationTemplate;
import nl.esciencecenter.esalsa.deploy.server.SimpleStub;

@SuppressWarnings("serial")
public class ConfigurationEditor extends Editor<ConfigurationTemplate> {

	public ConfigurationEditor(RootPanel parent, SimpleStub stub, RemoteStore<ConfigurationTemplate> store) { //, JobTableModel model) {
		super(parent, stub, store);
		addField(new TextAreaField("Configuration", "Configuration", false, true, -1, 15*Utils.defaultFieldHeight));
	}

	@Override
	public void show(ConfigurationTemplate elt) {
		setElementValue("ID", elt.ID);
		setElementValue("Comment", elt.getComment());
		setElementValue("Configuration", elt.toString());
	}

	@Override
	public void save() {
		
		
		
		
		// TODO Auto-generated method stub
		
	}
}