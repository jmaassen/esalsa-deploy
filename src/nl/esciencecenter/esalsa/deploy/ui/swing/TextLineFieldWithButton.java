package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import nl.esciencecenter.esalsa.deploy.parser.ParseException;

@SuppressWarnings("serial")
public class TextLineFieldWithButton extends TextLineField implements ActionListener, TextListener {
	
	public static final int DEFAULT_SCREEN_WIDTH = 1024;
    public static final int DEFAULT_SCREEN_HEIGHT = 700;
	
	private JFrame frame = null; 
	
	private final TextEditingPanel panel;
	private final EditorListener parser;
	
	private String data;
	private JButton button;
	
	private boolean editing = false;
	
    public TextLineFieldWithButton(String title, String line, String buttonText, boolean mayBeEmpty, boolean editable, EditorListener parser) {
    	super(title, mayBeEmpty);
    	
    	this.parser = parser;
        	
    	button = new JButton(buttonText);
    	button.addActionListener(this);
    	
    	add(button, BorderLayout.EAST);
    	
    	frame = new JFrame();
    	frame.getContentPane().setLayout(new BorderLayout());
         
    	panel = new TextEditingPanel(this, title, editable);
    	
    	frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.setPreferredSize(new Dimension(DEFAULT_SCREEN_WIDTH, DEFAULT_SCREEN_HEIGHT));
        frame.pack();

        // center on screen
        // frame.setLocationRelativeTo(null);
        
    	setLine(line);
    }

    public void setLine(String value) { 
    	
    	textField.setText("");
		
		if (value == null) { 
			return;
		}
		
    	if (value instanceof String) { 
    		textField.setText((String) value);
    	} 
    }
	
    @Override
    public void setValue(Object value) {
		
    	String text = "";
		
		if (value != null && value instanceof String) { 
    		text = (String) value;
    	}
    	
    	panel.setText(text);
    }
    
	@Override
    public Object getValue() {
		return data;
    }

    @Override
	public void actionPerformed(ActionEvent e) {
		// One of the buttons was clicked!
    	frame.setVisible(true);
    	
    	if (parser != null) { 
    		parser.startedEditing();
    	}
    }

	@Override
	public void check(String text) {
		
		if (parser != null) { 
			try { 
				parser.parse(text);
			} catch (ParseException e) {
				panel.showParseError(e.getLine(), e.getMessage());
				return;
			}
		}
	}
    
	@Override
	public void save(String text) {
		
		if (parser != null) { 
			
			try { 
				parser.parse(text);
			} catch (ParseException e) {
				panel.showParseError(e.getLine(), e.getMessage());
				return;
			}
			
			parser.stoppedEditing();
		}

		data = text;
		frame.setVisible(false);
	}

	@Override
	public void cancel() {
		frame.setVisible(false);

		if (parser != null) { 
			parser.stoppedEditing();
		}
	}
	
	public void setEnabled(boolean value) { 
		
		super.setEnabled(value);
		button.setEnabled(value);
	}
}

