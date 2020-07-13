package de.hse.licensemanager.resource;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;

import de.hse.licensemanager.dao.UserDao;
import de.hse.licensemanager.filter.SystemAdminOnly;
import de.hse.licensemanager.filter.CompanyAdminOrAbove;
import de.hse.licensemanager.filter.Login;
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
    public List<User> getUsers() {
        return UserDao.getInstance().getUsers();
    }

    @GET
    @Path("count")
    @Login
    @Produces(MediaType.TEXT_PLAIN)
    public String getCount() {
        final int count = UserDao.getInstance().getUsers().size();
        return String.valueOf(count);
    }

    @GET
    @Path("group-types")
    @Login
    @Produces(MediaType.APPLICATION_JSON)
    public Group[] getGroupTypes() {
        return Group.values();
    }

    @POST
    @Login
    @SystemAdminOnly
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<User> newUser(final User user, @Context final HttpServletResponse servletResponse) throws IOException {
        UserDao.getInstance().save(user);
        return UserDao.getInstance().getUsers();
    }

    @Path("{user}")
    public UserResource getUser(@PathParam("user") final Long id) {
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
    @CompanyAdminOrAbove
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getUsersByCompany(@PathParam("company") final long id,
            @Context final HttpServletRequest servletRequest, @Context final HttpServletResponse servletResponse)
            throws IOException {
        final User loginUser = checker.getLoginUser(servletRequest);

        if (loginUser.getGroup().equals(Group.SYSTEM_ADMIN) || loginUser.getCompany().getId() == id) {
            return UserDao.getInstance().getUsersByCompany(id);
        } else {
            servletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
    }
}
