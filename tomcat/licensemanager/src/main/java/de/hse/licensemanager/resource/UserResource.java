package de.hse.licensemanager.resource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.hse.licensemanager.dao.UserDao;
import de.hse.licensemanager.filter.AdminOnly;
import de.hse.licensemanager.filter.Login;
import de.hse.licensemanager.model.User;

@Login
public class UserResource {
    @Context
    private final UriInfo uriInfo;

    private final long id;

    public UserResource(final UriInfo uriInfo, final long id) {
        this.uriInfo = uriInfo;
        this.id = id;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public User getUser() {
        final User user = UserDao.getInstance().getUser(id);
        if (user == null)
            throw new RuntimeException("GET: User with " + id + " not found");
        return user;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putUser(final JAXBElement<User> user) {
        final User c = user.getValue();
        return saveAndGetResponse(c);
    }

    @DELETE
    @AdminOnly
    public void deleteUser() {
        UserDao.getInstance().delete(id);
    }

    private Response saveAndGetResponse(final User user) {
        UserDao.getInstance().save(user);
        return Response.created(uriInfo.getAbsolutePath()).build();
    }
}
