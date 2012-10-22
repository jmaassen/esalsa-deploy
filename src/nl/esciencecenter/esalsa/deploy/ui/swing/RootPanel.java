package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.BorderLayout;
import java.awt.Component;

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
	private final StorePanel<ExperimentTemplate> experimentPanel;

	private final RemoteStore<WorkerDescription> workerStore;
	private final StorePanel<WorkerDescription> workerPanel;

	private final RemoteStore<FileSet> inputStore;	
	private final StorePanel<FileSet> inputPanel;

	private final RemoteStore<ConfigurationTemplate> configurationStore;
	private final StorePanel<ConfigurationTemplate> configurationPanel;

	private final RemoteStore<ExperimentInfo> preparedStore;
	private final StorePanel<ExperimentInfo> preparedPanel;

	private final RemoteStore<ExperimentInfo> runningStore;
	private final StorePanel<ExperimentInfo> runningPanel;

	private final RemoteStore<ExperimentInfo> completedStore;
	private final StorePanel<ExperimentInfo> completedPanel;
	
	private final SimpleStub stub;
	
	public RootPanel(SimpleStub stub) throws Exception {

		this.stub = stub;
		
		setLayout(new BorderLayout());
		tabs = new JTabbedPane();
		
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
	}
	
	public void setEnabled(boolean value) { 
		
		System.out.println("RootPanel setEnables(" + value + ")");
		
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
		
		/*
		setEnabled(false);
		
		experimentPanel.disableMe();
		workerPanel.disableMe();
		inputPanel.disableMe();
		configurationPanel.disableMe();
		preparedPanel.disableMe();
		runningPanel.disableMe();
		completedPanel.disableMe();	
	}*/
	
	protected void enableMe() {
		setEnabled(true);
	}
	
	/*
		experimentPanel.enableMe();
		workerPanel.enableMe();
		inputPanel.enableMe();
		configurationPanel.enableMe();
		preparedPanel.enableMe();
		runningPanel.enableMe();
		completedPanel.enableMe();
	}*/
	
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
