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
import javax.ws.rs.core.Response.Status;

import de.hse.licensemanager.dao.IpMappingDao;
import de.hse.licensemanager.dao.UserDao;
import de.hse.licensemanager.filter.CompanyAdminOrAbove;
import de.hse.licensemanager.filter.Login;
import de.hse.licensemanager.model.IpMapping;
import de.hse.licensemanager.model.User;
import de.hse.licensemanager.model.User.Group;

@Login
public class IpMappingResource {
    private final long id;

    private final ILoginChecker checker;

    public IpMappingResource(final long id) {
        this(id, new LoginChecker());
    }

    public IpMappingResource(final long id, final ILoginChecker checker) {
        this.id = id;
        this.checker = checker;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() {
        final IpMapping ipMapping = IpMappingDao.getInstance().getIpMapping(id);
        if (ipMapping == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(ipMapping).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response put(final IpMapping modifiedIpMapping, @Context final UriInfo uriInfo,
            @Context final HttpServletRequest request) {
        final User loginUser = checker.getLoginUser(request);

        final IpMapping ipMapping = IpMappingDao.getInstance().getIpMapping(id);

        if (ipMapping == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else if (checker.compareGroup(loginUser, Group.SYSTEM_ADMIN) >= 0
                || (checker.compareGroup(loginUser, Group.COMPANY_ADMIN) >= 0 && loginUser.getCompany()
                        .equals(ipMapping.getLicense().getServiceContract().getContractor()))) {
            IpMappingDao.getInstance().modify(id, modifiedIpMapping);
            return Response.created(uriInfo.getAbsolutePath()).entity(ipMapping).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @DELETE
    @CompanyAdminOrAbove
    public Response delete(@Context final HttpServletRequest request) {
        final User loginUser = checker.getLoginUser(request);
        final IpMapping ipMapping = IpMappingDao.getInstance().getIpMapping(id);

        if (ipMapping == null) {
            return Response.ok().build();
        } else if (checker.compareGroup(loginUser, Group.SYSTEM_ADMIN) >= 0
                || (checker.compareGroup(loginUser, Group.COMPANY_ADMIN) >= 0 && loginUser.getCompany()
                        .equals(ipMapping.getLicense().getServiceContract().getContractor()))) {
            UserDao.getInstance().delete(id);
            return Response.ok().build();
        } else {
            return Response.status(Status.FORBIDDEN).build();
        }
    }
}
