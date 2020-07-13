package de.hse.licensemanager.resource;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import de.hse.licensemanager.dao.CompanyDao;
import de.hse.licensemanager.dao.ServiceContractDao;
import de.hse.licensemanager.filter.SystemAdminOnly;
import de.hse.licensemanager.filter.CompanyAdminOrAbove;
import de.hse.licensemanager.filter.Login;
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
    @SystemAdminOnly
    @Produces(MediaType.APPLICATION_JSON)
    public List<ServiceContract> getServiceContracts() {
        return ServiceContractDao.getInstance().getServiceContracts();
    }

    @GET
    @Path("by-company/{contractor}")
    @CompanyAdminOrAbove
    @Produces(MediaType.APPLICATION_JSON)
    public List<ServiceContract> getServiceContractsByContractor(@PathParam("contractor") final long id,
            @Context final HttpServletRequest servletRequest, @Context final HttpServletResponse servletResponse)
            throws IOException {

        final User loginUser = checker.getLoginUser(servletRequest);
        if (checker.compareGroup(loginUser, Group.SYSTEM_ADMIN) >= 0 || loginUser.getCompany().getId() == id) {
            return ServiceContractDao.getInstance()
                    .getServiceContractsByCompany(CompanyDao.getInstance().getCompany(id));
        } else {
            servletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String getCount() {
        final int count = ServiceContractDao.getInstance().getServiceContracts().size();
        return String.valueOf(count);
    }

    @Path("{service-contract}")
    public ServiceContractResource getServiceContract(@PathParam("service-contract") final long id,
            @Context final HttpServletRequest servletRequest, @Context final HttpServletResponse servletResponse)
            throws IOException {

        final User loginUser = checker.getLoginUser(servletRequest);
        if (checker.compareGroup(loginUser, Group.SYSTEM_ADMIN) >= 0 || loginUser.getCompany().getId() == id) {
            return new ServiceContractResource(id);
        } else {
            servletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
    }
}
