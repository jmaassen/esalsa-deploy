package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;

@SuppressWarnings("serial")
public class TextAreaField extends BorderedEditorField {

    private final JTextPane textPane;
    
    public TextAreaField(String title, boolean editable) {
        this(title, title, false, editable, -1, 5*Utils.defaultFieldHeight);
    }

    public TextAreaField(String title, String key, boolean mayBeEmpty, boolean editable, int width, int height) {
        
    	super(title, key, mayBeEmpty);
    	
    	textPane = new JTextPane();
    	textPane.setEditable(editable);
    	
        //textPane.addKeyListener(this);
        textPane.addMouseListener(this);
        
        JScrollPane scrollPane = new JScrollPane(textPane);
        
        setPreferredSize(new Dimension(width, height));
        add(scrollPane);
    }

    public Object getValue() {
        return textPane.getText().trim();
    }

    @Override
    public void setValue(Object value) {
    	
    	textPane.setText("");
    	
    	if (value == null) { 
    		return;
    	}
    	
    	if (value instanceof String) { 
    		textPane.setText((String) value);
    	}
    }
    
    @Override
	public boolean isEmpty() {
		String tmp = textPane.getText();
		
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
		textPane.setText("");    	
	}

	@Override
	public boolean checkCorrectness() {
		return true;
	}
}
