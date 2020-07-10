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
    public Response get() {
        final User user = UserDao.getInstance().getUser(id);
        if (user == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(user).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response put(final User modifiedUser, @Context final HttpServletRequest httpServletRequest) {
        final User loginUser = (User) httpServletRequest.getSession(false).getAttribute(HttpHeaders.AUTHORIZATION);

        if ((loginUser.getId() == id || loginUser.getSystemGroup().getDisplayName().equals("admin"))
                && loginUser.isActive()) {
            UserDao.getInstance().save(modifiedUser);
            return Response.created(uriInfo.getAbsolutePath()).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @DELETE
    @AdminOnly
    public void delete() {
        UserDao.getInstance().delete(id);
    }
}
