package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class FileListField extends BorderedEditorField {
	
	private static Logger globalLogger = LoggerFactory.getLogger("eSalsa");
	
	private class FileField implements MouseListener {

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

		void resetNormalBackGround() {
			fileField.setBackground(NORMAL_COLOR);
		}
		
		void setErrorBackGround() {
			fileField.setBackground(ERROR_COLOR);
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			resetNormalBackGround();
			
			String tmp = fileField.getText();
			int pos = fileField.getCaretPosition();
			
			fileField.setText("");
			
			if (checkCorrectness()) { 
				resetParentError();
			}
			
			fileField.setText(tmp);
			fileField.setCaretPosition(pos);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}
	}
	
	
	public class Handler implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			addFile();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
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

	private void addFile(String text) {

		//System.out.println("Adding file field! (" + text + ")");

		FileField field = new FileField(text);
		list.add(field);
		
		filesPanel.add(field.fileField);		
		field.fileField.addMouseListener(field);
		
		updateUI();
	}

	@Override
	public Object getValue() {

		LinkedList<URI> tmp = new LinkedList<URI>();

		for (FileField f : list) {
			String value = f.getFile();
			
			URI uri = null;
			
			try {
				uri = new URI(value);
			} catch (URISyntaxException e) {
				f.setErrorBackGround();
				globalLogger.debug("Invalid URI!", e);
				return null;
			}
			
			tmp.add(uri);
		}

		if (tmp.size() != 0) {
			return tmp;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setValue(Object value) {

		// System.out.println("FileListField.setValue " + value);

		filesPanel.removeAll();
		list.clear();

		if (value == null) {
			return;
		}

		if (value instanceof LinkedList) {

			//System.out.println("Is linkedList!");

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
		return (list.size() == 0);
	}

	@Override
	public void clear() {
		filesPanel.removeAll();
		list.clear();
		addFile();
	}

	@Override
	public boolean checkCorrectness() {

		//System.out.println("In Files.iscorrect!");
		
		boolean correct = true;
		
		for (FileField f : list) {
			
			String tmp = f.getFile();
			
			if (tmp != null && tmp.trim().length() > 0) { 
				try {
					new URI(f.getFile());
				} catch (URISyntaxException e) {
					f.setErrorBackGround();
					correct = false;
				}
			}
		}
		
		if (!correct) { 
			setError("Invalid URIs found!");
		}
		
		//System.out.println("In Files.iscorrect result = " + correct);
		return correct;
	}

	protected void resetParentError() { 
		super.resetError();		
	}
	
	@Override
	public void resetError() {
		
		resetParentError();
	
		for (FileField f : list) {
			f.resetNormalBackGround();
		}
	}
}