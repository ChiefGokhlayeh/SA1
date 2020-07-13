package de.hse.licensemanager.resource;

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

@Login
public class ServiceContractResource {
    @Context
    private final long id;

    public ServiceContractResource(final long id) {
        this.id = id;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() {
        final ServiceContract serviceContract = ServiceContractDao.getInstance().getServiceContract(id);
        if (serviceContract == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(serviceContract).build();
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
