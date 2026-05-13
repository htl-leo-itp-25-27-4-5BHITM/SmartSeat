package at.htl.boundary;

import at.htl.repository.SeatRepository;
import at.htl.repository.dto.SeatRenameDTO;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.beans.ConstructorProperties;

@Path("dashboard")
public class DashboardResource {

    @Inject
    SeatRepository seatRepository;

    @POST
    @Path("rename")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response renameSeat(SeatRenameDTO dto) {

        var seats = seatRepository.renameSeat(dto);

        if (seats.isEmpty()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Name existiert bereits")
                    .build();
        }

        return Response.ok(seats).build();
    }

    @GET
    @Path("duration")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDuration() {
        return Response.status(Response.Status.OK).entity(seatRepository.getDuration()).build();
    }

    @PUT
    @Transactional
    @Path("duration/{seconds}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeDuration(@PathParam("seconds") int seconds) {

        if (seconds > 10) {
            if (seatRepository.changeDuration(seconds)) {
                return Response.status(Response.Status.OK).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("histories")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAverageWaitingTimesBySeat() {
        return Response.ok(seatRepository.getAverageWaitingTimesBySeat()).build();
    }

    @GET
    @Path("history/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHistoryAVGByID(@PathParam("id") long id) {

        var result = seatRepository.getAverageWaitingTimesForId(id);

        if (result == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(result).build();
    }
}
