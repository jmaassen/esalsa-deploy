package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.esciencecenter.esalsa.deploy.ConfigurationTemplate;
import nl.esciencecenter.esalsa.deploy.ExperimentInfo;
import nl.esciencecenter.esalsa.deploy.ExperimentTemplate;
import nl.esciencecenter.esalsa.deploy.FileSet;
import nl.esciencecenter.esalsa.deploy.WorkerDescription;
import nl.esciencecenter.esalsa.deploy.server.SimpleStub;
import nl.esciencecenter.esalsa.deploy.server.Util;

public class RootPanel extends JPanel {

	private static final Logger globalLogger = LoggerFactory.getLogger("eSalsa");

	private static int DEFAULT_TIMEOUT = 30000;

	private static final long serialVersionUID = 2685960743908025422L;

	private final JTabbedPane tabs;

	private String server;
	private int port;

	private RemoteStore<ExperimentTemplate> experimentStore;
	private StorePanel<ExperimentTemplate> experimentPanel;

	private RemoteStore<WorkerDescription> workerStore;
	private StorePanel<WorkerDescription> workerPanel;

	private RemoteStore<FileSet> inputStore;	
	private StorePanel<FileSet> inputPanel;

	private RemoteStore<ConfigurationTemplate> configurationStore;
	private StorePanel<ConfigurationTemplate> configurationPanel;

	private RemoteStore<ExperimentInfo> preparedStore;
	private StorePanel<ExperimentInfo> preparedPanel;

	private RemoteStore<ExperimentInfo> runningStore;
	private StorePanel<ExperimentInfo> runningPanel;

	private RemoteStore<ExperimentInfo> completedStore;
	private StorePanel<ExperimentInfo> completedPanel;

	private SimpleStub stub;

	private GUI parent;
	private JFrame loginFrame;
	
	public RootPanel(GUI parent, String server, int port) throws Exception {

		this.parent = parent;
		this.server = server;
		this.port = port;

		setLayout(new BorderLayout());
		tabs = new JTabbedPane();		
		showLogin();
	} 
	
	protected void showErrorMessage(String message, Exception e) {
		JOptionPane.showMessageDialog(this, message + "\n(" + e.getLocalizedMessage() + ")");
	}
	
	protected boolean askConfirmation(String message) { 		
		
		String [] options = new String [] { "OK", "Cancel" };
		String selected = "Cancel";
		
		int result = JOptionPane.showOptionDialog(this, message, "WARNING", JOptionPane.OK_CANCEL_OPTION, 
				JOptionPane.WARNING_MESSAGE, null, options, selected);

		return (result == 0);
	}
	
	private void showLogin() { 

		LoginPanel login = new LoginPanel(this, server, port);

		loginFrame = new JFrame();
		//make sure the program exits when the frame closes
		loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		loginFrame.setTitle("Login to server");
		//loginFrame.setSize(500,300);

		//This will center the JFrame in the middle of the screen

		loginFrame.getContentPane().setLayout(new BorderLayout());
		loginFrame.getContentPane().add(login, BorderLayout.CENTER);
		
		loginFrame.setPreferredSize(new Dimension(300,230));

		
		// Display the window.
		loginFrame.pack();
		
		loginFrame.setLocationRelativeTo(null);
		loginFrame.setVisible(true);
		loginFrame.setAlwaysOnTop(true);		
	}	

	protected void login(String server, int port, String password) { 

		this.server = server;
		this.port = port;

		try {
			stub = Util.connect(server, port, DEFAULT_TIMEOUT, password, null);
		} catch (Exception e) {
			showErrorMessage("Failed to connect to server: " + server + ":" + port, e);
			return;
		} 
		
		loginFrame.setVisible(false);
		loginFrame.dispose();
		
		try { 
			init();
		} catch (Exception e) {
			showErrorMessage("INTERNAL ERROR: Failed to initialize GUI!", e);
			System.exit(1);
		}
	}
	
	protected void cancelLogin() { 
		
		boolean confirm = askConfirmation("Are you sure you want to exit?");
		
		if (confirm) { 
			System.exit(1);
		} 
	} 
		
	private void init() throws Exception {

		//System.out.println("In Init!");
		
		workerStore = new RemoteStore<WorkerDescription>(stub, SimpleStub.getKey("worker"));
		Editor<WorkerDescription> workerEditor = new WorkerEditor(this, stub, workerStore);
		StoreListView<WorkerDescription> workerList = new StoreListView<WorkerDescription>(this, stub, workerStore, workerEditor, true);
		workerEditor.setViewer(workerList);
		workerPanel = new StorePanel<WorkerDescription>(workerList, workerEditor);

		inputStore = new RemoteStore<FileSet>(stub, SimpleStub.getKey("inputs"));
		Editor<FileSet> inputEditor = new FileSetEditor(this, stub, inputStore);
		StoreListView<FileSet> inputList = new StoreListView<FileSet>(this, stub, inputStore, inputEditor, true);		
		inputEditor.setViewer(inputList);
		inputPanel = new StorePanel<FileSet>(inputList, inputEditor);

		configurationStore = new RemoteStore<ConfigurationTemplate>(stub, SimpleStub.getKey("configuration"));
		Editor<ConfigurationTemplate> configurationEditor = new ConfigurationEditor(this, stub, configurationStore);
		StoreListView<ConfigurationTemplate> configurationList = new StoreListView<ConfigurationTemplate>(this, stub, configurationStore, configurationEditor, true);
		configurationEditor.setViewer(configurationList);
		configurationPanel = new StorePanel<ConfigurationTemplate>(configurationList, configurationEditor);

		experimentStore = new RemoteStore<ExperimentTemplate>(stub, SimpleStub.getKey("experiment"));
		Editor<ExperimentTemplate> experimentEditor = new ExperimentTemplateEditor(this, stub, experimentStore);
		ExperimentTemplateList experimentList = new ExperimentTemplateList(this, stub, experimentStore, experimentEditor);
		experimentEditor.setViewer(experimentList);
		experimentPanel = new StorePanel<ExperimentTemplate>(experimentList, experimentEditor);

		preparedStore = new RemoteStore<ExperimentInfo>(stub, SimpleStub.getKey("waiting"));
		ExperimentViewer waitingViewer = new ExperimentViewer(this, stub, preparedStore, false);
		WaitingExperimentList waitingList = new WaitingExperimentList(this, stub, preparedStore, waitingViewer);
		waitingViewer.setViewer(waitingList);
		preparedPanel = new StorePanel<ExperimentInfo>(waitingList, waitingViewer);

		runningStore = new RemoteStore<ExperimentInfo>(stub, SimpleStub.getKey("running"));
		ExperimentViewer runningViewer = new ExperimentViewer(this, stub,  runningStore, true);
		RunningExperimentList runningList = new RunningExperimentList(this, stub, runningStore, runningViewer);
		runningViewer.setViewer(runningList);
		runningPanel = new StorePanel<ExperimentInfo>(runningList, runningViewer);

		completedStore = new RemoteStore<ExperimentInfo>(stub, SimpleStub.getKey("completed"));
		ExperimentViewer completedViewer = new ExperimentViewer(this, stub, completedStore, true);		
		StoreListView<ExperimentInfo> completedList = new StoreListView<ExperimentInfo>(this, stub, completedStore, completedViewer, true);
		completedViewer.setViewer(completedList);
		completedPanel = new StorePanel<ExperimentInfo>(completedList, completedViewer);

		tabs.addTab("Workers", Utils.createImageIcon(
				"images/computer.png", "Workers Tab"),
				workerPanel);

		tabs.addTab("Inputs", Utils.createImageIcon(
				"images/disks.png", "Inputs Tab"),
				inputPanel);

		tabs.addTab("Configuration Templates", Utils.createImageIcon(
				"images/blog.png", "Configurations Tab"),
				configurationPanel);

		tabs.addTab("Experiment Templates", Utils.createImageIcon(
				"images/beaker.png", "Experiments Tab"),
				experimentPanel);

		tabs.addTab("Prepared", Utils.createImageIcon(
				"images/control-pause.png", "Prepared Tab"),
				preparedPanel);

		tabs.addTab("Running", Utils.createImageIcon(
				"images/control.png", "Running Tab"),
				runningPanel);

		tabs.addTab("Completed", Utils.createImageIcon(
				"images/control-stop-square.png", "Completed Tab"),
				completedPanel);

		add(tabs, BorderLayout.CENTER);
		
		parent.ready();
	}

	protected void close() { 
		stub.close();
	}

	public void setEnabled(boolean value) { 
		super.setEnabled(value);

		tabs.setEnabled(value);

		experimentPanel.setEnabled(value);
		workerPanel.setEnabled(value);
		inputPanel.setEnabled(value);
		configurationPanel.setEnabled(value);
		preparedPanel.setEnabled(value);
		runningPanel.setEnabled(value);
		completedPanel.setEnabled(value);
	}

	protected void disableMe() { 	
		setEnabled(false);
	}
	
	protected void enableMe() {
		setEnabled(true);
	}
	
	protected void refresh(String who) { 

		try { 
			if (who.equals("worker")) { 
				workerStore.refresh();
			} else if (who.equals("inputs")) { 
				inputStore.refresh();
			} else if (who.equals("configuration")) { 
				configurationStore.refresh();
			} else if (who.equals("experiment")) { 
				experimentStore.refresh();
			} else if (who.equals("waiting")) { 
				preparedStore.refresh();
			} else if (who.equals("running")) { 
				runningStore.refresh();
			} else if (who.equals("completed")) { 
				completedStore.refresh();
			} else if (who.equals("all")) { 
				workerStore.refresh();
				inputStore.refresh();
				configurationStore.refresh();
				experimentStore.refresh();
				preparedStore.refresh();
				runningStore.refresh();
				completedStore.refresh();
			}
		} catch (Exception e) { 
			globalLogger.error("Failed to refresh " + who + " store", e);
		}
	}
}
