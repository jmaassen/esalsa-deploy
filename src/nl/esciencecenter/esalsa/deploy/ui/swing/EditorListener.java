package nl.esciencecenter.esalsa.deploy.ui.swing;

import nl.esciencecenter.esalsa.deploy.parser.ParseException;

public interface EditorListener {
	void startedEditing();
	void stoppedEditing();
	void parse(String text) throws ParseException;
}
