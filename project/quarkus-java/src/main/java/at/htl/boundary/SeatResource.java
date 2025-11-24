package at.htl.boundary;

import at.htl.model.Seat;
import at.htl.repository.SeatRepository;
import at.htl.repository.dto.SeatInformationDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/seat")
public class SeatResource {

    @Inject
    SeatRepository seatRepository;

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
    public Response getUnoccupiedSeatsByFloor (@PathParam("floor") String floor) {
        var seats = seatRepository.getUnoccupiedByFloor(floor);
        if (seats == -1) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(seats).build();
    }

}
