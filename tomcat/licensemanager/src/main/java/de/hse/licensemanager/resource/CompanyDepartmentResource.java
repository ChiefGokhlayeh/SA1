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

import de.hse.licensemanager.dao.CompanyDao;
import de.hse.licensemanager.dao.CompanyDepartmentDao;
import de.hse.licensemanager.filter.CompanyAdminOrAbove;
import de.hse.licensemanager.filter.Login;
import de.hse.licensemanager.filter.SystemAdminOnly;
import de.hse.licensemanager.model.CompanyDepartment;
import de.hse.licensemanager.model.User;
import de.hse.licensemanager.model.User.Group;

@Login
public class CompanyDepartmentResource {

    private final long id;
    private final ILoginChecker checker;

    public CompanyDepartmentResource(final CompanyDepartment companyDepartment) {
        this(companyDepartment.getId());
    }

    public CompanyDepartmentResource(final long id) {
        this(id, new LoginChecker());
    }

    public CompanyDepartmentResource(final CompanyDepartment companyDepartment, final ILoginChecker checker) {
        this(companyDepartment.getId(), checker);
    }

    public CompanyDepartmentResource(final long id, final ILoginChecker checker) {
        this.id = id;
        this.checker = checker;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@Context final HttpServletRequest request) {

        final User loginUser = checker.getLoginUser(request);
        final CompanyDepartment companyDepartment = CompanyDepartmentDao.getInstance().getCompanyDepartment(id);

        if (companyDepartment == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else if (checker.compareGroup(loginUser, Group.SYSTEM_ADMIN) >= 0
                || (loginUser.getCompany().equals(companyDepartment.getCompany()))) {
            return Response.ok(companyDepartment).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @PUT
    @CompanyAdminOrAbove
    @Consumes(MediaType.APPLICATION_JSON)
    public Response put(final CompanyDepartment modifiedCompanyDepartment, @Context final UriInfo uriInfo,
            @Context final HttpServletRequest request) {
        final User loginUser = checker.getLoginUser(request);
        final CompanyDepartment companyDepartment = CompanyDepartmentDao.getInstance().getCompanyDepartment(id);

        if (companyDepartment == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else if (checker.compareGroup(loginUser, Group.SYSTEM_ADMIN) >= 0
                || (checker.compareGroup(loginUser, Group.COMPANY_ADMIN) >= 0
                        && loginUser.getCompany().equals(companyDepartment.getCompany()))) {
            CompanyDepartmentDao.getInstance().modify(id, modifiedCompanyDepartment);
            return Response.created(uriInfo.getAbsolutePath()).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @DELETE
    @SystemAdminOnly
    public void delete() {
        CompanyDao.getInstance().delete(id);
    }
}
