package at.htl.boundary;

import at.htl.model.Seat;
import at.htl.repository.SeatRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

public class SeatResource {

    @Inject
    SeatRepository seatRepository;

    @GET
    @Path("getAllSeats")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSeats() {
        List<Seat> seats = seatRepository.getAllSeats();
        if (seats == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok().entity(seats).build();
    }

    @GET
    @Path("getSeatsByFloor/{floor}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSeatsByFloor(@PathParam("floor") String floor) {
        List<Seat> seats = seatRepository.getSeatByFloor(floor);

        if (seats == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(seats).build();
    }

}
