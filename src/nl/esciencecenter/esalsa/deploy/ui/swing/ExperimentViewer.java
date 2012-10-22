package nl.esciencecenter.esalsa.deploy.ui.swing;

import nl.esciencecenter.esalsa.deploy.ExperimentInfo;
import nl.esciencecenter.esalsa.deploy.server.SimpleStub;

@SuppressWarnings("serial")
public class ExperimentViewer extends Viewer<ExperimentInfo> {

	private final boolean showLogs; 
	private final boolean showState;

	private final TextLineFieldWithButton popLog;
	
	public ExperimentViewer(RootPanel parent, SimpleStub stub, RemoteStore<ExperimentInfo> store, boolean showLogs) { 	
		super(parent, stub, store, false);
		
		this.showLogs = showLogs;
		this.showState = showLogs;

		if (showState) { 
			addField(new TextLineField("State", false));
			addField(new TextLineField("Last update", false));			
		}
		
		addField(new URIField("URI", false));
		
		addField(new TextLineField("POP log file", false));

		addField(new TextLineField("Total job submissions", false));
		
		if (showState) { 
			addField(new TextLineField("Current job submission", false));
		}
		
		addField(new TextLineField("Template Directory", false));
		addField(new TextLineField("Experiment Directory", false));
		addField(new TextLineField("Input Directory", false));
		addField(new TextLineField("Output Directory", false));

		addField(new TextLineField("Start Script", false));
		addField(new TextLineField("Monitor Script", false));
		addField(new TextLineField("Stop Script", false));

		addField(new TextLineFieldWithButton("Configuration", "pop_in",  "show", false, false, null));
		//addField(new TextAreaField("Configuration", "Configuration", false, true,  true, -1, 15*Utils.defaultFieldHeight)); 
		
		if (showLogs) { 
			//addField(new TextAreaField("Log", "Log", true, false, true, -1, 15*Utils.defaultFieldHeight));
			//addField(new TextAreaField("POP Log", "POP Log", true, false, true, -1, 15*Utils.defaultFieldHeight));

			popLog = new TextLineFieldWithButton("POP Log", "", "show", false, false, null);
			addField(new TextLineFieldWithButton("Deploy Log", "deploy.log", "show", false, false, null));
			addField(popLog);
		} else { 
			popLog = null;
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
			setElementValue("Last update", elt.getLastUpdate());			
			setElementValue("Current job submission", "" + elt.getCurrentRun());
		}	
		
		if (showLogs) { 
			setElementValue("Deploy Log", elt.getDeployLog());
			setElementValue("POP Log", elt.getLogPOP());
			popLog.setLine(elt.popLogFile);
		}
	}	
}
