package nl.nlesc.esalsa.deploy.ws.entities;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Worker {
    private String id;
    private String comment;
    private String uri;
    private String template_dir;
    private String input_dir;
    private String output_dir;
    private HashMap<String, String> add_props;
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
    public String getUri() {
        return uri;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }
    public String getTemplate_dir() {
        return template_dir;
    }
    public void setTemplate_dir(String template_dir) {
        this.template_dir = template_dir;
    }
    public String getInput_dir() {
        return input_dir;
    }
    public void setInput_dir(String input_dir) {
        this.input_dir = input_dir;
    }
    public String getOutput_dir() {
        return output_dir;
    }
    public void setOutput_dir(String output_dir) {
        this.output_dir = output_dir;
    }
    public HashMap<String, String> getAdd_props() {
        return add_props;
    }
    public void setAdd_props(HashMap<String, String> add_props) {
        this.add_props = add_props;
    }

}

