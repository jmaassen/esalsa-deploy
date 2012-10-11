package nl.nlesc.esalsa.deploy.ws.entities;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Experiments {
    @XmlElement(name="rows")
    private List<Experiment> experiments;
    @XmlElement
    private int total = 0;

    public Experiments() {
    	experiments = new ArrayList<Experiment>();
    }

    public void add(Experiment experiment) {
    	experiments.add(experiment);
        total++;
    }
}
