package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.net.URI;
import java.util.HashMap;

import javax.swing.JOptionPane;

import nl.esciencecenter.esalsa.deploy.WorkerDescription;
import nl.esciencecenter.esalsa.deploy.server.SimpleStub;

@SuppressWarnings("serial")
public class WorkerEditor extends Editor<WorkerDescription> {

	public WorkerEditor(RootPanel parent, SimpleStub stub, RemoteStore<WorkerDescription> store) { 

		super(parent, stub, store);

		addField(new URIField("URI", true));
		addField(new TextLineField("Template Directory", true));
		addField(new TextLineField("Experiment Directory", true));
		addField(new TextLineField("Input Directory", true));
		addField(new TextLineField("Output Directory", true));
		addField(new PropertyField("Additional Properties", true));		
	}

	@Override
	public void show(WorkerDescription elt) {
		setElementValue("ID", elt.ID);
		setElementValue("Comment", elt.getComment());
		setElementValue("URI", elt.jobServer);
		setElementValue("Template Directory", elt.templateDir);
		setElementValue("Experiment Directory", elt.experimentDir);
		setElementValue("Input Directory", elt.inputDir);
		setElementValue("Output Directory", elt.outputDir);
		setElementValue("Additional Properties", elt.getMapping());
	}

	@SuppressWarnings("unchecked")
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

		URI uri = (URI) getElementValue("URI");
		
		String comment = (String) getElementValue("Comment");
		String templateDir = (String) getElementValue("Template Directory");
		String inputDir = (String) getElementValue("Input Directory");
		String outputDir = (String) getElementValue("Output Directory");
		String experimentDir = (String) getElementValue("Experiment Directory");
		
		HashMap<String, String> values = (HashMap<String, String>) getElementValue("Additional Properties");
		
		WorkerDescription w = new WorkerDescription(ID, uri, uri, inputDir, outputDir, experimentDir, templateDir, "start.sh", "monitor.sh", "stop.sh", comment, values);
	
		try {
			store.add(w);
		} catch (Exception e) {
			showErrorMessage("Failed to store worker!", e);
		}
	}
}