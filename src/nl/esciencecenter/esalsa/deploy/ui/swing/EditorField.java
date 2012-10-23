package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class EditorField extends JPanel implements MouseListener {

	public static final Color ERROR_COLOR = new Color(255, 0, 0, 128); 
	public static final Color NORMAL_COLOR = Color.WHITE; 
	
	public final boolean mayBeEmpty;
	public final String key;
	
	protected boolean error = false;
	
	public EditorField(String key, boolean mayBeEmpty) {
		super(new BorderLayout());
		this.key = key;
		this.mayBeEmpty = mayBeEmpty;
	}
    
	public void mouseClicked(MouseEvent e) {
    	resetError();
 	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}
		
	public abstract Object getValue();
	public abstract void setValue(Object setValue);    
    public abstract void clear();    

	public abstract boolean isEmpty();
    public abstract boolean checkCorrectness();
    public abstract void setError(String text);
    public abstract void resetError();    
}
