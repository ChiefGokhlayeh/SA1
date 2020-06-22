package de.hse.licensemanager.filter;

import static org.mockito.Mockito.*;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;

public class AuthenticationFilterTest {

    private final String CORRECT_LOGIN_URL = "/login.html";

    private AuthenticationFilter filter;
    private HttpServletRequest httpRequest;
    private HttpServletResponse httpResponse;
    private FilterChain filterChain;

    @Before
    public void setUp() {
        filter = new AuthenticationFilter();
        httpRequest = mock(HttpServletRequest.class);
        httpResponse = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);

    }

    @Test
    public void testRedirectOnWrongURI() throws IOException, ServletException {
        when(httpRequest.getContextPath()).thenReturn("");
        when(httpRequest.getRequestURI()).thenReturn(CORRECT_LOGIN_URL + "wrong");

        filter.doFilter(httpRequest, httpResponse, filterChain);

        verify(filterChain, never()).doFilter(null, null);
        verify(httpResponse).sendRedirect(CORRECT_LOGIN_URL);
    }

    @Test
    public void testContinueChainOnNoSession() throws IOException, ServletException {
        when(httpRequest.getContextPath()).thenReturn("");
        when(httpRequest.getRequestURI()).thenReturn(CORRECT_LOGIN_URL);
        when(httpRequest.getSession(false)).thenReturn(null);

        filter.doFilter(httpRequest, httpResponse, filterChain);

        verify(filterChain, times(1)).doFilter(httpRequest, httpResponse);
        verifyNoInteractions(httpResponse);
    }

    @Test
    public void testBlockUnauthenticatedRestRequest() throws IOException, ServletException {
        when(httpRequest.getContextPath()).thenReturn("");
        when(httpRequest.getRequestURI()).thenReturn("/rest/users");
        when(httpRequest.getSession(false)).thenReturn(null);

        filter.doFilter(httpRequest, httpResponse, filterChain);

        verify(filterChain, never()).doFilter(httpRequest, httpResponse);
        verify(httpResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void testPassRestLoginRequest() throws IOException, ServletException {
        when(httpRequest.getContextPath()).thenReturn("");
        when(httpRequest.getRequestURI()).thenReturn("/rest/users/login");
        when(httpRequest.getSession(false)).thenReturn(null);

        filter.doFilter(httpRequest, httpResponse, filterChain);

        verify(filterChain).doFilter(httpRequest, httpResponse);
        verify(httpResponse, never()).sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void testContinueChainOnLoginWithExistingSession() throws IOException, ServletException {
        when(httpRequest.getContextPath()).thenReturn("");
        when(httpRequest.getRequestURI()).thenReturn(CORRECT_LOGIN_URL);
        when(httpRequest.getSession(false)).thenReturn(mock(HttpSession.class));

        filter.doFilter(httpRequest, httpResponse, filterChain);

        verify(filterChain, times(1)).doFilter(httpRequest, httpResponse);
        verify(httpRequest, times(1)).getSession(false);
        verifyNoInteractions(httpResponse);
    }
}
