package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import nl.esciencecenter.esalsa.deploy.StoreableObject;

public class StorePanel<T extends StoreableObject> extends JPanel {

    private static final long serialVersionUID = -5264882651577509288L;

    private RemoteStore<T> store;    
    private Viewer<T> viewer;
    private StoreListView<T> list;
    
    public StorePanel(RemoteStore<T> store, Viewer<T> viewer) {
    	
    	this.store = store;
    	this.viewer = viewer;
    	
        list = new StoreListView<T>(store, viewer);

        // pane containing experiment editor to the left
        // and SmartSockets visualizer to the right
        JSplitPane horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, list, viewer);
        horizontalSplitPane.setOneTouchExpandable(true);
        horizontalSplitPane.setDividerLocation((int) (GUI.DEFAULT_SCREEN_WIDTH * 0.35));
        horizontalSplitPane.setResizeWeight(0.5);

        setLayout(new BorderLayout());
        add(horizontalSplitPane, BorderLayout.CENTER);
    }

}