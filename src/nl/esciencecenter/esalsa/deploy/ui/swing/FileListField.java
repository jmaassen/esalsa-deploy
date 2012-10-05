package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class FileListField extends EditorField {

	private static final long serialVersionUID = -8580838957929000835L;

	private class FileField { 
		
		final JTextField fileField = new JTextField();
    
		private String cleanup(String s) { 
			
			if (s == null) { 
				return null;
			}
			
			s = s.trim();
			
			if (s.length() == 0) { 
				return null;
			}
			
			return s;
		} 
		
		String getFile() { 
			return cleanup(fileField.getText()); 
		}
		
		FileField(String file) { 
			fileField.setText(file);
		} 
	} 

	public class Handler implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			addFile();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		} 
		
	}
	
	private LinkedList<FileField> list = new LinkedList<FileField>();
	
	private JPanel filesPanel; 
	
	public FileListField(String title) {
		this(title, title, true);
	}

	public FileListField(String title, String key, boolean mayBeEmpty) {
		
		super(title, key, mayBeEmpty);
		
		filesPanel = new JPanel();		
		filesPanel.setLayout(new BoxLayout(filesPanel, BoxLayout.Y_AXIS));		
		filesPanel.setBorder(BorderFactory.createEmptyBorder());
		
		add(filesPanel, BorderLayout.CENTER);
		
		JButton button = new JButton("+"); 
		button.addMouseListener(new Handler());

		JPanel container = new JPanel();
		container.setLayout(new FlowLayout(FlowLayout.RIGHT));
		container.add(button);

		add(container, BorderLayout.SOUTH);
	        
		addFile();		
	}
	
	private void addFile() {
		addFile(null);
	}
	
	private void addFile(String name) { 

		System.out.println("Adding file field! (" + name + ")");
		
		FileField field = new FileField(name);
		list.add(field);
		
		filesPanel.add(field.fileField);
		updateUI();
	}

	@Override
	public Object getValue() {
		
		LinkedList<URI> tmp = new LinkedList<URI>();
		
		for (FileField f : list) { 
			
			String value = f.getFile();
			
			try { 
				tmp.add(new URI(value));
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
		
		if (tmp.size() != 0) { 
			return tmp;
		}
		
		return null;
	}

	@Override
	public void setValue(Object value) {
		
		System.out.println("FileListField.setValue " + value.getClass() + " " + value);
		
		filesPanel.removeAll();
		list.clear();
		
		if (value instanceof LinkedList) { 
			
			System.out.println("Is linkedList!");
				
			// EEP
			LinkedList<URI> tmp = (LinkedList<URI>) value;
			
			for (URI uri : tmp) { 
				addFile(uri.toString());
			}
		}

		if (list.size() == 0) { 
			addFile();
		}
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}
}