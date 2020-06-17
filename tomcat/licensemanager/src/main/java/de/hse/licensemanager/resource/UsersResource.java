package de.hse.licensemanager.resource;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;

import de.hse.licensemanager.dao.UserDao;
import de.hse.licensemanager.model.User;

@Path("/users")
public class UsersResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    @GET
    @Produces(MediaType.TEXT_XML)
    public List<User> getUsersBrowser() {
        return UserDao.getInstance().getUsers();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public List<User> getUsers() {
        return UserDao.getInstance().getUsers();
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String getCount() {
        final int count = UserDao.getInstance().getUsers().size();
        return String.valueOf(count);
    }

    // @POST
    // @Path("login")
    // @Produces(MediaType.APPLICATION_JSON)
    // @Consumes(MediaType.APPLICATION_JSON)
    // public Map<String, Object> login(final User user, @Context final
    // HttpServletResponse servletResponse) {
    // User checkUser =
    // UserDao.getInstance().getUserByFacebookId(user.getFacebookId());
    // if (checkUser == null) {
    // UserDao.getInstance().save(user);
    // checkUser = UserDao.getInstance().getUserByFacebookId(user.getFacebookId());
    // }
    // final Map<String, Object> response = new HashMap<>();
    // response.put("success", true);
    // response.put("user", checkUser);
    // return response;
    // }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<User> newUser(final User user, @Context final HttpServletResponse servletResponse) throws IOException {
        UserDao.getInstance().save(user);
        return UserDao.getInstance().getUsers();
    }

    @Path("{user}")
    public UserResource getUser(@PathParam("user") final Long id) {
        return new UserResource(uriInfo, request, id);
    }
}
