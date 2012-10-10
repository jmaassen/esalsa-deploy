package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import nl.esciencecenter.esalsa.deploy.ExperimentInfo;
import nl.esciencecenter.esalsa.deploy.server.SimpleStub;

@SuppressWarnings("serial")
public class ExperimentViewer extends Viewer<ExperimentInfo> {

	private final boolean showLogs; 
	
	public ExperimentViewer(RootPanel parent, SimpleStub stub, RemoteStore<ExperimentInfo> store, boolean showLogs, boolean colapseDetails) { 	
		super(parent, stub, store, false);
		
		this.showLogs = showLogs;
		
		addField(new TextLineField("State", false));

		addField(new URIField("URI", false));

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
			addField(new URIField("Template Directory", false));
			addField(new URIField("Experiment Directory", false));
			addField(new URIField("Input Directory", false));
			addField(new URIField("Output Directory", false));
		
			addField(new URIField("POP log file", false));
		
			addField(new URIField("Start Script", false));
			addField(new URIField("Monitor Script", false));
			addField(new URIField("Stop Script", false));
		
			addField(new TextAreaField("Configuration", "Configuration", false, true,  true, -1, 15*Utils.defaultFieldHeight)); 
		}
		
		if (showLogs) { 
			addField(new TextAreaField("Log", "Log", true, false, true, -1, 15*Utils.defaultFieldHeight));
		} 
	}

	@Override
	public void show(ExperimentInfo elt) {
		setElementValue("ID", elt.ID);
		setElementValue("Comment", elt.getComment());
		setElementValue("State", elt.state);
		
		setElementValue("URI", elt.jobServer);
		
		setElementValue("Template Directory", elt.templateDir);
		setElementValue("Experiment Directory", elt.experimentDir);
		setElementValue("Input Directory", elt.inputDir);
		setElementValue("Output Directory", elt.outputDir);
		
		setElementValue("POP log file", elt.popLog);
		
		setElementValue("Start Script", elt.startScript);
		setElementValue("Monitor Script", elt.monitorScript);
		setElementValue("Stop Script", elt.stopScript);
		
		setElementValue("Configuration", elt.configuration);
		
		if (showLogs) { 
			setElementValue("Log", elt.log);
		} 
	}	
}
