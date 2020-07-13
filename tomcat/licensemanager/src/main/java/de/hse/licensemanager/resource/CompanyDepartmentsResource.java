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
import javax.ws.rs.core.Response;

import de.hse.licensemanager.dao.CompanyDao;
import de.hse.licensemanager.dao.CompanyDepartmentDao;
import de.hse.licensemanager.filter.Login;
import de.hse.licensemanager.filter.SystemAdminOnly;
import de.hse.licensemanager.model.Company;
import de.hse.licensemanager.model.CompanyDepartment;
import de.hse.licensemanager.model.User;
import de.hse.licensemanager.model.User.Group;

@Path("/company-departments")
public class CompanyDepartmentsResource {

    private final ILoginChecker checker;

    public CompanyDepartmentsResource() {
        this(new LoginChecker());
    }

    public CompanyDepartmentsResource(final ILoginChecker checker) {
        this.checker = checker;
    }

    @GET
    @Login
    @SystemAdminOnly
    @Produces(MediaType.APPLICATION_JSON)
    public List<CompanyDepartment> getCompanies() {
        return CompanyDepartmentDao.getInstance().getCompanyDepartments();
    }

    @GET
    @Path("count")
    @Login
    @Produces(MediaType.TEXT_PLAIN)
    public String getCount() {
        final int count = CompanyDepartmentDao.getInstance().getCompanyDepartments().size();
        return String.valueOf(count);
    }

    @Path("mine")
    public CompanyDepartmentResource mine(@Context final HttpServletRequest request,
            @Context final HttpServletResponse response) throws IOException {
        final User loginUser = checker.getLoginUser(request, response);

        if (loginUser != null) {
            return new CompanyDepartmentResource(loginUser.getCompanyDepartment());
        } else {
            return null;
        }
    }

    @GET
    @Path("by-company/{company}")
    @Login
    @SystemAdminOnly
    @Produces(MediaType.APPLICATION_JSON)
    public Response byCompany(@PathParam("company") final long id, @Context final HttpServletRequest request) {
        final User loginUser = checker.getLoginUser(request);
        final Company company = CompanyDao.getInstance().getCompany(id);

        if (company != null) {
            if (checker.compareGroup(loginUser, Group.SYSTEM_ADMIN) >= 0 || loginUser.getCompany().equals(company)) {
                final List<CompanyDepartment> companyDepartments = CompanyDepartmentDao.getInstance()
                        .getCompanyDepartmentsByCompany(id);
                return Response.ok(companyDepartments).build();
            } else {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
