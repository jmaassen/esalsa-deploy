package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.BorderLayout;

@SuppressWarnings("serial")
public class TextEditingPanel extends MyPanel { 
	
    private final TextListener parent;
    
    private class OK implements ButtonHandler {
		@Override
		public void clicked() {
			save();
		} 
    }

    private class Cancel implements ButtonHandler {
		@Override
		public void clicked() {
			cancel();
		} 
    }
    
    private class Check implements ButtonHandler {

		@Override
		public void clicked() {
			check();
		} 
    }

    private final TextAreaField text; 
    
	public TextEditingPanel(TextListener parent, String key, boolean editable) {
		
		super(null);
		
		this.parent = parent;
		
		text = new TextAreaField(key, editable, true);
		
		container.add(text, BorderLayout.CENTER);		
		
		if (editable) {
			addButton("OK", new OK());
			addButton("Check", new Check());			
			addButton("Cancel", new Cancel());
		}
	}

	public void setText(String value) { 
		text.setValue(value);
	}
	
	public String getText() { 
		return (String) text.getValue();
	}
		
	public void showParseError(int line, String message) { 
		text.setErrorLine(line);
		text.setError("Error on line " + line + ": " + message);	
	}
	
	public void save() { 
		parent.save((String) text.getValue());	
	}
	
	public void check() { 
		parent.check((String) text.getValue());	
	}
	
	public void cancel() { 
		parent.cancel();
	}
}
