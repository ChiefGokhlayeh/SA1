package de.hse.licensemanager.resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

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
    private UserResource userResource;
    private UriInfo uriInfo;

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

        UserDao.getInstance().save(user);

        uriInfo = mock(UriInfo.class);

        userResource = new UserResource(uriInfo, user.getId());
    }

    @Test
    public void testGetUser() {
        final Response testUserResponse = userResource.getUser();

        assertThat(testUserResponse, notNullValue());
        assertThat(testUserResponse.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(testUserResponse.getEntity(), equalTo(user));
    }

    @Test
    public void testDeleteUser() {
        assertThat(user, in(UserDao.getInstance().getUsers()));

        userResource.deleteUser();

        assertThat(user, not(in(UserDao.getInstance().getUsers())));
    }
}
