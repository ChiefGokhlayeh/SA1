package de.hse.licensemanager.resource;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
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

    private final ILoginChecker checker;

    public CompaniesResource() {
        this(new LoginChecker());
    }

    public CompaniesResource(final ILoginChecker checker) {
        this.checker = checker;
    }

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
    public CompanyResource mine(@Context final HttpServletRequest request, @Context final HttpServletResponse response)
            throws IOException {
        final User loginUser = checker.getLoginUser(request, response);

        if (loginUser != null) {
            return new CompanyResource(loginUser.getCompany());
        } else {
            return null;
        }
    }

    @Path("by-user/{user}")
    public CompanyResource byUser(@PathParam("user") final long id, @Context final HttpServletResponse servletResponse)
            throws IOException {
        final User user = UserDao.getInstance().getUser(id);

        if (user != null) {
            return new CompanyResource(user.getCompany());
        } else {
            servletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
    }

    @POST
    @Login
    @SystemAdminOnly
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response newCompany(final Company company, @Context final UriInfo uriInfo) {
        CompanyDao.getInstance().save(company);
        return Response.created(uriInfo.getRequestUri()).entity(company).build();
    }

    @Path("{company}")
    public CompanyResource getCompany(@PathParam("company") final long id) {
        return new CompanyResource(id);
    }
}
