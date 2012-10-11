package nl.nlesc.esalsa.deploy.ws.resources;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.nlesc.esalsa.deploy.ws.entities.Experiment;
import nl.nlesc.esalsa.deploy.ws.entities.Experiments;

@Path("/experiments")
@Produces(MediaType.APPLICATION_JSON)
public class ExperimentResource {
    private Experiments experiments = new Experiments();

    public ExperimentResource() {
        Experiment experiment1 = new Experiment();
        experiment1.setId("experiment1");
        experiment1.setInput("input1");
        experiment1.setConfiguration("configuration1");
        experiment1.setWorker("DAS3");
        experiments.add(experiment1);

        Experiment experiment2 = new Experiment();
        experiment2.setId("experiment2");
        experiment2.setInput("input2");
        experiment2.setConfiguration("configuration2");
        experiment2.setWorker("DAS4");
        experiments.add(experiment2);
    }

    @GET
    public Experiments getExperiments() {
        return experiments;
    }

    @POST
    @Path("/{id}")
    public Response addExperiment(@PathParam("id") String id,
            Experiment experiment) {
        return Response.ok().build();
    }

    @PUT
    @Path("/{id}")
    public Response updateExperiment(@PathParam("id") String id,
            Experiment experiment) {
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response removeExperiment(@PathParam("id") String id) {
        return Response.ok().build();
    }
}
