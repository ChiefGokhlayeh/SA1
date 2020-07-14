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

import de.hse.licensemanager.dao.UserDao;
import de.hse.licensemanager.filter.SystemAdminOnly;
import de.hse.licensemanager.filter.Login;
import de.hse.licensemanager.model.User;
import de.hse.licensemanager.model.User.Group;

@Login
public class UserResource {
    private final long id;

    private final ILoginChecker checker;

    public UserResource(final User user) {
        this(user.getId());
    }

    public UserResource(final long id) {
        this(id, new LoginChecker());
    }

    public UserResource(final User user, final ILoginChecker checker) {
        this(user.getId(), checker);
    }

    public UserResource(final long id, final ILoginChecker checker) {
        this.id = id;
        this.checker = checker;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() {
        final User user = UserDao.getInstance().getUser(id);
        if (user == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(user).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response put(final User modifiedUser, @Context final UriInfo uriInfo,
            @Context final HttpServletRequest request) {
        final User loginUser = checker.getLoginUser(request);

        final User user = UserDao.getInstance().getUser(id);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else if (checker.compareGroup(loginUser, Group.SYSTEM_ADMIN) >= 0
                || (checker.compareGroup(loginUser, Group.COMPANY_ADMIN) >= 0
                        && loginUser.getCompany().equals(user.getCompany()))
                || loginUser.equals(user)) {
            UserDao.getInstance().modify(id, modifiedUser);
            return Response.created(uriInfo.getAbsolutePath()).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @DELETE
    @SystemAdminOnly
    public void delete() {
        UserDao.getInstance().delete(id);
    }
}
