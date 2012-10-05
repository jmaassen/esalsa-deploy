package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;

import nl.esciencecenter.esalsa.deploy.server.SimpleStub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GUI {
	
	private static int DEFAULT_PORT = 50656;
	private static int DEFAULT_TIMEOUT = 30000;
    
	public static final int DEFAULT_SCREEN_WIDTH = 1024;
    public static final int DEFAULT_SCREEN_HEIGHT = 700;

    private static final Logger logger = LoggerFactory.getLogger(GUI.class);

    private JFrame frame = null;
    private RootPanel myRoot;

    private final String serverAddress;
    private final int serverPort;
    
    private Socket socket;
    private SimpleStub stub; 

    protected GUI(String address, int port) throws Exception { 
		
    	// connect to server here...
    	this.serverAddress = address;
    	this.serverPort = port;
    	
    	try { 
    		socket = new Socket(address, port);
    		socket.setSoTimeout(DEFAULT_TIMEOUT);
    		socket.setTcpNoDelay(true);
    		stub = new SimpleStub(socket);

    	} catch (Exception e) {
    		System.err.println("Failed to connect to server: " + serverAddress + ":" + port + " " + e);
    		e.printStackTrace(System.err);
    		throw e;
    	} 
    }

    private void close() {
        int choice = JOptionPane.showConfirmDialog(frame, "Really exit?", "Exiting eSalsa-Deploy", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            frame.dispose();
            System.exit(0);
        } else {
            // no, do nothing :)
        }
    }

    private void createAndShowGUI(String... logos) throws Exception {
        
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        frame = new JFrame("eSalsa Deploy");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        ImageIcon icon = Utils.createImageIcon("images/favicon.ico", null);
     
        if (icon != null) {
            frame.setIconImage(icon.getImage());
        }

        frame.getContentPane().setLayout(new BorderLayout());
        myRoot = new RootPanel(stub);
        frame.getContentPane().add(myRoot, BorderLayout.CENTER);
        frame.setPreferredSize(new Dimension(DEFAULT_SCREEN_WIDTH, DEFAULT_SCREEN_HEIGHT));

        // Display the window.
        frame.pack();

        // center on screen
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
    	
    	if (args.length == 0 || args.length > 2) { 
    		System.out.println("GUI [serveraddress] <serverport>");
    		System.exit(1);
    	}
    		
    	int port = DEFAULT_PORT;
    	
    	if (args.length == 2) { 
    		port = Integer.parseInt(args[2]);
    		
    		if (port < 0 || port > 65535) { 
    			System.err.println("Illegal port: " + args[2]);
    			System.exit(1);
    		}
    	}
            	
    	try {
            final GUI gui = new GUI(args[0], port);
    		
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
    		System.err.println("CLI Failed unexpectedly: " + e);
    		e.printStackTrace(System.err);
    	}
    }
}
