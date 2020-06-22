package de.hse.licensemanager.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.HttpHeaders;

@WebFilter(description = "Filter to check if session is active", displayName = "Log-In", value = "/*")
public class AuthenticationFilter implements Filter {

    private final String LOGIN_BROWSER = "/login.html";
    private final String LOGIN_REST = "/rest/users/login";
    private final String REST = "/rest/";

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {

        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;
        final HttpSession httpSession = httpRequest.getSession(false);
        final String loginURI = httpRequest.getContextPath() + LOGIN_BROWSER;
        final String requestURI = httpRequest.getRequestURI();

        final boolean restRequest = requestURI.startsWith(httpRequest.getContextPath() + REST);
        final boolean loggedIn = httpSession != null && httpSession.getAttribute(HttpHeaders.AUTHORIZATION) != null;
        final boolean loginRequest = requestURI.equals(loginURI)
                || requestURI.equals(httpRequest.getContextPath() + LOGIN_REST);

        if (loggedIn || loginRequest) {
            chain.doFilter(httpRequest, httpResponse);
        } else if (restRequest) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            httpResponse.sendRedirect(loginURI);
        }
    }

    @Override
    public void destroy() {
    }
}
