package de.hse.licensemanager.filter;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.model.User;
import de.hse.licensemanager.resource.UsersResource;

public class SystemAdminOnlyFilterTest {
    private SystemAdminOnlyFilter filter;
    private HttpServletRequest httpRequest;
    private ResourceInfo resourceInfo;
    private ContainerRequestContext requestContext;

    private User.Group adminGroup, userGroup;

    private User admin, disabledAdmin, user;

    @Before
    public void setUp() {
        httpRequest = mock(HttpServletRequest.class);
        resourceInfo = mock(ResourceInfo.class);
        requestContext = mock(ContainerRequestContext.class);
        filter = new SystemAdminOnlyFilter(resourceInfo, httpRequest);

        adminGroup = User.Group.SYSTEM_ADMIN;
        userGroup = User.Group.USER;

        admin = new User();
        admin.setActive(true);
        admin.setGroup(adminGroup);
        disabledAdmin = new User();
        disabledAdmin.setActive(false);
        disabledAdmin.setGroup(adminGroup);
        user = new User();
        user.setActive(true);
        user.setGroup(userGroup);
    }

    @Test
    public void testNoSessionAbortError() throws NoSuchMethodException, SecurityException, IOException {
        when(httpRequest.getSession(false)).thenReturn(null);

        final Method getCountMethod = UsersResource.class.getMethod("all");

        when(resourceInfo.getResourceMethod()).thenReturn(getCountMethod);
        when(resourceInfo.getResourceClass()).thenAnswer((inv) -> getCountMethod.getDeclaringClass());

        filter.filter(requestContext);

        verify(requestContext).abortWith(
                argThat((resp) -> resp.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));
        verify(httpRequest, never()).getSession(true);
    }

    @Test
    public void testBlockUser() throws NoSuchMethodException, SecurityException, IOException {
        final HttpSession fakeSession = mock(HttpSession.class);
        when(fakeSession.getAttribute(any())).thenReturn(user);
        when(httpRequest.getSession(false)).thenReturn(fakeSession);

        final Method resourceMethod = UsersResource.class.getMethod("all");

        when(resourceInfo.getResourceMethod()).thenReturn(resourceMethod);
        when(resourceInfo.getResourceClass()).thenAnswer((inv) -> resourceMethod.getDeclaringClass());

        filter.filter(requestContext);

        verify(requestContext)
                .abortWith(argThat((resp) -> resp.getStatus() == Response.Status.FORBIDDEN.getStatusCode()));
        verify(httpRequest, never()).getSession(true);
    }

    @Test
    public void testAllowAdmin() throws NoSuchMethodException, SecurityException, IOException {
        final HttpSession fakeSession = mock(HttpSession.class);
        when(fakeSession.getAttribute(any())).thenReturn(admin);
        when(httpRequest.getSession(false)).thenReturn(fakeSession);

        final Method resourceMethod = UsersResource.class.getMethod("all");

        when(resourceInfo.getResourceMethod()).thenReturn(resourceMethod);
        when(resourceInfo.getResourceClass()).thenAnswer((inv) -> resourceMethod.getDeclaringClass());

        filter.filter(requestContext);

        verify(requestContext, never()).abortWith(any());
        verify(httpRequest, never()).getSession(true);
    }

    @Test
    public void testBlockDisabledAdmin() throws NoSuchMethodException, SecurityException, IOException {
        final HttpSession fakeSession = mock(HttpSession.class);
        when(fakeSession.getAttribute(any())).thenReturn(disabledAdmin);
        when(httpRequest.getSession(false)).thenReturn(fakeSession);

        final Method resourceMethod = UsersResource.class.getMethod("all");

        when(resourceInfo.getResourceMethod()).thenReturn(resourceMethod);
        when(resourceInfo.getResourceClass()).thenAnswer((inv) -> resourceMethod.getDeclaringClass());

        filter.filter(requestContext);

        verify(requestContext)
                .abortWith(argThat((resp) -> resp.getStatus() == Response.Status.FORBIDDEN.getStatusCode()));
        verify(httpRequest, never()).getSession(true);
    }
}
