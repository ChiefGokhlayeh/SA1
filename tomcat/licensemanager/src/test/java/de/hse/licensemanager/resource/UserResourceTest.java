package de.hse.licensemanager.resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

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

        userResource = new UserResource(uriInfo, null, user.getId());
    }

    @Test
    public void testGetUser() {
        final User testUser = userResource.getUser();

        assertThat(testUser, notNullValue());
        assertThat(testUser, equalTo(user));
    }

    @Test
    public void testGetUserHTML() {
        final User testUser = userResource.getUserHTML();

        assertThat(testUser, notNullValue());
        assertThat(testUser, equalTo(user));
    }

    @Test
    public void testDeleteUser() {
        assertThat(user, in(UserDao.getInstance().getUsers()));

        userResource.deleteUser();

        assertThat(user, not(in(UserDao.getInstance().getUsers())));
    }

    @Test
    public void testPutUser() throws URISyntaxException {
        when(uriInfo.getAbsolutePath()).thenReturn(new URI("http://www.test.org/user/" + user.getId()));
        final JAXBElement<User> dummyElement = new JAXBElement<>(new QName("http://www.test.org", "user"), User.class,
                user);
        userResource.putUser(dummyElement);
    }
}
