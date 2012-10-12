package nl.nlesc.esalsa.deploy.ws.entities;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Experiment {
	private String id;
	private String comment;
	private String worker;
	private String configuration;
	private String input;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getWorker() {
		return worker;
	}

	public void setWorker(String worker) {
		this.worker = worker;
	}

	public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}
}
