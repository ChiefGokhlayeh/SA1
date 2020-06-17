package de.hse.licensemanager.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import de.hse.licensemanager.dao.UserDao;
import de.hse.licensemanager.model.User;

public class UserResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    Long id;

    public UserResource(final UriInfo uriInfo, final Request request, final Long id) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.id = id;
    }

    // Application integration
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public User getUser() {
        final User user = UserDao.getInstance().getUser(id);
        if (user == null)
            throw new RuntimeException("GET: User with " + id + " not found");
        return user;
    }

    // for the browser
    @GET
    @Produces(MediaType.TEXT_XML)
    public User getUserHTML() {
        final User user = UserDao.getInstance().getUser(id);
        if (user == null)
            throw new RuntimeException("GET: User with " + id + " not found");
        return user;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public Response putUser(final JAXBElement<User> user) {
        final User c = user.getValue();
        return saveAndGetResponse(c);
    }

    @DELETE
    public void deleteUser() {
        UserDao.getInstance().delete(id);
    }

    private Response saveAndGetResponse(final User user) {
        UserDao.getInstance().save(user);
        return Response.created(uriInfo.getAbsolutePath()).build();
    }
}
