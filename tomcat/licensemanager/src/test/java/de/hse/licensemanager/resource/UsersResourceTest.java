package de.hse.licensemanager.resource;

import static org.hamcrest.core.Every.everyItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.UnitTestSupport;
import de.hse.licensemanager.dao.CompanyDepartmentDao;
import de.hse.licensemanager.dao.UserDao;
import de.hse.licensemanager.model.Credentials;
import de.hse.licensemanager.model.User;
import de.hse.licensemanager.model.User.Group;

public class UsersResourceTest {
    private UsersResource usersResource;

    @Before
    public void setUp() {
        UnitTestSupport.initDatabase();

        usersResource = new UsersResource();
    }

    @Test
    public void testGetUsers() {
        final List<User> users = usersResource.getUsers();

        assertThat(users, everyItem(is(in(UserDao.getInstance().getUsers()))));
    }

    @Test
    public void testCountUsers() {
        final String count = usersResource.getCount();

        assertThat(Integer.parseInt(count), equalTo(UserDao.getInstance().getUsers().size()));
    }

    @Test
    public void testNewUserCreated() throws IOException {
        final UriInfo uriInfo = mock(UriInfo.class);

        final Credentials credentials = new Credentials();
        credentials.setLoginname("testuser");
        credentials.generateNewHash("hello world");
        final User user = new User();
        user.setFirstname("Test");
        user.setLastname("User");
        user.setEmail("some.email@test.org");
        user.setActive(true);
        user.setGroup(Group.USER);
        user.setCompanyDepartment(
                CompanyDepartmentDao.getInstance().getCompanyDepartment(UnitTestSupport.COMPANY_DEPARTMENT_ID_IT));
        user.setCredentials(credentials);

        usersResource.newUser(user, uriInfo);

        assertThat(user, is(in(UserDao.getInstance().getUsers())));
    }
}
