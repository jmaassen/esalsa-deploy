package nl.esciencecenter.esalsa.deploy.ui.swing;

import nl.esciencecenter.esalsa.deploy.FileSet;

public class FileSetEditor extends Editor<FileSet> {

	private static final long serialVersionUID = -8580838957929000835L;
	
	public FileSetEditor(RemoteStore<FileSet> store) { //, JobTableModel model) {
		super(store);
		addElement(new FileListField("Files"));
	}
	
	@Override
	public void show(FileSet elt) {
		setElementValue("ID", elt.ID);
		setElementValue("Comment", elt.getComment());
		setElementValue("Files", elt.getFiles());
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub
		
	}
}