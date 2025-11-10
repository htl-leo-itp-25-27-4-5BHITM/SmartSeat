package at.htl.boundary;


import at.htl.model.Seat;
import at.htl.repository.Repository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

@Path("")
public class Resource {

    @Inject
    Repository repository;

    @GET
    @Path("getAllSeats")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        List<Seat> seatList = repository.getAll();
        if (seatList == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(seatList).build();
    }

    @GET
    @Path("{filter:[a-zA-Z0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSeatById() {
        Seat seat;
        if (seat == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(seat).build();
    }

    @POST
    @Path("addSeat")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSeat(Seat seat) {
        if (!addSeat(seat)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.OK).build();
    }


}
