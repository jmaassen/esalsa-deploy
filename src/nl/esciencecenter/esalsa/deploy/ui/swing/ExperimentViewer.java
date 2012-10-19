package nl.esciencecenter.esalsa.deploy.ui.swing;

import nl.esciencecenter.esalsa.deploy.ExperimentInfo;
import nl.esciencecenter.esalsa.deploy.server.SimpleStub;

@SuppressWarnings("serial")
public class ExperimentViewer extends Viewer<ExperimentInfo> {

	private final boolean showLogs; 
	private final boolean showState;

	public ExperimentViewer(RootPanel parent, SimpleStub stub, RemoteStore<ExperimentInfo> store, boolean showLogs) { 	
		super(parent, stub, store, false);
		
		this.showLogs = showLogs;
		this.showState = showLogs;

		if (showState) { 
			addField(new TextLineField("State", false));
		}
		
		addField(new URIField("URI", false));
		
		addField(new TextLineField("POP log file", false));

		addField(new TextLineField("Total job submissions", false));
		
		if (showState) { 
			addField(new TextLineField("Current job submission", false));
		}
		
/*		
		if (colapseDetails) { 
			CollapsiblePanel tmp = new CollapsiblePanel("Experiment Details");
			
			JPanel container = new JPanel();
			container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
			container.setBorder(new EmptyBorder(5, 5, 5, 5));		
			container.add(Box.createRigidArea(new Dimension(0, Utils.gapHeight)));						
			
			addField(new URIField("Template Directory", false));
			addField(new URIField("Experiment Directory", false));
			addField(new URIField("Input Directory", false));
			addField(new URIField("Output Directory", false));
		
			addField(new URIField("POP log file", false));
		
			addField(new URIField("Start Script", false));
			addField(new URIField("Monitor Script", false));
			addField(new URIField("Stop Script", false));
		
			addField(new TextAreaField("Configuration", "Configuration", false, true,  true, -1, 15*Utils.defaultFieldHeight)); 
		
			tmp.add(container);
			formPanel.add(tmp);
			
		} else {
*/
			addField(new TextLineField("Template Directory", false));
			addField(new TextLineField("Experiment Directory", false));
			addField(new TextLineField("Input Directory", false));
			addField(new TextLineField("Output Directory", false));
		
			addField(new TextLineField("Start Script", false));
			addField(new TextLineField("Monitor Script", false));
			addField(new TextLineField("Stop Script", false));
		
			addField(new TextAreaField("Configuration", "Configuration", false, true,  true, -1, 15*Utils.defaultFieldHeight)); 
//		}
		
		if (showLogs) { 
			addField(new TextAreaField("Log", "Log", true, false, true, -1, 15*Utils.defaultFieldHeight));
			addField(new TextAreaField("POP Log", "POP Log", true, false, true, -1, 15*Utils.defaultFieldHeight));
		}
	}

	@Override
	public void show(ExperimentInfo elt) {
		setElementValue("ID", elt.ID);
		setElementValue("Comment", elt.getComment());
		
		setElementValue("URI", elt.jobServer);

		setElementValue("Total job submissions", "" + elt.totalRestarts);
		
		setElementValue("Template Directory", elt.templateDir);
		setElementValue("Experiment Directory", elt.experimentDir);
		setElementValue("Input Directory", elt.inputDir);
		setElementValue("Output Directory", elt.outputDir);
		
		setElementValue("POP log file", elt.popLogFile);
		
		setElementValue("Start Script", elt.startScript);
		setElementValue("Monitor Script", elt.monitorScript);
		setElementValue("Stop Script", elt.stopScript);
		
		setElementValue("Configuration", elt.configuration);
		
		if (showState) { 
			setElementValue("State", elt.getState());
			setElementValue("Current job submission", "" + elt.getCurrentRun());
		}	
		
		if (showLogs) { 
			setElementValue("Log", elt.getDeployLog());
			setElementValue("POP Log", elt.getLogPOP());
		}
	}	
}
