package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.BorderLayout;

import javax.swing.JTextField;

public class TextLineField extends EditorField {

    private static final long serialVersionUID = -3105825793218772947L;
	
    private final JTextField textField;

    public TextLineField(String title, boolean editable) {
    	this(title, title, false, editable);
    }
    
    public TextLineField(String title, String key, boolean mayBeEmpty, boolean editable) {
    	super(title, key, mayBeEmpty);
    	textField = new JTextField();
    	textField.setEditable(editable);
    	add(textField, BorderLayout.CENTER);
    }

	@Override
    public Object getValue() {
        return textField.getText().trim();
    }

	@Override
    public void setValue(Object value) {    	
    	if (value instanceof String) { 
    		textField.setText((String) value);
    	} 
    }

	@Override
	public boolean isEmpty() {
		String tmp = textField.getText();
		
		if (tmp == null) { 
			return true;
		}
		
		tmp = tmp.trim();
		
		if (tmp.length() == 0) { 
			return true;
		}
		
		return false;
	}
}
