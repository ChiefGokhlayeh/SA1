package de.hse.licensemanager.resource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.hse.licensemanager.dao.ServiceContractDao;
import de.hse.licensemanager.filter.SystemAdminOnly;
import de.hse.licensemanager.filter.Login;
import de.hse.licensemanager.model.ServiceContract;
import de.hse.licensemanager.model.User;
import de.hse.licensemanager.model.User.Group;

@Login
public class ServiceContractResource {
    private final long id;

    private final ILoginChecker checker;

    public ServiceContractResource(final long id) {
        this(id, new LoginChecker());
    }

    public ServiceContractResource(final long id, final ILoginChecker checker) {
        this.id = id;
        this.checker = checker;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@Context HttpServletRequest request) {

        final User loginUser = checker.getLoginUser(request);
        final ServiceContract serviceContract = ServiceContractDao.getInstance().getServiceContract(id);

        if (serviceContract == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else if (checker.compareGroup(loginUser, Group.SYSTEM_ADMIN) >= 0
                || (checker.compareGroup(loginUser, Group.COMPANY_ADMIN) >= 0
                        && loginUser.getCompany().equals(serviceContract.getContractor()))
                || loginUser.getServiceContracts().stream().anyMatch((sc) -> sc.equals(serviceContract))) {
            return Response.ok(serviceContract).build();
        } else {
            Response.status(Response.Status.FORBIDDEN).build();
            return null;
        }
    }

    @DELETE
    @SystemAdminOnly
    public void delete() {
        ServiceContractDao.getInstance().delete(id);
    }

    @PUT
    @SystemAdminOnly
    @Consumes(MediaType.APPLICATION_JSON)
    public Response put(final ServiceContract serviceContract, @Context final UriInfo uriInfo) {
        ServiceContractDao.getInstance().save(serviceContract);
        return Response.created(uriInfo.getAbsolutePath()).build();
    }
}
