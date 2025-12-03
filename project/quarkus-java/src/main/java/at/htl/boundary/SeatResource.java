package at.htl.boundary;

import at.htl.repository.SeatRepository;
import at.htl.sockets.SeatWebSocket;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/seat")
public class SeatResource {

    @Inject
    SeatRepository seatRepository;

    @Inject
    SeatWebSocket seatWebSocket;

    @GET
    @Path("getAllSeats")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSeats() {
        var seats = seatRepository.getAllSeats();
        if (seats == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok().entity(seats).build();
    }

    @GET
    @Path("getSeatsByFloor/{floor}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSeatsByFloor(@PathParam("floor") String floor) {
        var seats = seatRepository.getSeatByFloor(floor);

        if (seats == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(seats).build();
    }

    @GET
    @Path("getUnoccupiedSeatsByFloor/{floor}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUnoccupiedSeatsByFloor(@PathParam("floor") String floor) {
        var seats = seatRepository.getUnoccupiedByFloor(floor);
        if (seats == -1) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(seats).build();
    }

    @GET
    @Path("changeStatus/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeStatus(@PathParam("id") long id) {
        if (seatRepository.changeStatus(id)) {
            return Response.status(Response.Status.OK).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

}
