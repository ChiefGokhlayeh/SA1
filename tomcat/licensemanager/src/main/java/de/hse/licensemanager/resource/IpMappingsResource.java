package de.hse.licensemanager.resource;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import de.hse.licensemanager.dao.IpMappingDao;
import de.hse.licensemanager.dao.LicenseDao;
import de.hse.licensemanager.filter.SystemAdminOnly;
import de.hse.licensemanager.filter.Login;
import de.hse.licensemanager.model.IpMapping;
import de.hse.licensemanager.model.License;
import de.hse.licensemanager.model.User;
import de.hse.licensemanager.model.User.Group;

@Path("/ip-mappings")
public class IpMappingsResource {

    private final ILoginChecker checker;

    public IpMappingsResource() {
        this(new LoginChecker());
    }

    public IpMappingsResource(final ILoginChecker checker) {
        this.checker = checker;
    }

    @GET
    @SystemAdminOnly
    @Produces(MediaType.APPLICATION_JSON)
    public List<IpMapping> all() {
        return IpMappingDao.getInstance().getIpMappings();
    }

    @GET
    @Path("count")
    @Login
    @Produces(MediaType.TEXT_PLAIN)
    public String count() {
        final int count = IpMappingDao.getInstance().getIpMappings().size();
        return String.valueOf(count);
    }

    @GET
    @Path("by-license/{license}")
    @Login
    @Produces(MediaType.APPLICATION_JSON)
    public List<IpMapping> byLicense(@PathParam("license") final long id,
            @Context final HttpServletRequest servletRequest, @Context final HttpServletResponse servletResponse)
            throws IOException {

        final User loginUser = checker.getLoginUser(servletRequest);
        final License license = LicenseDao.getInstance().getLicense(id);

        if (license == null) {
            servletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        } else if (checker.compareGroup(loginUser, Group.SYSTEM_ADMIN) >= 0
                || (checker.compareGroup(loginUser, Group.COMPANY_ADMIN) >= 0
                        && loginUser.getCompany().equals(license.getServiceContract().getContractor()))
                || loginUser.getServiceContracts().contains(license.getServiceContract())) {
            return IpMappingDao.getInstance().getIpMappingsByLicense(license);
        } else {
            servletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
    }
}
