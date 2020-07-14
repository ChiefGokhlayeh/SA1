package de.hse.licensemanager.resource;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import de.hse.licensemanager.dao.ServiceGroupDao;
import de.hse.licensemanager.filter.SystemAdminOnly;
import de.hse.licensemanager.filter.Login;
import de.hse.licensemanager.model.ServiceGroup;
import de.hse.licensemanager.model.ServiceGroupId;

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
}
