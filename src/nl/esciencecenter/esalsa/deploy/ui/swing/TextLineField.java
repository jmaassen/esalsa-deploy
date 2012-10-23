package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JTextField;

@SuppressWarnings("serial")
public class TextLineField extends BorderedEditorField {
	
    protected final JTextField textField;

    public TextLineField(String title, boolean editable) {
    	this(title, title, false, editable);
    }
    
    public TextLineField(String title, String key, boolean mayBeEmpty, boolean editable) {
    	super(title, key, mayBeEmpty);
    	textField = new JTextField();
    	textField.setEditable(editable);
    	textField.setBackground(Color.WHITE);    	
    	add(textField, BorderLayout.CENTER);
    	
    	textField.addMouseListener(this);
    }
    
    public void setBackGround(Color color) { 
    	textField.setBackground(color);
    }

	@Override
    public Object getValue() {
        return textField.getText().trim();
    }

	@Override
    public void setValue(Object value) {
		
		textField.setText("");
		
		if (value == null) { 
			return;
		}
		
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

	@Override
	public void clear() {
		textField.setText("");
	}

	@Override
	public boolean checkCorrectness() {
		return true;
	}	

	public void setEnabled(boolean value) { 
		
		//System.out.println("TextLineField setEnables(" + value + ")");
		
		super.setEnabled(value);
		textField.setEnabled(value);
	}
}
