package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class PropertyField extends BorderedEditorField {

	private class Property { 
	
		final JTextField keyField = new JTextField();
		final JTextField valueField = new JTextField();
    
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
		
		String getKey() { 
			return cleanup(keyField.getText()); 
		}
		
		String getValue() { 
			return cleanup(valueField.getText()); 
		}		
		
		Property(String key, String value) { 
			keyField.setText(key);
			valueField.setText(value);
		} 
		
		void setBackground(Color key, Color value) {
			keyField.setBackground(key);
			valueField.setBackground(value);
		}
		
		void resetNormalBackGround() {
			setBackground(NORMAL_COLOR, NORMAL_COLOR);
		}
		
		void setErrorBackGround() {
			setBackground(ERROR_COLOR, ERROR_COLOR);
		}
	} 
	
	public class Handler implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			addProperty();
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
    
	private LinkedList<Property> properties = new LinkedList<Property>();
	
	private JPanel keys; 
	private JPanel values; 
	
	public PropertyField(String title, boolean editable) {
		this(title, title, true, editable);
	}

	public PropertyField(String title, String key, boolean mayBeEmpty, boolean editable) {
		super(title, key, mayBeEmpty);
		
		keys = new JPanel();		
		keys.setLayout(new BoxLayout(keys, BoxLayout.Y_AXIS));
		keys.setBorder(BorderFactory.createEmptyBorder());
		
		values = new JPanel();		
		values.setLayout(new BoxLayout(values, BoxLayout.Y_AXIS));		
		values.setBorder(BorderFactory.createEmptyBorder());
			
		JSplitPane horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, keys, values);
        //horizontalSplitPane.setOneTouchExpandable(false);
        //horizontalSplitPane.setDividerLocation((int) (GUI.DEFAULT_SCREEN_WIDTH * 0.35));
        horizontalSplitPane.setResizeWeight(0.5);		
        //horizontalSplitPane.setPreferredSize(new Dimension(-1, 5*Utils.defaultFieldHeight));
        horizontalSplitPane.setBorder(BorderFactory.createEmptyBorder());		
        
        add(horizontalSplitPane, BorderLayout.CENTER);
        
        JButton button = new JButton("+"); 
        button.addMouseListener(new Handler());

        JPanel container = new JPanel();
        container.setLayout(new FlowLayout(FlowLayout.RIGHT));
	    container.add(button);

	    add(container, BorderLayout.SOUTH);
        
        addProperty();
	}

	private void addProperty() {
		addProperty(null, null);
	}
	
	private void addProperty(String key, String value) { 

		System.out.println("Adding property field! (" + key + " = " + value + ")");
		
		Property field = new Property(key, value);
		properties.add(field);
		
		keys.add(field.keyField);
		keys.add(Box.createRigidArea(new Dimension(0, Utils.gapHeight)));
		
		values.add(field.valueField);
		values.add(Box.createRigidArea(new Dimension(0, Utils.gapHeight)));
		
		updateUI();
	}
	
	@Override
	public Object getValue() {
		
		HashMap<String, String> result = new HashMap<String, String>();
		
		for (Property f : properties) { 
			
			String key = f.getKey();
			String value = f.getValue();
			
			if (key != null && value != null) {
				result.put(key, value);
			}
		}

		if (result.size() > 0) { 
			return result;
		}

		return null;
	}

	@Override
	public void setValue(Object value) {

		keys.removeAll();
		values.removeAll();
		
		properties.clear();

		if (value == null) { 
			return;
		}
				
		if (value instanceof HashMap) { 

			// EEP!
			HashMap<String, String> tmp = (HashMap<String, String>) value;
			
			for (Entry<String, String> e : tmp.entrySet()) { 
				addProperty(e.getKey(), e.getValue());
			}
		}
		
		if (properties.size() == 0) { 
			addProperty();
		}
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public void clear() {
		keys.removeAll();
		values.removeAll();
		properties.clear();
		addProperty();
	}

	private boolean isEmpty(String s) { 
		
		if (s == null) { 
			return true;
		}
		
		if (s.trim().length() == 0) { 
			return true;
		}
		
		return false;		
	}
	
	@Override
	public boolean checkCorrectness() {

		resetError();
		
		if (properties.size() == 0) { 
			return true;
		}
		
		boolean correct = true;
		HashSet<String> keys = new HashSet<String>();
		
		for (Property f : properties) { 
			
			String key = f.getKey();
			String value = f.getValue();

			if (isEmpty(key) && !isEmpty(value)) { 
				correct = false;
				f.setBackground(ERROR_COLOR, NORMAL_COLOR);
			}
			
			if (!isEmpty(key) && isEmpty(value)) { 
				correct = false;
				f.setBackground(NORMAL_COLOR, ERROR_COLOR);
			}
		
			if (keys.contains(key)) { 
				correct = false;
				f.setBackground(ERROR_COLOR, NORMAL_COLOR);
			}			
		} 
		
		return correct;
	}
	
	@Override
	public void resetError() {
		
		super.resetError();
		
		for (Property f : properties) { 
			f.setBackground(NORMAL_COLOR, NORMAL_COLOR);
		} 
	}	
}
