package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import nl.esciencecenter.esalsa.deploy.StoreableObject;

@SuppressWarnings("serial")
public class StorePanel<T extends StoreableObject> extends JPanel {
	
	private StoreListView<T> list;
	private Viewer<T> viewer;
	
    public StorePanel(StoreListView<T> list, Viewer<T> viewer) {
    
    	this.list = list;
    	this.viewer = viewer;
    	
    	JSplitPane horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, list, viewer);
        horizontalSplitPane.setOneTouchExpandable(true);
        horizontalSplitPane.setDividerLocation((int) (GUI.DEFAULT_SCREEN_WIDTH * 0.35));
        horizontalSplitPane.setResizeWeight(0.5);

        setLayout(new BorderLayout());
        add(horizontalSplitPane, BorderLayout.CENTER);
    }
    
    public void setEnabled(boolean value) { 

    	System.out.println("StorePanel setEnables(" + value + ")");

    	super.setEnabled(value);
    	
    	for (Component c : getComponents()) { 
			c.setEnabled(value);
		}
    	
    	list.setEnabled(value);
    	viewer.setEnabled(value);
    }

    /*
    protected void disableMe() { 
    	
    	
    	list.disableMe();
    	viewer.disableMe();
    }
    
    protected void enableMe() {
    	
    	super.setEnabled(true);
    	
    	list.enableMe();
		viewer.enableMe();
    }*/
    
}