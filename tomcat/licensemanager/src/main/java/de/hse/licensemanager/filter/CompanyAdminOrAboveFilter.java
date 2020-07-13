package de.hse.licensemanager.filter;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.ext.Provider;

import de.hse.licensemanager.model.User.Group;

@Provider
@Priority(Priorities.AUTHORIZATION)
public class CompanyAdminOrAboveFilter extends GroupFilter {

    public CompanyAdminOrAboveFilter() {
        super(Group.COMPANY_ADMIN, Group.SYSTEM_ADMIN);
    }

    public CompanyAdminOrAboveFilter(final ResourceInfo resourceInfo, final HttpServletRequest httpRequest) {
        super(resourceInfo, httpRequest, Group.COMPANY_ADMIN, Group.SYSTEM_ADMIN);
    }
}
