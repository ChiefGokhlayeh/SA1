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

import de.hse.licensemanager.dao.LicenseDao;
import de.hse.licensemanager.dao.ServiceContractDao;
import de.hse.licensemanager.dao.UserDao;
import de.hse.licensemanager.filter.SystemAdminOnly;
import de.hse.licensemanager.filter.Login;
import de.hse.licensemanager.model.License;
import de.hse.licensemanager.model.ServiceContract;
import de.hse.licensemanager.model.User;
import de.hse.licensemanager.model.User.Group;

@Path("/licenses")
public class LicensesResource {

    private final ILoginChecker checker;

    public LicensesResource() {
        this(new LoginChecker());
    }

    public LicensesResource(final ILoginChecker checker) {
        this.checker = checker;
    }

    @GET
    @SystemAdminOnly
    @Produces(MediaType.APPLICATION_JSON)
    public List<License> getLicenses() {
        return LicenseDao.getInstance().getLicenses();
    }

    @GET
    @Path("count")
    @Login
    @Produces(MediaType.TEXT_PLAIN)
    public String getCount() {
        final int count = LicenseDao.getInstance().getLicenses().size();
        return String.valueOf(count);
    }

    @GET
    @Path("by-service-contract/{service-contract}")
    @Login
    @Produces(MediaType.APPLICATION_JSON)
    public List<License> getLicensesByServiceContract(@PathParam("service-contract") final long id,
            @Context final HttpServletRequest servletRequest, @Context final HttpServletResponse servletResponse)
            throws IOException {

        final User loginUser = checker.getLoginUser(servletRequest);
        final ServiceContract serviceContract = ServiceContractDao.getInstance().getServiceContract(id);

        if (serviceContract == null) {
            servletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        } else if (checker.compareGroup(loginUser, Group.SYSTEM_ADMIN) >= 0
                || loginUser.getServiceContracts().contains(serviceContract)) {
            return LicenseDao.getInstance().getLicensesByServiceContract(serviceContract);
        } else {
            servletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
    }

    @GET
    @Path("by-user/{user}")
    @Login
    @Produces(MediaType.APPLICATION_JSON)
    public List<License> getLicensesByUser(@PathParam("user") final long id,
            @Context final HttpServletRequest servletRequest, @Context final HttpServletResponse servletResponse)
            throws IOException {

        final User loginUser = checker.getLoginUser(servletRequest);
        final User user = UserDao.getInstance().getUser(id);

        if (user == null) {
            servletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        } else if (checker.compareGroup(loginUser, Group.SYSTEM_ADMIN) >= 0
                || (checker.compareGroup(loginUser, Group.COMPANY_ADMIN) >= 0
                        && loginUser.getCompany().equals(user.getCompany()))
                || loginUser.equals(user)) {
            return LicenseDao.getInstance().getLicensesByUser(user);
        } else {
            servletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
    }

    @GET
    @Path("mine")
    @Login
    @Produces(MediaType.APPLICATION_JSON)
    public List<License> mine(@Context final HttpServletRequest servletRequest) throws IOException {

        final User loginUser = checker.getLoginUser(servletRequest);

        return LicenseDao.getInstance().getLicensesByUser(loginUser);
    }

    @POST
    @Login
    @SystemAdminOnly
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response newLicense(final License license, @Context final UriInfo uriInfo) {
        LicenseDao.getInstance().save(license);
        return Response.created(uriInfo.getRequestUri()).entity(license).build();
    }
}
