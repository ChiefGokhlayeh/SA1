package de.hse.licensemanager.resource;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;

import de.hse.licensemanager.dao.CredentialsDao;
import de.hse.licensemanager.dao.UserDao;
import de.hse.licensemanager.filter.AdminOnly;
import de.hse.licensemanager.filter.Login;
import de.hse.licensemanager.model.Credentials;
import de.hse.licensemanager.model.PlainCredentials;
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
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Map<String, Object> login(final PlainCredentials credentials,
            @Context final HttpServletRequest servletRequest, @Context final HttpServletResponse servletResponse)
            throws IOException {
        final Credentials checkCredentials = CredentialsDao.getInstance()
                .getCredentialsByLoginname(credentials.getLoginname());
        final Map<String, Object> response = new HashMap<>();
        if (checkCredentials != null && credentials.verify(checkCredentials)) {
            response.put("success", true);
            response.put("user", checkCredentials.getUser());
            servletRequest.getSession(true).setAttribute(HttpHeaders.AUTHORIZATION, checkCredentials.getUser());
            addSameSiteCookieAttribute(servletResponse);
        } else {
            servletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
        return response;
    }

    @POST /*
           * POST causes issues with tomcat v9: Access-Control-Origin header is not
           * appended when using this method. However, according to
           * https://tomcat.apache.org/tomcat-9.0-doc/images/cors-flowchart.png it should.
           */
    @Path("logout")
    public Response logoutPost(@Context final HttpServletRequest servletRequest) {
        return logout(servletRequest);
    }

    @GET
    @Path("logout")
    public Response logoutGet(@Context final HttpServletRequest servletRequest) {
        return logout(servletRequest);
    }

    private Response logout(final HttpServletRequest servletRequest) {
        final HttpSession session = servletRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return Response.status(Response.Status.NO_CONTENT).build();

    }

    private void addSameSiteCookieAttribute(final HttpServletResponse response) {
        final Collection<String> headers = response.getHeaders(HttpHeaders.SET_COOKIE);
        boolean firstHeader = true;
        for (final String header : headers) { // there can be multiple Set-Cookie attributes
            if (firstHeader) {
                response.setHeader(HttpHeaders.SET_COOKIE, String.format("%s; %s", header, "SameSite=None"));
                firstHeader = false;
                continue;
            }
            response.addHeader(HttpHeaders.SET_COOKIE, String.format("%s; %s", header, "SameSite=None"));
        }
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
}
