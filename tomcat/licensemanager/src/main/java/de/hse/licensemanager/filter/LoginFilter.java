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

@Provider
@Priority(Priorities.AUTHENTICATION)
public class LoginFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Context
    private HttpServletRequest httpRequest;

    public LoginFilter() {
        this(null, null);
    }

    public LoginFilter(final ResourceInfo resourceInfo, final HttpServletRequest httpRequest) {
        this.resourceInfo = resourceInfo;
        this.httpRequest = httpRequest;
    }

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        final Method resourceMethod = resourceInfo.getResourceMethod();
        final Class<?> resourceClass = resourceInfo.getResourceClass();

        /*
         * Here we check if either the JAX-RS endpoint method itself or the surrounding
         * resource class are marked as Login-protected.
         */
        if (resourceMethod.isAnnotationPresent(Login.class) || resourceClass.isAnnotationPresent(Login.class)) {
            /* Resource was marked as Login-protected. */
            final HttpSession httpSession = httpRequest.getSession(false);

            final boolean loggedIn = httpSession != null && httpSession.getAttribute(HttpHeaders.AUTHORIZATION) != null;

            if (loggedIn) {
                /* let them pass */
            } else {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            }
        } else {
            /* Resource is not protected by Login filter. */
        }
    }
}
