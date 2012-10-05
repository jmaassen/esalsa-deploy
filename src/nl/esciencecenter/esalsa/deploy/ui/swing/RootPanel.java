package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import nl.esciencecenter.esalsa.deploy.ConfigurationTemplate;
import nl.esciencecenter.esalsa.deploy.ExperimentDescription;
import nl.esciencecenter.esalsa.deploy.ExperimentInfo;
import nl.esciencecenter.esalsa.deploy.FileSet;
import nl.esciencecenter.esalsa.deploy.WorkerDescription;
import nl.esciencecenter.esalsa.deploy.server.SimpleStub;
import nl.esciencecenter.esalsa.deploy.server.Stub;

public class RootPanel extends JPanel {

	private static final long serialVersionUID = 2685960743908025422L;

	private final JTabbedPane tabs;

	private final RemoteStore<ExperimentDescription> experimentStore;
	private final Editor<ExperimentDescription> experimentEditor;
	private final StorePanel<ExperimentDescription> experimentPanel;

	private final RemoteStore<WorkerDescription> workerStore;
	private final Editor<WorkerDescription> workerEditor;
	private final StorePanel<WorkerDescription> workerPanel;

	private final RemoteStore<FileSet> inputStore;
	private final Editor<FileSet> inputEditor;
	private final StorePanel<FileSet> inputPanel;

	private final RemoteStore<ConfigurationTemplate> configurationStore;
	private final Editor<ConfigurationTemplate> configurationEditor;
	private final StorePanel<ConfigurationTemplate> configurationPanel;

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
		workerEditor = new WorkerEditor(workerStore);
		workerPanel = new StorePanel<WorkerDescription>(workerStore, workerEditor);

		inputStore = new RemoteStore<FileSet>(stub, SimpleStub.getKey("inputs"));
		inputEditor = new FileSetEditor(inputStore);
		inputPanel = new StorePanel<FileSet>(inputStore, inputEditor);

		configurationStore = new RemoteStore<ConfigurationTemplate>(stub, SimpleStub.getKey("configuration"));
		configurationEditor = new ConfigurationEditor(configurationStore);
		configurationPanel = new StorePanel<ConfigurationTemplate>(configurationStore, configurationEditor);

		experimentStore = new RemoteStore<ExperimentDescription>(stub, SimpleStub.getKey("experiment"));
		experimentEditor = new ExperimentEditor(experimentStore);
		experimentPanel = new StorePanel<ExperimentDescription>(experimentStore, experimentEditor);
		
		runningStore = new RemoteStore<ExperimentInfo>(stub, SimpleStub.getKey("running"));
		runningViewer = new RunningExperimentViewer(runningStore);
		runningPanel = new StorePanel<ExperimentInfo>(runningStore, runningViewer);
		
		completedStore = new RemoteStore<ExperimentInfo>(stub, SimpleStub.getKey("completed"));
		completedViewer = new ExperimentViewer(completedStore);
		completedPanel = new StorePanel<ExperimentInfo>(completedStore, completedViewer);
/*		
		
		
		experimentStore.add(new ExperimentDescription("POP_1degree_DAS4VU", "config.1-degree.x1", "DAS4VU.1d.c", "input.1-degree.x1", "This is a 1 degree POP run using 32 cores (4 nodes) of the VU DAS4 cluster."));
		experimentStore.add(new ExperimentDescription("POP_1degree_DAS4LU", "config.1-degree.x1", "DAS4LU.1d.c", "input.1-degree.x1", "This is a 1 degree POP run using 32 cores (4 nodes) of the Leiden DAS4 cluster."));
		
		HashMap<String, String> tmp = new HashMap<String, String>();
		
		tmp.put("nprocs_clinic", "32");
		tmp.put("nprocs_tropic", "32");
		tmp.put("clinic_distribution_type", "cartesian");
		tmp.put("tropic_distribution_type", "cartesian");
		tmp.put("distribution_file" , "unknown");

		workerStore.add(new WorkerDescription("DAS4VU.1d.c", 
				new URI("ssh://fs0.das4.cs.vu.nl"),
				new URI("ssh://fs0.das4.cs.vu.nl"), 
				"/var/scratch/jason/esalsa/experiments/input", 
				"/var/scratch/jason/esalsa/experiments/output", 
				"/home/jason/esalsa/experiments",
				"/home/jason/esalsa/templates/pop_1d_c_32c", 
				"1 degree cartesian POP on the VU DAS4 cluster.", 
				tmp));

		workerStore.add(new WorkerDescription("DAS4LU.1d.c", 
				new URI("ssh://fs1.das4.liacs.nl"),
				new URI("ssh://fs1.das4.liacs.nl"), 
				"/var/scratch/jason/esalsa/experiments/input", 
				"/var/scratch/jason/esalsa/experiments/output", 
				"/home/jason/esalsa/experiments",
				"/home/jason/esalsa/templates/pop_1d_c_32c", 
				"1 degree cartesian POP on the VU DAS4 cluster.", 
				tmp));

		inputStore.add(new FileSet("input.1-degree.x1", "This is a fileset comment", new URI [] {
				new URI("ssh://fs0.das4.cs.vu.nl/var/scratch/jason/POP/input_small/grid/horiz_grid.x1"), 
				new URI("ssh://fs0.das4.cs.vu.nl/var/scratch/jason/POP/input_small/grid/vert_grid.x1"),
				new URI("ssh://fs0.das4.cs.vu.nl/var/scratch/jason/POP/input_small/grid/topography.x1"), 
				new URI("ssh://fs0.das4.cs.vu.nl/var/scratch/jason/POP/input_small/config/transport_contents.x1"), 
				new URI("ssh://fs0.das4.cs.vu.nl/var/scratch/jason/POP/input_small/config/tavg_contents.x1") }));

		configurationStore.add(new ConfigurationTemplate("config.1-degree.x1", "This is a config comment"));
*/
		
				
		tabs.addTab("Workers", Utils.createImageIcon(
				"images/utilities-system-monitor.png", "Workers Tab"),
				workerPanel);

		tabs.addTab("Inputs", Utils.createImageIcon(
				"images/utilities-system-monitor.png", "Inputs Tab"),
				inputPanel);

		tabs.addTab("Configurations", Utils.createImageIcon(
				"images/utilities-system-monitor.png", "Configurations Tab"),
				configurationPanel);

		tabs.addTab("Experiments", Utils.createImageIcon(
				"images/utilities-system-monitor.png", "Experiments Tab"),
				experimentPanel);

		tabs.addTab("Running", Utils.createImageIcon(
				"images/utilities-system-monitor.png", "Running Tab"),
				runningPanel);

		tabs.addTab("Completed", Utils.createImageIcon(
				"images/utilities-system-monitor.png", "Completed Tab"),
				completedPanel);

		add(tabs, BorderLayout.CENTER);
	}
/*
	private void initializeWorkers() { 
		try { 
			List<String> tmp = stub.listWorkerDescriptions();
			
			for (String s : tmp) { 
				workerStore.add(stub.getWorkerDescription(s));
			}
		} catch (Exception e) {
			System.err.println("Failed to update worker store!");
			e.printStackTrace(System.err);
		}
	}
	
	private void initializeInputs() { 
		try { 
			List<String> tmp = stub.listInputFileSets();
			
			for (String s : tmp) { 
				inputStore.add(stub.getInputFileSet(s));
			}
		} catch (Exception e) {
			System.err.println("Failed to update worker store!");
			e.printStackTrace(System.err);
		}
	}
	
	private void initializeTemplates() { 
		try { 
			List<String> tmp = stub.listConfigurationTemplates();
			
			for (String s : tmp) { 
				configurationStore.add(stub.getConfigurationTemplate(s));
			}
		} catch (Exception e) {
			System.err.println("Failed to update worker store!");
			e.printStackTrace(System.err);
		}
	}
	
	private void initializeExperiments() { 
		try { 
			List<String> tmp = stub.listExperimentDescriptions();
			
			for (String s : tmp) { 
				experimentStore.add(stub.getExperimentDescription(s));
			}
		} catch (Exception e) {
			System.err.println("Failed to update worker store!");
			e.printStackTrace(System.err);
		}
	}
	
	public void initializeData() { 
		initializeWorkers();
		initializeInputs();
		initializeTemplates();
		initializeExperiments();
	}
*/	
}
