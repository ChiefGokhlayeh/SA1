package de.hse.licensemanager.resource;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;

import de.hse.licensemanager.dao.CompanyDepartmentDao;
import de.hse.licensemanager.dao.UserDao;
import de.hse.licensemanager.filter.SystemAdminOnly;
import de.hse.licensemanager.filter.Login;
import de.hse.licensemanager.model.CompanyDepartment;
import de.hse.licensemanager.model.User;
import de.hse.licensemanager.model.User.Group;

@Path("/users")
public class UsersResource {

    private final ILoginChecker checker;

    public UsersResource() {
        this(new LoginChecker());
    }

    public UsersResource(final ILoginChecker checker) {
        this.checker = checker;
    }

    @GET
    @Login
    @SystemAdminOnly
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> all() {
        return UserDao.getInstance().getUsers();
    }

    @GET
    @Path("count")
    @Login
    @Produces(MediaType.TEXT_PLAIN)
    public String count() {
        final int count = UserDao.getInstance().getUsers().size();
        return String.valueOf(count);
    }

    @GET
    @Path("group-types")
    @Login
    @Produces(MediaType.APPLICATION_JSON)
    public Group[] groupTypes() {
        return Group.values();
    }

    @POST
    @Login
    @SystemAdminOnly
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newUser(final User user, @Context final UriInfo uriInfo) throws IOException {
        UserDao.getInstance().save(user);
        return Response.created(uriInfo.getRequestUri()).entity(user).build();
    }

    @Path("{user}")
    public UserResource user(@PathParam("user") final Long id) {
        return new UserResource(id);
    }

    @Path("me")
    public UserResource me(@Context final HttpServletRequest servletRequest,
            @Context final HttpServletResponse servletResponse) throws IOException {
        final User loginUser = checker.getLoginUser(servletRequest, servletResponse);
        if (loginUser != null)
            return new UserResource(loginUser);
        else
            return null;
    }

    @GET
    @Path("by-company/{company}")
    @Login
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> byCompany(@PathParam("company") final long id, @Context final HttpServletRequest servletRequest,
            @Context final HttpServletResponse servletResponse) throws IOException {
        final User loginUser = checker.getLoginUser(servletRequest);

        if (loginUser.getGroup().equals(Group.SYSTEM_ADMIN) || loginUser.getCompany().getId() == id) {
            return UserDao.getInstance().getUsersByCompany(id);
        } else {
            servletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
    }

    @GET
    @Path("by-company-department/{company-department}")
    @Login
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> byCompanyDepartment(@PathParam("company-department") final long id,
            @Context final HttpServletRequest servletRequest, @Context final HttpServletResponse servletResponse)
            throws IOException {
        final User loginUser = checker.getLoginUser(servletRequest);
        final CompanyDepartment companyDepartment = CompanyDepartmentDao.getInstance().getCompanyDepartment(id);

        if (companyDepartment == null) {
            servletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        } else if (loginUser.getGroup().equals(Group.SYSTEM_ADMIN)
                || loginUser.getCompanyDepartment().getCompany().equals(companyDepartment.getCompany())) {
            return UserDao.getInstance().getUsersByCompanyDepartment(id);
        } else {
            servletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
    }
}
