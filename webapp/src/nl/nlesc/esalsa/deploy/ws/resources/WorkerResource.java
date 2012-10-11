package nl.nlesc.esalsa.deploy.ws.resources;

import java.util.HashMap;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.nlesc.esalsa.deploy.ws.entities.Worker;
import nl.nlesc.esalsa.deploy.ws.entities.Workers;

@Path("/workers")
@Produces(MediaType.APPLICATION_JSON)
public class WorkerResource {
    private Workers workers;

    public WorkerResource() {
        workers = new Workers();

        Worker worker = new Worker();
        worker.setId("DAS4");
        worker.setUri("My uri");
        worker.setTemplate_dir("My template dir");
        worker.setInput_dir("My input dir");
        worker.setOutput_dir("My output dir");
        HashMap<String, String> add_props = new HashMap<String, String>();
        add_props.put("nprocs_tropic", "16");
        worker.setAdd_props(add_props);
        workers.add(worker);

        Worker worker2 = new Worker();
        worker2.setId("DAS3");
        worker2.setUri("My uri");
        worker2.setTemplate_dir("My template dir");
        worker2.setInput_dir("My input dir");
        worker2.setOutput_dir("My output dir");
        HashMap<String, String> add_props2 = new HashMap<String, String>();
        add_props2.put("nprocs_tropic", "8");
        add_props2.put("mem", "123");
        worker2.setAdd_props(add_props2);
        workers.add(worker2);
    }

    @GET
    public Workers getWorkers() {
        return workers;
    }

    @PUT
    @Path("/{id}")
    public Response updateWorker(@PathParam("id") String id, Worker worker) {
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response removeWorker(@PathParam("id") String id) {
        return Response.ok().build();
    }
}
