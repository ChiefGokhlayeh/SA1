package de.hse.licensemanager.resource;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.HttpHeaders;

import de.hse.licensemanager.model.User;
import de.hse.licensemanager.model.User.Group;

public class LoginChecker implements ILoginChecker {

    @Override
    public int compareGroup(final HttpServletRequest request, final Group group) {
        return compareGroup(getLoginUser(request), group);
    }

    @Override
    public int compareGroup(final User loginUser, final Group group) {
        return loginUser.getGroup().compareTo(group);
    }

    @Override
    public User getLoginUser(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        } else {
            return (User) request.getSession(false).getAttribute(HttpHeaders.AUTHORIZATION);
        }
    }

    @Override
    public User getLoginUser(final HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        if (session == null) {
            throw new IllegalStateException("Login-Filter protected REST API called without login.");
        } else {
            return (User) request.getSession(false).getAttribute(HttpHeaders.AUTHORIZATION);
        }
    }

}
