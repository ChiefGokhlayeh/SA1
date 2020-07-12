package de.hse.licensemanager.resource;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.hse.licensemanager.dao.ServiceGroupDao;
import de.hse.licensemanager.filter.SystemAdminOnly;
import de.hse.licensemanager.filter.Login;
import de.hse.licensemanager.model.ServiceGroup;
import de.hse.licensemanager.model.ServiceGroupId;

@Login
public class ServiceGroupResource {

    private final ServiceGroupId id;

    public ServiceGroupResource(final ServiceGroupId id) {
        this.id = id;
    }

    public ServiceGroupResource(final long serviceContractId, final long userId) {
        this.id = new ServiceGroupId(serviceContractId, userId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() {
        final ServiceGroup serviceGroup = ServiceGroupDao.getInstance().getServiceGroup(id);
        if (serviceGroup == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(serviceGroup).build();
    }

    @DELETE
    @SystemAdminOnly
    public void delete() {
        ServiceGroupDao.getInstance().delete(id);
    }
}
