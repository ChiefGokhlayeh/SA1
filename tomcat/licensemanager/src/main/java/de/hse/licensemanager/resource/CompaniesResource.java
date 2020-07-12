package de.hse.licensemanager.resource;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.hse.licensemanager.dao.CompanyDao;
import de.hse.licensemanager.dao.UserDao;
import de.hse.licensemanager.filter.SystemAdminOnly;
import de.hse.licensemanager.filter.Login;
import de.hse.licensemanager.model.Company;
import de.hse.licensemanager.model.User;

@Path("/companies")
public class CompaniesResource {

    @GET
    @Login
    @Produces(MediaType.APPLICATION_JSON)
    public List<Company> getCompanies() {
        return CompanyDao.getInstance().getCompanies();
    }

    @GET
    @Path("count")
    @Login
    @Produces(MediaType.TEXT_PLAIN)
    public String getCount() {
        final int count = CompanyDao.getInstance().getCompanies().size();
        return String.valueOf(count);
    }

    @Path("mine")
    @Login
    public CompanyResource mine(@Context final HttpServletRequest servletRequest) throws IOException {
        final HttpSession session = servletRequest.getSession(false);
        if (session == null) {
            throw new IllegalStateException("Login-Filter protected REST API called without login.");
        } else {
            return new CompanyResource(((User) session.getAttribute(HttpHeaders.AUTHORIZATION)).getCompany());
        }
    }

    @Path("by-user/{user}")
    @Login
    public CompanyResource byUser(@PathParam("user") final long id, @Context final HttpServletResponse servletResponse)
            throws IOException {
        final User user = UserDao.getInstance().getUser(id);

        if (user == null) {
            servletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        } else {
            return new CompanyResource(user.getCompany());
        }
    }

    @POST
    @Login
    @SystemAdminOnly
    @Produces(MediaType.APPLICATION_JSON)
    public Response newCompany(final Company company, @Context final UriInfo uriInfo) {
        CompanyDao.getInstance().save(company);
        return Response.created(null).entity(CompanyDao.getInstance().getCompany(company.getId())).build();
    }

    @Path("{company}")
    @Login
    public CompanyResource getCompany(@PathParam("company") final long id) {
        return new CompanyResource(id);
    }
}
