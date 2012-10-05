package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class TextAreaField extends EditorField implements KeyListener {

    private static final long serialVersionUID = 9123048071906291528L;
	
    private final JTextPane textPane;
    
    public TextAreaField(String title, boolean editable) {
        this(title, title, false, editable, -1, 5*Utils.defaultFieldHeight);
    }

    public TextAreaField(String title, String key, boolean mayBeEmpty, boolean editable, int width, int height) {
        
    	super(title, key, mayBeEmpty);
    	
    	textPane = new JTextPane();
    	textPane.setEditable(editable);
    	
        textPane.addKeyListener(this);
        
        JScrollPane scrollPane = new JScrollPane(textPane);
        
        setPreferredSize(new Dimension(width, height));
        add(scrollPane);
    }

    public Object getValue() {
        return textPane.getText().trim();
    }

    @Override
    public void setValue(Object value) {
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
    public void setError(String message) {
        // textPane.setText(text);
    }

    @Override
    public void keyPressed(KeyEvent arg0) {
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
//        informParent();
    }

    @Override
    public void keyTyped(KeyEvent arg0) {

    }

  
}
