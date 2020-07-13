package de.hse.licensemanager.filter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import de.hse.licensemanager.model.User;
import de.hse.licensemanager.model.User.Group;

public class GroupFilter implements ContainerRequestFilter {

    protected final Group[] allowedGroups;

    @Context
    protected ResourceInfo resourceInfo;

    @Context
    protected HttpServletRequest httpRequest;

    public GroupFilter(final Group... allowedGroups) {
        this(null, null, allowedGroups);
    }

    public GroupFilter(final ResourceInfo resourceInfo, final HttpServletRequest httpRequest,
            final Group... allowedGroups) {
        this.allowedGroups = allowedGroups;
        this.resourceInfo = resourceInfo;
        this.httpRequest = httpRequest;
    }

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        final Method resourceMethod = resourceInfo.getResourceMethod();
        final Class<?> resourceClass = resourceInfo.getResourceClass();

        /*
         * Here we check if either the JAX-RS endpoint method itself or the surrounding
         * resource class are marked as AdminOnly-protected.
         */
        if (resourceMethod.isAnnotationPresent(SystemAdminOnly.class)
                || resourceClass.isAnnotationPresent(SystemAdminOnly.class)) {
            /* Resource was marked as AdminOnly-protected. */
            final HttpSession httpSession = httpRequest.getSession(false);
            final boolean loggedIn = httpSession != null && httpSession.getAttribute(HttpHeaders.AUTHORIZATION) != null;

            if (!loggedIn) {
                /*
                 * We assume that a user must already be logged in. Otherwise this filter has no
                 * data to work on.
                 */
                requestContext.abortWith(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
                return;
            }

            final User user = (User) httpSession.getAttribute(HttpHeaders.AUTHORIZATION);
            if (!user.isActive()
                    || Arrays.stream(allowedGroups).noneMatch((allowedGroup) -> allowedGroup.equals(user.getGroup()))) {
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
                return;
            }

            /* Let 'em pass. */
        } else {
            /* Resource is not protected by AdminOnly filter. */
        }
    }
}
