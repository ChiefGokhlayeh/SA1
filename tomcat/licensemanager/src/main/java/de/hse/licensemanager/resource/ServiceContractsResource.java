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
import javax.ws.rs.core.UriInfo;

import de.hse.licensemanager.dao.CompanyDao;
import de.hse.licensemanager.dao.ServiceContractDao;
import de.hse.licensemanager.filter.Login;
import de.hse.licensemanager.model.ServiceContract;
import de.hse.licensemanager.model.User;

@Path("/service-contracts")
@Login
public class ServiceContractsResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ServiceContract> getServiceContracts(@Context final HttpServletRequest httpRequest) {
        return ServiceContractDao.getInstance().getServiceContractsOfUser(
                (User) httpRequest.getSession(false).getAttribute(HttpHeaders.AUTHORIZATION));
    }

    @GET
    @Path("of-company/{contractor}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ServiceContract> getServiceContracts(@PathParam("contractor") final long id) {
        return ServiceContractDao.getInstance().getServiceContractsOfCompany(CompanyDao.getInstance().getCompany(id));
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
