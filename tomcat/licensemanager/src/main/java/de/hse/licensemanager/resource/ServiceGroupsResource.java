package de.hse.licensemanager.resource;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
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
import javax.ws.rs.core.Response.Status;

import de.hse.licensemanager.dao.ServiceGroupDao;
import de.hse.licensemanager.filter.SystemAdminOnly;
import de.hse.licensemanager.filter.CompanyAdminOrAbove;
import de.hse.licensemanager.filter.Login;
import de.hse.licensemanager.model.ServiceGroup;
import de.hse.licensemanager.model.ServiceGroupId;
import de.hse.licensemanager.model.User;
import de.hse.licensemanager.model.User.Group;

@Path("/service-groups")
public class ServiceGroupsResource {

    private final ILoginChecker checker;

    public ServiceGroupsResource() {
        this(new LoginChecker());
    }

    public ServiceGroupsResource(final ILoginChecker checker) {
        this.checker = checker;
    }

    @GET
    @Login
    @SystemAdminOnly
    @Produces(MediaType.APPLICATION_JSON)
    public List<ServiceGroup> all() {
        return ServiceGroupDao.getInstance().getServiceGroups();
    }

    @GET
    @Path("count")
    @Login
    @Produces(MediaType.TEXT_PLAIN)
    public String count() {
        final int count = ServiceGroupDao.getInstance().getServiceGroups().size();
        return String.valueOf(count);
    }

    @GET
    @Path("mine")
    @Login
    @Produces(MediaType.APPLICATION_JSON)
    public List<ServiceGroup> mine(@Context final HttpServletRequest request) {
        return ServiceGroupDao.getInstance().getServiceGroupsByUser(checker.getLoginUser(request));
    }

    @GET
    @Login
    @Path("by-service-contract/{service-contract}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ServiceGroup> byServiceContract(@PathParam("service-contract") final long id) {
        return ServiceGroupDao.getInstance().getServiceGroupsByServiceContract(id);
    }

    @GET
    @Login
    @SystemAdminOnly
    @Path("by-user/{user}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ServiceGroup> byUser(@PathParam("user") final long id) {
        return ServiceGroupDao.getInstance().getServiceGroupsByUser(id);
    }

    @GET
    @Path("by-service-contract-and-user/{service-contract}:{user}")
    public ServiceGroupResource byServiceContractAndUser(@PathParam("service-contract") final long serviceContractId,
            @PathParam("user") final long userId) {
        return new ServiceGroupResource(new ServiceGroupId(serviceContractId, userId));
    }

    @POST
    @Login
    @CompanyAdminOrAbove
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response newServiceGroup(final ServiceGroup serviceGroup, @Context final UriInfo uriInfo,
            @Context final HttpServletRequest request) {
        final User loginUser = checker.getLoginUser(request);

        if (checker.compareGroup(loginUser, Group.SYSTEM_ADMIN) >= 0
                || (checker.compareGroup(loginUser, Group.COMPANY_ADMIN) >= 0
                        && loginUser.getCompany().equals(serviceGroup.getServiceContract().getContractor()))) {
            ServiceGroupDao.getInstance().save(serviceGroup);
            return Response.created(uriInfo.getRequestUri()).entity(serviceGroup).build();
        } else {
            return Response.status(Status.FORBIDDEN).build();
        }
    }
}
