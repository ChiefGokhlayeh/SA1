package de.hse.licensemanager.resource;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hse.licensemanager.model.User;
import de.hse.licensemanager.model.User.Group;

public interface ILoginChecker {

    public int compareGroup(final HttpServletRequest request, final Group group);

    public int compareGroup(final User user, final Group group);

    public User getLoginUser(final HttpServletRequest request, final HttpServletResponse response) throws IOException;

    public User getLoginUser(final HttpServletRequest request);
}
