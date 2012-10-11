package nl.nlesc.esalsa.deploy.ws.entities;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Configurations {
    @XmlElement(name="rows")
    private List<Configuration> configurations;
    @XmlElement
    private int total = 0;

    public Configurations() {
    	configurations = new ArrayList<Configuration>();
    }

    public void add(Configuration configuration) {
    	configurations.add(configuration);
        total++;
    }
}
