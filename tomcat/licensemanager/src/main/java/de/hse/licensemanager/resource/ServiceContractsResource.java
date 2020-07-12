package de.hse.licensemanager.resource;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import de.hse.licensemanager.dao.CompanyDao;
import de.hse.licensemanager.dao.ServiceContractDao;
import de.hse.licensemanager.filter.SystemAdminOnly;
import de.hse.licensemanager.filter.Login;
import de.hse.licensemanager.model.ServiceContract;

@Path("/service-contracts")
@Login
public class ServiceContractsResource {

    @GET
    @SystemAdminOnly
    @Produces(MediaType.APPLICATION_JSON)
    public List<ServiceContract> getServiceContracts() {
        return ServiceContractDao.getInstance().getServiceContracts();
    }

    @GET
    @Path("by-company/{contractor}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ServiceContract> getServiceContractsByContractor(@PathParam("contractor") final long id) {
        return ServiceContractDao.getInstance().getServiceContractsByCompany(CompanyDao.getInstance().getCompany(id));
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String getCount() {
        final int count = ServiceContractDao.getInstance().getServiceContracts().size();
        return String.valueOf(count);
    }

    @Path("{service-contract}")
    public ServiceContractResource getServiceContract(@Context final UriInfo uriInfo,
            @PathParam("service-contract") final long id) {
        return new ServiceContractResource(uriInfo, id);
    }
}
