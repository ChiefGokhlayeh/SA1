package de.hse.licensemanager.resource;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import de.hse.licensemanager.dao.ServiceGroupDao;
import de.hse.licensemanager.filter.SystemAdminOnly;
import de.hse.licensemanager.filter.Login;
import de.hse.licensemanager.model.ServiceGroup;
import de.hse.licensemanager.model.ServiceGroupId;
import de.hse.licensemanager.model.User;

@Path("/service-groups")
public class ServiceGroupsResource {

    @GET
    @Login
    @SystemAdminOnly
    @Produces(MediaType.APPLICATION_JSON)
    public List<ServiceGroup> getServiceGroups() {
        return ServiceGroupDao.getInstance().getServiceGroups();
    }

    @GET
    @Path("count")
    @Login
    @Produces(MediaType.TEXT_PLAIN)
    public String getCount() {
        final int count = ServiceGroupDao.getInstance().getServiceGroups().size();
        return String.valueOf(count);
    }

    @GET
    @Path("mine")
    @Login
    @Produces(MediaType.APPLICATION_JSON)
    public List<ServiceGroup> mine(@Context final HttpServletRequest servletRequest) {
        return ServiceGroupDao.getInstance().getServiceGroupsByUser(
                (User) servletRequest.getSession(false).getAttribute(HttpHeaders.AUTHORIZATION));
    }

    @GET
    @Login
    @Path("by-contractor/{contractor}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ServiceGroup> getServiceGroupsByContractor(@PathParam("contractor") final Long id) {
        return ServiceGroupDao.getInstance().getServiceGroupsByServiceContract(id);
    }

    @GET
    @Login
    @SystemAdminOnly
    @Path("by-user/{user}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ServiceGroup> getContractorByUser(@PathParam("user") final Long id) {
        return ServiceGroupDao.getInstance().getServiceGroupsByUser(id);
    }

    @GET
    @Login
    @SystemAdminOnly
    @Path("by-contractor-and-user/{contractor}:{user}")
    @Produces(MediaType.APPLICATION_JSON)
    public ServiceGroupResource getServiceGroupsByContractorAndUser(
            @PathParam("contractor") final long serviceContractId, @PathParam("user") final long userId) {
        return new ServiceGroupResource(new ServiceGroupId(serviceContractId, userId));
    }
}
