package de.hse.licensemanager.resource;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
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
import de.hse.licensemanager.filter.AdminOnly;
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

    @GET
    @Path("mine")
    @Login
    @Produces(MediaType.APPLICATION_JSON)
    public Company mine(@Context final HttpServletRequest servletRequest) {
        final HttpSession session = servletRequest.getSession(false);
        return session == null ? null
                : ((User) servletRequest.getSession(false).getAttribute(HttpHeaders.AUTHORIZATION)).getCompany();
    }

    @POST
    @Login
    @AdminOnly
    @Produces(MediaType.APPLICATION_JSON)
    public Response newCompany(final Company company, @Context final UriInfo uriInfo) {
        CompanyDao.getInstance().save(company);
        return Response.created(null).entity(CompanyDao.getInstance().getCompany(company.getId())).build();
    }

    @Path("{company}")
    @Login
    public CompanyResource getCompany(@Context final UriInfo uriInfo, @PathParam("company") final Long id) {
        return new CompanyResource(uriInfo, id);
    }
}
