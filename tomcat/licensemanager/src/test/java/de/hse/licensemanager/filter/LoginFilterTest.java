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

public class LoginFilterTest {

    private LoginFilter filter;
    private HttpServletRequest httpRequest;
    private ResourceInfo resourceInfo;
    private ContainerRequestContext requestContext;

    private User user;

    @Before
    public void setUp() {
        httpRequest = mock(HttpServletRequest.class);
        resourceInfo = mock(ResourceInfo.class);
        requestContext = mock(ContainerRequestContext.class);
        filter = new LoginFilter(resourceInfo, httpRequest);

        user = new User();
    }

    @Test
    public void testNoSessionAbortUnauthorized() throws NoSuchMethodException, SecurityException, IOException {
        when(httpRequest.getSession(false)).thenReturn(null);

        final Method getCountMethod = UsersResource.class.getMethod("getCount");

        when(resourceInfo.getResourceMethod()).thenReturn(getCountMethod);
        when(resourceInfo.getResourceClass()).thenAnswer((inv) -> getCountMethod.getDeclaringClass());

        filter.filter(requestContext);

        verify(requestContext)
                .abortWith(argThat((resp) -> resp.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()));
        verify(httpRequest, never()).getSession(true);
    }

    @Test
    public void testNoUserAttributeAbortUnauthorized() throws NoSuchMethodException, SecurityException, IOException {
        final HttpSession fakeSession = mock(HttpSession.class);
        when(fakeSession.getAttribute(any())).thenReturn(null);
        when(httpRequest.getSession(false)).thenReturn(fakeSession);

        final Method getCountMethod = UsersResource.class.getMethod("getCount");

        when(resourceInfo.getResourceMethod()).thenReturn(getCountMethod);
        when(resourceInfo.getResourceClass()).thenAnswer((inv) -> getCountMethod.getDeclaringClass());

        filter.filter(requestContext);

        verify(requestContext)
                .abortWith(argThat((resp) -> resp.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()));
        verify(httpRequest, never()).getSession(true);
    }

    @Test
    public void testAllowNoLoginRequired() throws NoSuchMethodException, SecurityException, IOException {
        when(httpRequest.getSession(false)).thenReturn(null);

        final Method hashCodeMethod = Object.class.getMethod("hashCode");

        when(resourceInfo.getResourceMethod()).thenReturn(hashCodeMethod);
        when(resourceInfo.getResourceClass()).thenAnswer((inv) -> hashCodeMethod.getDeclaringClass());

        filter.filter(requestContext);

        verify(requestContext, never()).abortWith(any());
        verify(httpRequest, never()).getSession(true);
    }

    @Test
    public void testAllowLoggedIn() throws NoSuchMethodException, SecurityException, IOException {
        final HttpSession fakeSession = mock(HttpSession.class);
        when(fakeSession.getAttribute(any())).thenReturn(user);
        when(httpRequest.getSession(false)).thenReturn(fakeSession);

        final Method hashCodeMethod = UsersResource.class.getMethod("getCount");

        when(resourceInfo.getResourceMethod()).thenReturn(hashCodeMethod);
        when(resourceInfo.getResourceClass()).thenAnswer((inv) -> hashCodeMethod.getDeclaringClass());

        filter.filter(requestContext);

        verify(requestContext, never()).abortWith(any());
        verify(httpRequest, never()).getSession(true);
    }
}
