package at.htl.boundary;


import at.htl.model.ScanHistory;
import at.htl.model.Seat;
import at.htl.repository.Repository;
import at.htl.repository.ScanHistoryRepo;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Path("")
public class Resource {

//    @Inject
//    Repository repository;


    @Inject
    ScanHistoryRepo repository;

    @GET
    @Path("getAllEntries")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        List<ScanHistory> entryList = repository.getAllEntries();
        if (entryList == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(entryList).build();
    }

//    @GET
//    @Path("{id:\\d+}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getSeatById(@PathParam("id") long id) {
//        Seat seat = repository.getSeatById(id);
//        if (seat == null) {
//            return Response.status(Response.Status.NOT_FOUND).build();
//        }
//        return Response.status(Response.Status.OK).entity(seat).build();
//    }

//    @POST
//    @Path("addSeat")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response addSeat(Seat seat) {
//        if (!repository.addSeat(seat)) {
//            return Response.status(Response.Status.BAD_REQUEST).build();
//        }
//        return Response.status(Response.Status.OK).build();
//    }


    @GET
    @Path("addEntry/{id:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSeat(@PathParam("id") long id) {
        if (!repository.addEntry(id)) {
            return Response.seeOther(URI.create("http://10.152.213.15:8080/error.html"))
                    .build();
        }
        return Response.seeOther(URI.create("http://10.152.213.15:8080"))
                //.status(Response.Status.OK)
                .build();

    }


}
