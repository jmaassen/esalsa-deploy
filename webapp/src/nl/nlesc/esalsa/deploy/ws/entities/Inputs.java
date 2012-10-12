package nl.nlesc.esalsa.deploy.ws.entities;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Inputs {
    @XmlElement(name="rows")
    private List<Input> inputs;
    @XmlElement
    private int total = 0;

    public Inputs() {
    	inputs = new ArrayList<Input>();
    }

    public void add(Input input) {
    	inputs.add(input);
        total++;
    }
}
