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
    @Transactional
    @Path("rename")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response renameSeat(SeatRenameDTO seatRenameDTO) {
        var seats = seatRepository.renameSeat(seatRenameDTO);
        if (!seats.isEmpty()) {
            return Response.status(Response.Status.OK).entity(seats).build();
        }

        return Response.status(Response.Status.NOT_FOUND).entity(seats).build();
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
    public Response getAllHistoriesAVG() {
        var result = seatRepository.getAVGHistories();
        if (result != null) {
            return Response.status(Response.Status.OK).entity(result).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }


    @GET
    @Path("history/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHistoryAVGByID(@PathParam("id") long id) {
        var result = seatRepository.getHistoryAVG(id);
        if (result != null) {
            return Response.status(Response.Status.OK).entity(result).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

}
