package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.net.URI;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import nl.esciencecenter.esalsa.deploy.FileSet;
import nl.esciencecenter.esalsa.deploy.server.SimpleStub;

@SuppressWarnings("serial")
public class FileSetEditor extends Editor<FileSet> {

	public FileSetEditor(RootPanel parent, SimpleStub stub, RemoteStore<FileSet> store) { //, JobTableModel model) {
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
			System.err.println("Failed to store file list! (" + e.getLocalizedMessage() + ")");
			e.printStackTrace(System.err);
			JOptionPane.showMessageDialog(this, "Failed to store file list! (" + e.getLocalizedMessage() + ")");			
		}		
	}
}