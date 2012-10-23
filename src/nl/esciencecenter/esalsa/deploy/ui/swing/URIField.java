package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.net.URI;
import java.net.URISyntaxException;

@SuppressWarnings("serial")
public class URIField extends TextLineField {

    public URIField(String title, boolean editable) {
    	this(title, title, false, editable);
    }
    
    public URIField(String title, String key, boolean mayBeEmpty, boolean editable) {
    	super(title, key, mayBeEmpty, editable);
    }

	@Override
    public Object getValue() {
		
		String tmp = textField.getText().trim();
		
		URI uri = null;
		
		try {
			uri = new URI(tmp);
		} catch (URISyntaxException e) {
			GUI.globalLogger.error("URIField.getValue: Field contains invalid URI: " + tmp);
		}
		
        return uri;
    }

	@Override
    public void setValue(Object value) {
		
		textField.setText("");
		
		if (value == null) { 
			return;
		}

		//System.out.println("Setting URI field " + value);
		
    	if (value instanceof URI) { 
    		textField.setText(((URI) value).toString());
    	} else { 
    		GUI.globalLogger.error("URIField.setValue cannot handle paramater of type " + value.getClass());
    	}
    }
	
	@Override
	public boolean checkCorrectness() {

		String tmp = textField.getText();
		
		if (tmp == null) { 

			if (mayBeEmpty) { 
				return true;
			}
			
			setError("Invalid URI");
			return false;
		}

		tmp = tmp.trim();
		
		try {
			new URI(tmp);
			return true;
		} catch (URISyntaxException e) {
			setError("Invalid URI");
			return false;
		}
	}
}
