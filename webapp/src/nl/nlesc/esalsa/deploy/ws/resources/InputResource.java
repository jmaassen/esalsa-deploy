package nl.nlesc.esalsa.deploy.ws.resources;

import java.util.ArrayList;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.nlesc.esalsa.deploy.ws.entities.Input;
import nl.nlesc.esalsa.deploy.ws.entities.Inputs;

@Path("/inputs")
@Produces(MediaType.APPLICATION_JSON)
public class InputResource {
	private Inputs inputs = new Inputs();

	public InputResource() {
		Input input1 = new Input();
		input1.setId("input1");
		ArrayList<String> files1 = new ArrayList<String>();
		files1.add("file1");
		files1.add("file2");
		input1.setFiles(files1);
		inputs.add(input1);

		Input input2 = new Input();
		input2.setId("input2");
		ArrayList<String> files2 = new ArrayList<String>();
		files2.add("file3");
		files2.add("file4");
		input2.setFiles(files2);
		inputs.add(input2);
	}

	@GET
	public Inputs getInputs() {
		return inputs;
	}

	@PUT
	@Path("/{id}")
	public Response updateInput(@PathParam("id") String id, Input input) {
		return Response.ok().build();
	}

	@DELETE
	@Path("/{id}")
	public Response removeInput(@PathParam("id") String id) {
		return Response.ok().build();
	}
}
