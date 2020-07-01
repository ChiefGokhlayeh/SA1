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

import de.hse.licensemanager.model.SystemGroup;
import de.hse.licensemanager.model.User;
import de.hse.licensemanager.resource.UsersResource;

public class AdminOnlyFilterTest {
    private AdminOnlyFilter filter;
    private HttpServletRequest httpRequest;
    private ResourceInfo resourceInfo;
    private ContainerRequestContext requestContext;

    private SystemGroup adminGroup, userGroup;

    private User admin, disabledAdmin, user;

    @Before
    public void setUp() {
        httpRequest = mock(HttpServletRequest.class);
        resourceInfo = mock(ResourceInfo.class);
        requestContext = mock(ContainerRequestContext.class);
        filter = new AdminOnlyFilter(resourceInfo, httpRequest);

        adminGroup = new SystemGroup();
        adminGroup.setDisplayName("admin");
        userGroup = new SystemGroup();
        userGroup.setDisplayName("user");

        admin = new User();
        admin.setActive(true);
        admin.setSystemGroup(adminGroup);
        disabledAdmin = new User();
        disabledAdmin.setActive(false);
        disabledAdmin.setSystemGroup(adminGroup);
        user = new User();
        user.setActive(true);
        user.setSystemGroup(userGroup);
    }

    @Test
    public void testNoSessionAbortError() throws NoSuchMethodException, SecurityException, IOException {
        when(httpRequest.getSession(false)).thenReturn(null);

        final Method getCountMethod = UsersResource.class.getMethod("getUsers");

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

        final Method resourceMethod = UsersResource.class.getMethod("getUsers");

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

        final Method resourceMethod = UsersResource.class.getMethod("getUsers");

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

        final Method resourceMethod = UsersResource.class.getMethod("getUsers");

        when(resourceInfo.getResourceMethod()).thenReturn(resourceMethod);
        when(resourceInfo.getResourceClass()).thenAnswer((inv) -> resourceMethod.getDeclaringClass());

        filter.filter(requestContext);

        verify(requestContext)
                .abortWith(argThat((resp) -> resp.getStatus() == Response.Status.FORBIDDEN.getStatusCode()));
        verify(httpRequest, never()).getSession(true);
    }
}
