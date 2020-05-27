package de.hse.licensemanager.dao;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.BeforeClass;
import org.junit.Test;

import de.hse.licensemanager.PrepareTests;
import de.hse.licensemanager.model.User;

public class UserDaoTest {

        private static final int SYSTEM_GROUP_ID_ADMIN;
        private static final int SYSTEM_GROUP_ID_USER;

        private static final int COMPANY_ID_LICENSEMANAGER;
        private static final int COMPANY_ID_NOTABROTHEL;
        private static final String COMPANY_ADDRESS_NOTABROTHEL = "Reeperbahn 31\nHamburg";

        private static final int COMPANY_DEPARTMENT_ID_IT;
        private static final int COMPANY_DEPARTMENT_ID_ACCOUNTING;
        private static final String COMPANY_DEPARTMENT_NAME_ACCOUNTING = "Accounting";

        private static final int USER_ID_MUSTERMANN;
        private static final int USER_ID_HANNELORE;

        static {
                int id = 1;
                SYSTEM_GROUP_ID_ADMIN = id++;
                SYSTEM_GROUP_ID_USER = id++;

                id = 1;
                COMPANY_ID_LICENSEMANAGER = id++;
                COMPANY_ID_NOTABROTHEL = id++;

                id = 1;
                COMPANY_DEPARTMENT_ID_IT = id++;
                COMPANY_DEPARTMENT_ID_ACCOUNTING = id++;

                id = 1;
                USER_ID_MUSTERMANN = id++;
                USER_ID_HANNELORE = id++;
        }

        @BeforeClass
        public static void SetUpBeforeClass() {
                PrepareTests.initDatabase();

                final EntityManager em = DaoManager.getEntityManager();

                final EntityTransaction et = em.getTransaction();
                et.begin();

                int param = 1;
                em.createNativeQuery("INSERT INTO t_system_group (id, displayname) VALUES (?1, ?2)")
                                .setParameter(param++, SYSTEM_GROUP_ID_ADMIN).setParameter(param++, "admin")
                                .executeUpdate();

                param = 1;
                em.createNativeQuery("INSERT INTO t_system_group (id, displayname) VALUES (?1, ?2)")
                                .setParameter(param++, SYSTEM_GROUP_ID_USER).setParameter(param++, "user")
                                .executeUpdate();

                param = 1;
                em.createNativeQuery("INSERT INTO t_company (id, name, address) VALUES (?1, ?2, ?3)")
                                .setParameter(param++, COMPANY_ID_LICENSEMANAGER)
                                .setParameter(param++, "LicenseManager GmbH")
                                .setParameter(param++, "Sesamstraße 123\nIrgendwo in Deutschland").executeUpdate();

                param = 1;
                em.createNativeQuery("INSERT INTO t_company (id, name, address) VALUES (?1, ?2, ?3)")
                                .setParameter(param++, COMPANY_ID_NOTABROTHEL)
                                .setParameter(param++, "Not a Brothel e. K.")
                                .setParameter(param++, COMPANY_ADDRESS_NOTABROTHEL).executeUpdate();

                param = 1;
                em.createNativeQuery("INSERT INTO t_company_department (id, name, company) VALUES (?1, ?2, ?3)")
                                .setParameter(param++, COMPANY_DEPARTMENT_ID_IT).setParameter(param++, "IT")
                                .setParameter(param++, COMPANY_ID_LICENSEMANAGER).executeUpdate();

                param = 1;
                em.createNativeQuery("INSERT INTO t_company_department (id, name, company) VALUES (?1, ?2, ?3)")
                                .setParameter(param++, COMPANY_DEPARTMENT_ID_ACCOUNTING)
                                .setParameter(param++, COMPANY_DEPARTMENT_NAME_ACCOUNTING)
                                .setParameter(param++, COMPANY_ID_NOTABROTHEL).executeUpdate();

                param = 1;
                em.createNativeQuery(
                                "INSERT INTO t_user (id, firstname, lastname, loginname, email, password_hash, verified, active, system_group, company_department) VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10)")
                                .setParameter(param++, USER_ID_MUSTERMANN).setParameter(param++, "Max")
                                .setParameter(param++, "Mustermann").setParameter(param++, "maexle")
                                .setParameter(param++, "mustermann@example.com").setParameter(param++, new byte[16])
                                .setParameter(param++, true).setParameter(param++, true)
                                .setParameter(param++, SYSTEM_GROUP_ID_ADMIN)
                                .setParameter(param++, COMPANY_DEPARTMENT_ID_IT).executeUpdate();

                param = 1;
                em.createNativeQuery(
                                "INSERT INTO t_user (id, firstname, lastname, loginname, email, password_hash, verified, active, system_group, company_department) VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10)")
                                .setParameter(param++, USER_ID_HANNELORE).setParameter(param++, "Greta")
                                .setParameter(param++, "Hannelore").setParameter(param++, "hanni")
                                .setParameter(param++, "hanni@notabrothel.com").setParameter(param++, new byte[16])
                                .setParameter(param++, true).setParameter(param++, true)
                                .setParameter(param++, SYSTEM_GROUP_ID_USER)
                                .setParameter(param++, COMPANY_DEPARTMENT_ID_ACCOUNTING).executeUpdate();

                et.commit();
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
                final User mustermann = UserDao.getInstance().getUser(USER_ID_MUSTERMANN);
                assertThat(mustermann, notNullValue());
        }

        @Test
        public void testFoundUserDataPopulated() {
                final User hannelore = UserDao.getInstance().getUser(USER_ID_HANNELORE);

                assertThat(hannelore.getFirstname(), not(emptyOrNullString()));
                assertThat(hannelore.getLastname(), not(emptyOrNullString()));
                assertThat(hannelore.getLoginname(), not(emptyOrNullString()));
        }

        @Test
        public void testFoundUserNestedDataPopulated() {
                final User hannelore = UserDao.getInstance().getUser(USER_ID_HANNELORE);

                assertThat(hannelore.getCompany().getAddress(), equalTo(COMPANY_ADDRESS_NOTABROTHEL));
                assertThat(hannelore.getCompanyDepartment().getName(), equalTo(COMPANY_DEPARTMENT_NAME_ACCOUNTING));
        }

        @Test
        public void testQueryAllUsers() {
                final List<User> users = UserDao.getInstance().getUsers();

                assertThat(users, not(empty()));
                assertThat(users, hasSize(2));
                assertThat(users.stream().map((u) -> u.getId()).collect(Collectors.toList()),
                                containsInAnyOrder((long) USER_ID_HANNELORE, (long) USER_ID_MUSTERMANN));
        }
}
