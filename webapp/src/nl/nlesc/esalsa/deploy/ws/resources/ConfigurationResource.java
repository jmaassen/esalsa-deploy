package nl.nlesc.esalsa.deploy.ws.resources;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.nlesc.esalsa.deploy.ws.entities.Configuration;
import nl.nlesc.esalsa.deploy.ws.entities.Configurations;

@Path("/configurations")
@Produces(MediaType.APPLICATION_JSON)
public class ConfigurationResource {
	private Configurations configurations = new Configurations();

	public ConfigurationResource() {
		Configuration configuration1 = new Configuration();
		configuration1.setId("configuration1");
		configuration1.setConfiguration("My config1");
		configurations.add(configuration1);

		Configuration configuration2 = new Configuration();
		configuration2.setId("configuration2");
		configuration2.setConfiguration("My config2");
		configurations.add(configuration2);
	}

	@GET
	public Configurations getConfigurations() {
		return configurations;
	}

	@PUT
	@Path("/{id}")
	public Response updateConfiguration(@PathParam("id") String id,
			Configuration configuration) {
		return Response.ok().build();
	}

	@DELETE
	@Path("/{id}")
	public Response removeConfiguration(@PathParam("id") String id) {
		return Response.ok().build();
	}
}
