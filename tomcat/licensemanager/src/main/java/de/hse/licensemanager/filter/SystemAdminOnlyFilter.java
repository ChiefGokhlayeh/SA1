package de.hse.licensemanager.filter;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.ext.Provider;

import de.hse.licensemanager.model.User.Group;

@Provider
@Priority(Priorities.AUTHORIZATION)
public class SystemAdminOnlyFilter extends GroupFilter {

    public SystemAdminOnlyFilter() {
        super(Group.SYSTEM_ADMIN);
    }

    public SystemAdminOnlyFilter(final ResourceInfo resourceInfo, final HttpServletRequest httpRequest) {
        super(Group.SYSTEM_ADMIN, resourceInfo, httpRequest);
    }
}
