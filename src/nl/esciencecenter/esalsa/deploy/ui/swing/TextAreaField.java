package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;

@SuppressWarnings("serial")
public class TextAreaField extends BorderedEditorField implements DocumentListener {

	private JTextArea textArea;
	private JTextArea lines;
	private Highlighter highlighter;
	
	public class MyHighlighter extends DefaultHighlighter.DefaultHighlightPainter {
		public MyHighlighter(Color c) {
			super(c);
		}
	}
	
    public TextAreaField(String title, boolean editable, boolean lineCount) {
        this(title, title, false, editable, lineCount, -1, 5*Utils.defaultFieldHeight);
    }

    public TextAreaField(String title, String key, boolean mayBeEmpty, boolean editable, boolean lineCount, int width, int height) {
        
    	super(title, key, mayBeEmpty);

    	/* Set up JtextArea */
		textArea = new JTextArea();
		textArea.setEditable(editable);
		
		textArea.getDocument().addDocumentListener(this);

		/* Set up Highlighter */
		highlighter = textArea.getHighlighter();

		/* Set up scroll pane */
		JScrollPane jsp = new JScrollPane();
		jsp.getViewport().add(textArea);
		
		/* Set up line numbers */
		if (lineCount) {

			lines = new JTextArea("1 ");
			lines.setBackground(new Color(240, 240, 240));
			lines.setForeground(Color.DARK_GRAY);

			lines.setEditable(false);
			lines.addMouseListener(this);
			
			lines.setBorder(new LineBorder(new Color(230, 230, 230)));
			
			jsp.setRowHeaderView(lines);
			
		} else { 
			lines = null;
		}
		
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		add(jsp);
		setPreferredSize(new Dimension(width, height));
    }
    
    /* Document Listener Events */
	public void changedUpdate(DocumentEvent de) {
		highlighter.removeAllHighlights();
		resetError();
		
		setLines();
	}
	public void insertUpdate(DocumentEvent de) {
		highlighter.removeAllHighlights();
		resetError();
		
		setLines();
	}
	public void removeUpdate(DocumentEvent de) {
		highlighter.removeAllHighlights();
		resetError();
		
		setLines();
	}
	
	public void setLines() {
		
		if (lines == null) { 
			return;
		}
		
		int caretPosition = textArea.getDocument().getLength();
		
		Element root = textArea.getDocument().getDefaultRootElement();
		
		String text = "1 \n";
		
		for(int i = 2; i < root.getElementIndex( caretPosition ) + 2; i++) { 
			text += i + " \n";
		}
		
		lines.setText(text);
		//lines.setCaretPosition(jta.getCaretPosition());		
	}
    
    public void setPosition(int pos) {
    	// textPane.setCaretPosition(pos);
    }
    
    public void setErrorLine(int line) { 
    	
    	try {    		
    		int start = textArea.getLineStartOffset(line-1);
    		int end = textArea.getLineEndOffset(line-1);
    		
    		//System.out.println("Highlight " + start + " " + end);
    		
			highlighter.addHighlight(start, end, new MyHighlighter(ERROR_COLOR));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}    	
    }
    
    public Object getValue() {
    	return textArea.getText();	
    }

    @Override
    public void setValue(Object value) {

    	if (lines != null) { 
    		lines.setText("");
    	} 
    	textArea.setText("");
    	
    	if (value == null) { 
    		return;
    	}
    	
    	if (value instanceof String) { 
    		textArea.setText((String) value);
    		setLines();
    	}
    }
    
    @Override
	public boolean isEmpty() {
		
    	String text = textArea.getText();
    	
    	if (text == null) { 
    		return true;
    	}
    	
    	text = text.trim();
    	
    	return (text.length() == 0); 
	}

	@Override
	public void clear() {

		if (lines != null) { 
			lines.setText("");
		} 
		textArea.setText("");		
	}

	@Override
	public boolean checkCorrectness() {
		return true;
	}
	
	
	public void setEnabled(boolean value) { 
		
		//System.out.println("TextAreaField setEnables(" + value + ")");
		
		super.setEnabled(value);

		if (lines != null) { 
			lines.setEnabled(value);
		} 
		
		textArea.setEnabled(value);
	}

	
}
