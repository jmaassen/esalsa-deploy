package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import nl.esciencecenter.esalsa.deploy.ConfigurationTemplate;
import nl.esciencecenter.esalsa.deploy.ExperimentInfo;
import nl.esciencecenter.esalsa.deploy.ExperimentTemplate;
import nl.esciencecenter.esalsa.deploy.FileSet;
import nl.esciencecenter.esalsa.deploy.WorkerDescription;
import nl.esciencecenter.esalsa.deploy.server.SimpleStub;

public class RootPanel extends JPanel {

	private static final long serialVersionUID = 2685960743908025422L;

	private final JTabbedPane tabs;

	private final RemoteStore<ExperimentTemplate> experimentStore;
	private final Editor<ExperimentTemplate> experimentEditor;
	private final StorePanel<ExperimentTemplate> experimentPanel;

	private final RemoteStore<WorkerDescription> workerStore;
	private final Editor<WorkerDescription> workerEditor;
	private final StorePanel<WorkerDescription> workerPanel;

	private final RemoteStore<FileSet> inputStore;
	private final Editor<FileSet> inputEditor;
	private final StorePanel<FileSet> inputPanel;

	private final RemoteStore<ConfigurationTemplate> configurationStore;
	private final Editor<ConfigurationTemplate> configurationEditor;
	private final StorePanel<ConfigurationTemplate> configurationPanel;

	private final RemoteStore<ExperimentInfo> preparedStore;
	private final Viewer<ExperimentInfo> preparedViewer;
	private final StorePanel<ExperimentInfo> preparedPanel;

	private final RemoteStore<ExperimentInfo> runningStore;
	private final Viewer<ExperimentInfo> runningViewer;
	private final StorePanel<ExperimentInfo> runningPanel;

	private final RemoteStore<ExperimentInfo> completedStore;
	private final Viewer<ExperimentInfo> completedViewer;
	private final StorePanel<ExperimentInfo> completedPanel;
	
	private final SimpleStub stub;
	
	public RootPanel(SimpleStub stub) throws Exception {

		this.stub = stub;
		
		setLayout(new BorderLayout());
		tabs = new JTabbedPane();
		
		workerStore = new RemoteStore<WorkerDescription>(stub, SimpleStub.getKey("worker"));
		workerEditor = new WorkerEditor(this, stub, workerStore);
		workerPanel = new StorePanel<WorkerDescription>(workerStore, workerEditor);

		inputStore = new RemoteStore<FileSet>(stub, SimpleStub.getKey("inputs"));
		inputEditor = new FileSetEditor(this, stub, inputStore);
		inputPanel = new StorePanel<FileSet>(inputStore, inputEditor);

		configurationStore = new RemoteStore<ConfigurationTemplate>(stub, SimpleStub.getKey("configuration"));
		configurationEditor = new ConfigurationEditor(this, stub, configurationStore);
		configurationPanel = new StorePanel<ConfigurationTemplate>(configurationStore, configurationEditor);

		experimentStore = new RemoteStore<ExperimentTemplate>(stub, SimpleStub.getKey("experiment"));
		experimentEditor = new ExperimentTemplateEditor(this, stub, experimentStore);
		experimentPanel = new StorePanel<ExperimentTemplate>(experimentStore, experimentEditor);

		preparedStore = new RemoteStore<ExperimentInfo>(stub, SimpleStub.getKey("waiting"));
		preparedViewer = new WaitingExperimentViewer(this, stub, preparedStore);
		preparedPanel = new StorePanel<ExperimentInfo>(preparedStore, preparedViewer);
		
		runningStore = new RemoteStore<ExperimentInfo>(stub, SimpleStub.getKey("running"));
		runningViewer = new RunningExperimentViewer(this, stub,  runningStore);
		runningPanel = new StorePanel<ExperimentInfo>(runningStore, runningViewer);
		
		completedStore = new RemoteStore<ExperimentInfo>(stub, SimpleStub.getKey("completed"));
		completedViewer = new ExperimentViewer(this, stub, completedStore);
		completedPanel = new StorePanel<ExperimentInfo>(completedStore, completedViewer);
				
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
			System.err.println("Failed to refresh " + who + " store");
			System.err.println(e.getLocalizedMessage());
			e.printStackTrace(System.err);
		}
	}
}
