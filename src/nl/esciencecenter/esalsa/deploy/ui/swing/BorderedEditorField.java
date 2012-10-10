package nl.esciencecenter.esalsa.deploy.ui.swing;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

@SuppressWarnings("serial")
public abstract class BorderedEditorField extends EditorField {

	private final Border normalBorder;
	public final String title;
	
	BorderedEditorField(String title, String key, boolean mayBeEmpty) { 
		
		super(key, mayBeEmpty);
		
		this.title = title;
		
		normalBorder = BorderFactory.createTitledBorder(title);
		
		setBorder(normalBorder);
	}
    
    public void setError(String text) { 
    	
    	error = true;
    	
    	String tmp = title;
    	
    	if (text != null && text.length() > 0) { 
    		tmp = tmp + " (" + text + ")";
    	}
    	
    	setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(ERROR_COLOR), tmp));
    }

    public void resetError() { 
        
    	System.out.println("Reset error AAP!");
    	
    	error = false;
    	setBorder(normalBorder);
    	
    	System.out.println("Reset error!");
    	
    }

	
	
}
