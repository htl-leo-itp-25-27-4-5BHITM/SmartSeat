package at.htl.boundary;

import at.htl.model.User;
import at.htl.repository.UserRepository;
import at.htl.repository.dto.AccountInfoDTO;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.mindrot.jbcrypt.BCrypt;

@Path("/user")
public class UserResource {

    @Inject
    UserRepository repository;

    @Inject
    PasswordHelper helper;

    @POST
    @Path("/login")
    public Response loginUser(AccountInfoDTO data) {

        User user = repository.getUserByName(data.username());

        if (user == null) {
            return Response.status(401).build();
        }

        boolean valid = helper.verifyPassword(data.password(), user.getPassword());

        if (!valid) {
            return Response.status(401).build();
        }

        return Response.ok().build();
    }
}
