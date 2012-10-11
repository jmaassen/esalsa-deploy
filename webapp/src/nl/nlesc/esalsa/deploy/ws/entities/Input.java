package nl.nlesc.esalsa.deploy.ws.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Input {
    private String id;
    private String comment;
    private List<String> files;

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
	public List<String> getFiles() {
		return files;
	}
	public void setFiles(List<String> files) {
		this.files = files;
	}
}
