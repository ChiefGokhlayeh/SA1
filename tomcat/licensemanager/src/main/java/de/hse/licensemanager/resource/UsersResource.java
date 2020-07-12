package de.hse.licensemanager.resource;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;

import de.hse.licensemanager.dao.UserDao;
import de.hse.licensemanager.filter.AdminOnly;
import de.hse.licensemanager.filter.Login;
import de.hse.licensemanager.model.User;

@Path("/users")
public class UsersResource {

    @GET
    @Login
    @AdminOnly
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getUsers() {
        return UserDao.getInstance().getUsers();
    }

    @GET
    @Path("count")
    @Login
    @Produces(MediaType.TEXT_PLAIN)
    public String getCount() {
        final int count = UserDao.getInstance().getUsers().size();
        return String.valueOf(count);
    }

    @POST
    @Login
    @AdminOnly
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<User> newUser(final User user, @Context final HttpServletResponse servletResponse) throws IOException {
        UserDao.getInstance().save(user);
        return UserDao.getInstance().getUsers();
    }

    @Path("{user}")
    @Login
    public UserResource getUser(@Context final UriInfo uriInfo, @PathParam("user") final Long id) {
        return new UserResource(uriInfo, id);
    }

    @GET
    @Path("me")
    @Login
    @Produces(MediaType.APPLICATION_JSON)
    public User me(@Context final HttpServletRequest servletRequest) {
        final HttpSession session = servletRequest.getSession(false);
        return session == null ? null : (User) servletRequest.getSession(false).getAttribute(HttpHeaders.AUTHORIZATION);
    }
}
