package at.htl.boundary;


import at.htl.model.Seat;
import at.htl.repository.Repository;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

@Path("")
public class Resource {

    @Inject
    Repository repository;

    @GET
    @Path("Seats")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        List<Seat> seatList = repository.getAll();
        if (seatList == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(seatList).build();
    }
}
