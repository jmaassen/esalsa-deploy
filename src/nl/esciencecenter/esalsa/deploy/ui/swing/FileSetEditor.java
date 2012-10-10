package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.net.URI;
import java.util.LinkedList;

import nl.esciencecenter.esalsa.deploy.FileSet;
import nl.esciencecenter.esalsa.deploy.server.SimpleStub;

@SuppressWarnings("serial")
public class FileSetEditor extends Editor<FileSet> {

	public FileSetEditor(RootPanel parent, SimpleStub stub, RemoteStore<FileSet> store) { 
		super(parent, stub, store);
		addField(new FileListField("Files"));
	}
	
	@Override
	public void show(FileSet elt) {
		setElementValue("ID", elt.ID);
		setElementValue("Comment", elt.getComment());
		setElementValue("Files", elt.getFiles());
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
		
		String comment = (String) getElementValue("Comment");		
		LinkedList<URI> files = (LinkedList<URI>) getElementValue("Files");
		FileSet f = new FileSet(ID, comment, files);
		
		try {
			store.add(f);
		} catch (Exception e) {
			showErrorMessage("Failed to store file list " + ID + "!", e);
		}		
	}
}