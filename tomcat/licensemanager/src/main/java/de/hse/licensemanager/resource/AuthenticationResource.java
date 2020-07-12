package de.hse.licensemanager.resource;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.hse.licensemanager.dao.CredentialsDao;
import de.hse.licensemanager.model.Credentials;
import de.hse.licensemanager.model.PlainCredentials;

@Path("/auth")
public class AuthenticationResource {
    @PUT
    @Path("change")
    public Response change(final PlainCredentials plainCredentials, @Context final HttpServletRequest servletRequest,
            @Context final UriInfo uriInfo) {
        final Credentials originalCredentials = CredentialsDao.getInstance()
                .getCredentialsByLoginname(plainCredentials.getLoginname());
        if (originalCredentials != null) {
            CredentialsDao.getInstance().refresh(originalCredentials);
            if (plainCredentials.verify(originalCredentials)) {
                final Credentials modifiedCredentials = new Credentials(plainCredentials.getLoginname(),
                        plainCredentials.getNewPassword());
                CredentialsDao.getInstance().modify(originalCredentials.getId(), modifiedCredentials);
                return Response.created(uriInfo.getAbsolutePath()).build();
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
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
        if (checkCredentials != null) {
            CredentialsDao.getInstance().refresh(checkCredentials);
            if (credentials.verify(checkCredentials)) {
                response.put("success", true);
                response.put("user", checkCredentials.getUser());
                servletRequest.getSession(true).setAttribute(HttpHeaders.AUTHORIZATION, checkCredentials.getUser());
                addSameSiteCookieAttribute(servletResponse);
                return response;
            }
        }
        response.put("success", false);
        servletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
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
}
