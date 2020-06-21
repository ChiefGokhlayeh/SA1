package de.hse.licensemanager.resource;

import static org.hamcrest.core.Every.everyItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.PrepareTests;
import de.hse.licensemanager.dao.CompanyDepartmentDao;
import de.hse.licensemanager.dao.SystemGroupDao;
import de.hse.licensemanager.dao.UserDao;
import de.hse.licensemanager.model.Credentials;
import de.hse.licensemanager.model.User;

public class UsersResourceTest {
    private UsersResource usersResource;

    @Before
    public void setUp() {
        PrepareTests.initDatabase();

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
        final Credentials credentials = new Credentials();
        credentials.setLoginname("testuser");
        credentials.setPasswordHash(new byte[16]);
        final User user = new User();
        user.setFirstname("Test");
        user.setLastname("User");
        user.setEmail("some.email@test.org");
        user.setActive(true);
        user.setVerified(false);
        user.setSystemGroup(SystemGroupDao.getInstance().getSystemGroup(PrepareTests.SYSTEM_GROUP_ID_USER));
        user.setCompanyDepartment(
                CompanyDepartmentDao.getInstance().getCompanyDepartment(PrepareTests.COMPANY_DEPARTMENT_ID_IT));
        user.setCredentials(credentials);

        usersResource.newUser(user, null);

        assertThat(user, is(in(UserDao.getInstance().getUsers())));
    }
}
