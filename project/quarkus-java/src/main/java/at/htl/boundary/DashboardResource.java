package at.htl.boundary;

import at.htl.repository.SeatRepository;
import at.htl.repository.dto.SeatRenameDTO;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.beans.ConstructorProperties;

@Path("dashboard")
public class DashboardResource {

    @Inject
    SeatRepository seatRepository;

    @GET
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
}
