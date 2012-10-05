package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import nl.esciencecenter.esalsa.deploy.ExperimentInfo;

public class RunningExperimentViewer extends ExperimentViewer implements ActionListener {

	private static final long serialVersionUID = -8580838957929000835L;
	
	public RunningExperimentViewer(RemoteStore<ExperimentInfo> store) { 
	
		super(store);

		JPanel buttonPanel = new JPanel();
	    buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	    add(buttonPanel, BorderLayout.SOUTH);
		
	    JButton stop = new JButton("Stop");
	    stop.setActionCommand("Stop");;
	    stop.addActionListener(this);
	    
		buttonPanel.add(stop);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
