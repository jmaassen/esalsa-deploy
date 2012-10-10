package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import nl.esciencecenter.esalsa.deploy.StoreableObject;

@SuppressWarnings("serial")
public class StorePanel<T extends StoreableObject> extends JPanel {
	
    public StorePanel(RemoteStore<T> store, Viewer<T> viewer) {
    
        // Create a pane containing list to the left and editor to the right.
    	StoreListView<T> list = new StoreListView<T>(store, viewer);

        JSplitPane horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, list, viewer);
        horizontalSplitPane.setOneTouchExpandable(true);
        horizontalSplitPane.setDividerLocation((int) (GUI.DEFAULT_SCREEN_WIDTH * 0.35));
        horizontalSplitPane.setResizeWeight(0.5);

        setLayout(new BorderLayout());
        add(horizontalSplitPane, BorderLayout.CENTER);
    }
}