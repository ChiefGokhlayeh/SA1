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
import de.hse.licensemanager.dao.ServiceContractDao;
import de.hse.licensemanager.dao.UserDao;
import de.hse.licensemanager.filter.SystemAdminOnly;
import de.hse.licensemanager.filter.CompanyAdminOrAbove;
import de.hse.licensemanager.filter.Login;
import de.hse.licensemanager.model.Company;
import de.hse.licensemanager.model.ServiceContract;
import de.hse.licensemanager.model.User;
import de.hse.licensemanager.model.User.Group;

@Path("/service-contracts")
@Login
public class ServiceContractsResource {

    private final ILoginChecker checker;

    public ServiceContractsResource() {
        this(new LoginChecker());
    }

    public ServiceContractsResource(final ILoginChecker checker) {
        this.checker = checker;
    }

    @GET
    @Login
    @SystemAdminOnly
    @Produces(MediaType.APPLICATION_JSON)
    public List<ServiceContract> all() {
        return ServiceContractDao.getInstance().getServiceContracts();
    }

    @GET
    @Path("by-contractor/{contractor}")
    @Login
    @CompanyAdminOrAbove
    @Produces(MediaType.APPLICATION_JSON)
    public List<ServiceContract> byContractor(@PathParam("contractor") final long id,
            @Context final HttpServletRequest servletRequest, @Context final HttpServletResponse servletResponse)
            throws IOException {

        final User loginUser = checker.getLoginUser(servletRequest);
        final Company company = CompanyDao.getInstance().getCompany(id);

        if (company == null) {
            servletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        } else if (checker.compareGroup(loginUser, Group.SYSTEM_ADMIN) >= 0 || loginUser.getCompany().getId() == id) {
            return ServiceContractDao.getInstance().getServiceContractsByContractor(company);
        } else {
            servletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
    }

    @GET
    @Path("by-user/{user}")
    @Login
    @CompanyAdminOrAbove
    @Produces(MediaType.APPLICATION_JSON)
    public List<ServiceContract> byUser(@PathParam("user") final long id,
            @Context final HttpServletRequest servletRequest, @Context final HttpServletResponse servletResponse)
            throws IOException {

        final User loginUser = checker.getLoginUser(servletRequest);
        final User user = UserDao.getInstance().getUser(id);

        if (user == null) {
            servletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        } else if (checker.compareGroup(loginUser, Group.SYSTEM_ADMIN) >= 0 || loginUser.getCompany().getId() == id) {
            return ServiceContractDao.getInstance().getServiceContractsByUser(user);
        } else {
            servletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
    }

    @GET
    @Path("mine")
    @Login
    @Produces(MediaType.APPLICATION_JSON)
    public List<ServiceContract> mine(@Context final HttpServletRequest servletRequest) throws IOException {

        final User loginUser = checker.getLoginUser(servletRequest);

        return ServiceContractDao.getInstance().getServiceContractsByUser(loginUser);
    }

    @GET
    @Path("count")
    @Login
    @Produces(MediaType.TEXT_PLAIN)
    public String count() {
        final int count = ServiceContractDao.getInstance().getServiceContracts().size();
        return String.valueOf(count);
    }

    @Path("{service-contract}")
    public ServiceContractResource serviceContract(@PathParam("service-contract") final long id,
            @Context final HttpServletRequest servletRequest, @Context final HttpServletResponse servletResponse)
            throws IOException {
        return new ServiceContractResource(id);
    }

    @POST
    @Login
    @SystemAdminOnly
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response newServiceContract(final ServiceContract serviceContract, @Context final UriInfo uriInfo) {
        ServiceContractDao.getInstance().save(serviceContract);
        return Response.created(uriInfo.getRequestUri()).entity(serviceContract).build();
    }
}
