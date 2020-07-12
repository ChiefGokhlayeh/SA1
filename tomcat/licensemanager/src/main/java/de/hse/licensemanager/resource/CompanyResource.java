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

import de.hse.licensemanager.dao.CompanyDao;
import de.hse.licensemanager.filter.SystemAdminOnly;
import de.hse.licensemanager.filter.Login;
import de.hse.licensemanager.model.Company;

@Login
public class CompanyResource {

    private final long id;

    public CompanyResource(final Company company) {
        this(company.getId());
    }

    public CompanyResource(final long id) {
        this.id = id;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() {
        final Company company = CompanyDao.getInstance().getCompany(id);
        if (company == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(company).build();
    }

    @PUT
    @SystemAdminOnly
    @Consumes(MediaType.APPLICATION_JSON)
    public Response put(final Company modifiedCompany, @Context UriInfo uriInfo) {
        try {
            CompanyDao.getInstance().modify(id, modifiedCompany);
        } catch (final IllegalArgumentException ex) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.created(uriInfo.getAbsolutePath()).build();
    }

    @DELETE
    @SystemAdminOnly
    public void delete() {
        CompanyDao.getInstance().delete(id);
    }
}
