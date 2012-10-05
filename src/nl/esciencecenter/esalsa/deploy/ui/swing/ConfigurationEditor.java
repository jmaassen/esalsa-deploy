package nl.esciencecenter.esalsa.deploy.ui.swing;

import nl.esciencecenter.esalsa.deploy.ConfigurationTemplate;

public class ConfigurationEditor extends Editor<ConfigurationTemplate> {

	private static final long serialVersionUID = -8580838957929000835L;

	public ConfigurationEditor(RemoteStore<ConfigurationTemplate> store) { //, JobTableModel model) {

		super(store);
		
		addElement(new TextAreaField("Configuration", "Configuration", false, true, -1, 15*Utils.defaultFieldHeight));
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