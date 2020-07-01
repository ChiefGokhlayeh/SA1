package de.hse.licensemanager.filter;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import de.hse.licensemanager.model.User;

@Provider
@Priority(Priorities.AUTHORIZATION)
public class AdminOnlyFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Context
    private HttpServletRequest httpRequest;

    public AdminOnlyFilter() {
        this(null, null);
    }

    public AdminOnlyFilter(final ResourceInfo resourceInfo, final HttpServletRequest httpRequest) {
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
        if (resourceMethod.isAnnotationPresent(AdminOnly.class) || resourceClass.isAnnotationPresent(AdminOnly.class)) {
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
            if (!user.isActive() || !user.getSystemGroup().getDisplayName().equals("admin")) {
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
                return;
            }

            /* Let 'em pass. */
        } else {
            /* Resource is not protected by AdminOnly filter. */
        }
    }
}
