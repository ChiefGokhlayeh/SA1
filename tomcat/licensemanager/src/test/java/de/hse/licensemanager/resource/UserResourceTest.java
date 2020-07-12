package de.hse.licensemanager.resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.PrepareTests;
import de.hse.licensemanager.dao.CompanyDepartmentDao;
import de.hse.licensemanager.dao.SystemGroupDao;
import de.hse.licensemanager.dao.UserDao;
import de.hse.licensemanager.model.Credentials;
import de.hse.licensemanager.model.User;

public class UserResourceTest {

    private Credentials credentials;
    private User user;
    private Credentials someoneElsesCredentials;
    private User someoneElse;
    private UserResource userResource;

    @Before
    public void setUp() {
        PrepareTests.initDatabase();

        credentials = new Credentials();
        credentials.setLoginname("testuser");
        credentials.generateNewHash("hello world");
        user = new User();
        user.setFirstname("Test");
        user.setLastname("User");
        user.setEmail("test@user.com");
        user.setCompanyDepartment(
                CompanyDepartmentDao.getInstance().getCompanyDepartment(PrepareTests.COMPANY_DEPARTMENT_ID_ACCOUNTING));
        user.setActive(true);
        user.setVerified(true);
        user.setSystemGroup(SystemGroupDao.getInstance().getSystemGroup(PrepareTests.SYSTEM_GROUP_ID_USER));
        user.setCredentials(credentials);

        someoneElsesCredentials = new Credentials();
        someoneElsesCredentials.setLoginname("testuser2");
        someoneElsesCredentials.generateNewHash("hello world");
        someoneElse = new User();
        someoneElse.setFirstname("Somebody");
        someoneElse.setLastname("Else");
        someoneElse.setEmail("test2@admin.com");
        someoneElse.setCompanyDepartment(
                CompanyDepartmentDao.getInstance().getCompanyDepartment(PrepareTests.COMPANY_DEPARTMENT_ID_ACCOUNTING));
        someoneElse.setActive(true);
        someoneElse.setVerified(true);
        someoneElse.setSystemGroup(SystemGroupDao.getInstance().getSystemGroup(PrepareTests.SYSTEM_GROUP_ID_USER));
        someoneElse.setCredentials(someoneElsesCredentials);

        UserDao.getInstance().save(user);
        UserDao.getInstance().save(someoneElse);

        userResource = new UserResource(user.getId());
    }

    @Test
    public void testGet() {
        final Response testUserResponse = userResource.get();

        assertThat(testUserResponse, notNullValue());
        assertThat(testUserResponse.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(testUserResponse.getEntity(), equalTo(user));
    }

    @Test
    public void testDelete() {
        assertThat(user, in(UserDao.getInstance().getUsers()));

        userResource.delete();

        assertThat(user, not(in(UserDao.getInstance().getUsers())));
    }

    @Test
    public void testPutSelf() {
        final String changedFirstname = "Foo";
        final User modifiedUser = new User("Foo", user.getLastname(), user.getEmail(), null, null, null);

        final HttpServletRequest fakeRequest = mock(HttpServletRequest.class);
        final HttpSession fakeSession = mock(HttpSession.class);
        final UriInfo uriInfo = mock(UriInfo.class);
        when(fakeSession.getAttribute(HttpHeaders.AUTHORIZATION)).thenReturn(user);
        when(fakeRequest.getSession(anyBoolean())).thenReturn(fakeSession);
        when(uriInfo.getAbsolutePath()).thenReturn(URI.create("http://some-host/users/me"));

        final Response response = userResource.put(modifiedUser, uriInfo, fakeRequest);

        assertThat(response, notNullValue());
        assertThat(response.getStatus(), is(Response.Status.CREATED.getStatusCode()));
        assertThat(UserDao.getInstance().getUser(user.getId()).getFirstname(), is(equalTo(changedFirstname)));
        assertThat(UserDao.getInstance().getUser(user.getId()).getLastname(), is(equalTo(user.getLastname())));
        assertThat(UserDao.getInstance().getUser(user.getId()).getEmail(), is(equalTo(user.getEmail())));
        assertThat(UserDao.getInstance().getUser(user.getId()).getCompanyDepartment(),
                is(equalTo(user.getCompanyDepartment())));
    }

    @Test
    public void testPutSomebodyElseAsNonAdmin() {
        final String expectedFirstname = "Foo";
        user.setFirstname(expectedFirstname);

        final HttpServletRequest fakeRequest = mock(HttpServletRequest.class);
        final HttpSession fakeSession = mock(HttpSession.class);
        final UriInfo uriInfo = mock(UriInfo.class);
        when(fakeSession.getAttribute(HttpHeaders.AUTHORIZATION)).thenReturn(someoneElse);
        when(fakeRequest.getSession(anyBoolean())).thenReturn(fakeSession);
        when(uriInfo.getAbsolutePath()).thenReturn(URI.create("http://some-host/users/1"));

        final Response response = userResource.put(user, uriInfo, fakeRequest);

        assertThat(response, notNullValue());
        assertThat(response.getStatus(), is(Response.Status.FORBIDDEN.getStatusCode()));
    }
}
