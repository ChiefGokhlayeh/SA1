package de.hse.licensemanager.dao;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.UnitTestSupport;
import de.hse.licensemanager.model.Company;
import de.hse.licensemanager.model.CompanyDepartment;
import de.hse.licensemanager.model.Credentials;
import de.hse.licensemanager.model.User;
import de.hse.licensemanager.model.User.Group;

public class UserDaoTest {

    @Before
    public void setupBeforeTest() {
        UnitTestSupport.initDatabase();
    }

    @Test
    public void testInstanceAllocated() {
        assertThat(UserDao.getInstance(), notNullValue());
    }

    @Test
    public void testInstanceSingleton() {
        final IUserDao a = UserDao.getInstance();
        final IUserDao b = UserDao.getInstance();
        assertThat(a, sameInstance(b));
    }

    @Test
    public void testFindUserById() {
        final User mustermann = UserDao.getInstance().getUser(UnitTestSupport.USER_ID_MUSTERMANN);
        assertThat(mustermann, notNullValue());
    }

    @Test
    public void testFoundUserDataPopulated() {
        final User hannelore = UserDao.getInstance().getUser(UnitTestSupport.USER_ID_HANNELORE);

        assertThat(hannelore.getFirstname(), not(emptyOrNullString()));
        assertThat(hannelore.getLastname(), not(emptyOrNullString()));
        assertThat(hannelore.getEmail(), not(emptyOrNullString()));
    }

    @Test
    public void testFoundUserNestedDataPopulated() {
        final User hannelore = UserDao.getInstance().getUser(UnitTestSupport.USER_ID_HANNELORE);

        assertThat(hannelore.getCompany().getAddress(), equalTo(UnitTestSupport.COMPANY_ADDRESS_NOTABROTHEL));
        assertThat(hannelore.getCompanyDepartment().getName(),
                equalTo(UnitTestSupport.COMPANY_DEPARTMENT_NAME_ACCOUNTING));
    }

    @Test
    public void testQueryAllUsers() {
        final List<User> users = UserDao.getInstance().getUsers();

        assertThat(users, not(empty()));
        assertThat(users, hasSize(3));
        assertThat(users.stream().map((u) -> u.getId()).collect(Collectors.toList()),
                containsInAnyOrder(UnitTestSupport.USER_ID_HANNELORE, UnitTestSupport.USER_ID_MUSTERMANN,
                        UnitTestSupport.USER_ID_DELETEME));
    }

    @Test
    public void testQueryUsersByCompany() {
        final Company company = CompanyDao.getInstance().getCompany(UnitTestSupport.COMPANY_ID_LICENSEMANAGER);

        final List<User> users = UserDao.getInstance().getUsersByCompany(company.getId());

        final List<User> expectedUsers = company.getDepartments().stream().flatMap((dep) -> dep.getUsers().stream())
                .collect(Collectors.toList());

        assertThat(users, not(empty()));
        assertThat(users, hasSize(expectedUsers.size()));
        assertThat(users, everyItem(in(expectedUsers)));
        assertThat(users, everyItem(hasProperty("company", equalTo(company))));
    }

    @Test
    public void testSaveSimple() {
        final User user = new User("Max", "Mustermann", "max.mustermann@email.com",
                CompanyDepartmentDao.getInstance().getCompanyDepartment(UnitTestSupport.COMPANY_DEPARTMENT_ID_IT),
                Group.USER, new Credentials("max_mu", "hello world"));

        UserDao.getInstance().save(user);

        final List<User> users = UserDao.getInstance().getUsers();

        assertThat(user.getId(), is(in(users.stream().map((c) -> c.getId()).collect(Collectors.toList()))));
    }

    @Test
    public void testDeleteUser() {
        final User user = UserDao.getInstance().getUser(UnitTestSupport.USER_ID_DELETEME);

        assertThat(user, notNullValue());
        assertThat(user, in(UserDao.getInstance().getUsers()));

        UserDao.getInstance().delete(user.getId());

        assertThat(user, not(in(UserDao.getInstance().getUsers())));
    }

    @Test
    public void testSaveCascadePersistence() {
        final User user = new User("A", "B", "C",
                new CompanyDepartment("Some Department",
                        CompanyDao.getInstance().getCompany(UnitTestSupport.COMPANY_ID_LICENSEMANAGER)),
                Group.COMPANY_ADMIN, new Credentials("user A", "super secret password"));

        UserDao.getInstance().save(user);

        assertThat(user, in(UserDao.getInstance().getUsers()));
        assertThat(user.getGroup(), in(Group.values()));
    }
}
