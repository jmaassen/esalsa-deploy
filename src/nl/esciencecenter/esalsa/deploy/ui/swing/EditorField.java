package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

public abstract class EditorField extends JPanel implements MouseListener {

	private static final long serialVersionUID = -3930224981501541391L;
	
	private final Border normalBorder;
	
	public final String title;
	public final String key;
	public final boolean mayBeEmpty;

	private boolean error = false;
	
	public EditorField (String title, String key, boolean mayBeEmpty) {
		super(new BorderLayout());
		this.key = key;
		this.title = title;
		this.mayBeEmpty = mayBeEmpty;
		
		normalBorder = BorderFactory.createTitledBorder(title);
		
		setBorder(normalBorder);
	}
	
	public abstract Object getValue();
    public abstract void setValue(Object setValue);    
    public abstract boolean isEmpty();
    
    public void setError(String text) { 
    	
    	error = true;
    	
    	String tmp = title;
    	
    	if (text != null && text.length() > 0) { 
    		tmp = tmp + " (" + text + ")";
    	}
    	
    	setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), tmp));
    }
	
    @Override
	public void mouseClicked(MouseEvent e) {    	
    	if (error) { 
    		error = false;
    		setBorder(normalBorder);
    	}
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
