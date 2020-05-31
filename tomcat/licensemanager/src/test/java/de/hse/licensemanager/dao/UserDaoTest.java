package de.hse.licensemanager.dao;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.PrepareTests;
import de.hse.licensemanager.model.User;

public class UserDaoTest {

    @Before
    public void setupBeforeTest() {
        PrepareTests.initDatabase();
    }

    @Test
    public void testInstanceAllocated() {
        assertThat(UserDao.getInstance(), notNullValue());
    }

    @Test
    public void testInstanceSingleton() {
        final UserDao a = UserDao.getInstance();
        final UserDao b = UserDao.getInstance();
        assertThat(a, sameInstance(b));
    }

    @Test
    public void testFindUserById() {
        final User mustermann = UserDao.getInstance().getUser(PrepareTests.USER_ID_MUSTERMANN);
        assertThat(mustermann, notNullValue());
    }

    @Test
    public void testFoundUserDataPopulated() {
        final User hannelore = UserDao.getInstance().getUser(PrepareTests.USER_ID_HANNELORE);

        assertThat(hannelore.getFirstname(), not(emptyOrNullString()));
        assertThat(hannelore.getLastname(), not(emptyOrNullString()));
        assertThat(hannelore.getLoginname(), not(emptyOrNullString()));
    }

    @Test
    public void testFoundUserNestedDataPopulated() {
        final User hannelore = UserDao.getInstance().getUser(PrepareTests.USER_ID_HANNELORE);

        assertThat(hannelore.getCompany().getAddress(), equalTo(PrepareTests.COMPANY_ADDRESS_NOTABROTHEL));
        assertThat(hannelore.getCompanyDepartment().getName(),
                equalTo(PrepareTests.COMPANY_DEPARTMENT_NAME_ACCOUNTING));
    }

    @Test
    public void testQueryAllUsers() {
        final List<User> users = UserDao.getInstance().getUsers();

        assertThat(users, not(empty()));
        assertThat(users, hasSize(3));
        assertThat(users.stream().map((u) -> u.getId()).collect(Collectors.toList()), containsInAnyOrder(
                PrepareTests.USER_ID_HANNELORE, PrepareTests.USER_ID_MUSTERMANN, PrepareTests.USER_ID_DELETEME));
    }

    @Test
    public void testSaveSimple() {
        final User user = new User();
        user.setFirstname("Max");
        user.setLastname("Mustermann");
        user.setLoginname("max_mu");
        user.setEmail("max.mustermann@email.com");
        user.setPasswordHash(new byte[60]);
        user.setActive(true);
        user.setVerified(true);
        user.setSystemGroup(SystemGroupDao.getInstance().getSystemGroup(PrepareTests.SYSTEM_GROUP_ID_USER));
        user.setCompanyDepartment(
                CompanyDepartmentDao.getInstance().getCompanyDepartment(PrepareTests.COMPANY_DEPARTMENT_ID_IT));

        UserDao.getInstance().save(user);

        final List<User> users = UserDao.getInstance().getUsers();

        assertThat(user.getId(), is(in(users.stream().map((c) -> c.getId()).collect(Collectors.toList()))));
    }

    @Test
    public void testDeleteUser() {
        final User user = UserDao.getInstance().getUser(PrepareTests.USER_ID_DELETEME);

        assertThat(user, notNullValue());
        assertThat(user, in(UserDao.getInstance().getUsers()));

        UserDao.getInstance().delete(user.getId());

        assertThat(user, not(in(UserDao.getInstance().getUsers())));
    }
}
