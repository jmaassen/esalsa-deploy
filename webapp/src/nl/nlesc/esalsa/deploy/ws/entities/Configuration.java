package nl.nlesc.esalsa.deploy.ws.entities;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Configuration {
	private String id;
	private String comment;
	private String configuration;

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

	public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}
}
