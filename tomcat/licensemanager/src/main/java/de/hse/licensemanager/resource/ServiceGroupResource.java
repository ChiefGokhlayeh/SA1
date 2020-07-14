package de.hse.licensemanager.resource;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.hse.licensemanager.dao.ServiceGroupDao;
import de.hse.licensemanager.filter.SystemAdminOnly;
import de.hse.licensemanager.filter.Login;
import de.hse.licensemanager.model.ServiceGroup;
import de.hse.licensemanager.model.ServiceGroupId;
import de.hse.licensemanager.model.User;
import de.hse.licensemanager.model.User.Group;

@Login
public class ServiceGroupResource {

    private final ILoginChecker checker;

    private final ServiceGroupId id;

    public ServiceGroupResource(final long serviceContractId, final long userId) {
        this(new ServiceGroupId(serviceContractId, userId));
    }

    public ServiceGroupResource(final ServiceGroupId id) {
        this(id, new LoginChecker());
    }

    public ServiceGroupResource(final long serviceContractId, final long userId, final ILoginChecker checker) {
        this(new ServiceGroupId(serviceContractId, userId), new LoginChecker());
    }

    public ServiceGroupResource(final ServiceGroupId id, final ILoginChecker checker) {
        this.id = id;
        this.checker = checker;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@Context final HttpServletRequest request) throws IOException {
        final ServiceGroup serviceGroup = ServiceGroupDao.getInstance().getServiceGroup(id);
        final User loginUser = checker.getLoginUser(request);

        if (serviceGroup == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else if (checker.compareGroup(loginUser, Group.SYSTEM_ADMIN) >= 0
                || (checker.compareGroup(loginUser, Group.COMPANY_ADMIN) >= 0
                        && loginUser.getCompany().equals(serviceGroup.getServiceContract().getContractor()))
                || (loginUser.equals(serviceGroup.getUser()))) {
            return Response.ok(serviceGroup).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @DELETE
    @SystemAdminOnly
    public void delete() {
        ServiceGroupDao.getInstance().delete(id);
    }
}
