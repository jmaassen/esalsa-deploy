package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GUI {
	
	protected static int DEFAULT_PORT = 50656;
	
	public static final int DEFAULT_SCREEN_WIDTH = 1024;
    public static final int DEFAULT_SCREEN_HEIGHT = 700;

    protected static final Logger globalLogger = LoggerFactory.getLogger("eSalsa");

    private JFrame frame = null;
    private RootPanel myRoot;

    protected GUI(String address, int port) throws Exception { 
        myRoot = new RootPanel(this, address, port);        
    }

    private void close() {
        int choice = JOptionPane.showConfirmDialog(frame, "Really exit?", "Exiting eSalsa-Deploy", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
        	
        	myRoot.close();
            frame.dispose();
        	globalLogger.info("Goodbye...");
            System.exit(0);
        } else {
            // no, do nothing :)
        }
    }

    private void createAndShowGUI(String... logos) throws Exception {
        
        UIManager.put("swing.boldMetal", Boolean.FALSE);

    	frame = new JFrame("eSalsa Deploy");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                close();
            }
        });
        
        ImageIcon icon = Utils.createImageIcon("images/favicon.ico", null);
     
        if (icon != null) {
            frame.setIconImage(icon.getImage());
        }
        
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(myRoot, BorderLayout.CENTER);
        frame.setPreferredSize(new Dimension(DEFAULT_SCREEN_WIDTH, DEFAULT_SCREEN_HEIGHT));

        // Display the window.
        frame.pack();
    } 

    protected void ready() {
        // center on screen
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    	
    	globalLogger.info("Login completed.");
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
    	
    	
    	if (args.length > 2) { 
    		System.out.println("GUI <serveraddress> <serverport>");
    		System.exit(1);
    	}
    	
    	String server = "localhost";
    	int port = DEFAULT_PORT;

    	if (args.length >= 1) { 
    		server = args[0];
    	}
    	
    	if (args.length == 2) { 
    		port = Integer.parseInt(args[2]);
    		
    		if (port < 0 || port > 65535) { 
    			System.err.println("Illegal port: " + args[2]);
    			System.exit(1);
    		}
    	}
            	
    	try {
            final GUI gui = new GUI(server, port);
    		
    		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
            
            // Schedule a job for the event-dispatching thread:
            // creating and showing this application's GUI.
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        gui.createAndShowGUI();
                    } catch (Exception e) {
                        e.printStackTrace(System.err);
                        System.exit(1);
                    }
                }
            });
    	} catch (Exception e) {
    		System.err.println("GUI failed unexpectedly: " + e);
    		e.printStackTrace(System.err);
    	}
    }
}
