package de.hse.licensemanager.dao;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.BeforeClass;
import org.junit.Test;

import de.hse.licensemanager.PrepareTests;
import de.hse.licensemanager.model.ServiceContract;

public class ServiceContractDaoTest {

        private static final long SYSTEM_GROUP_ID_ADMIN;
        private static final long SYSTEM_GROUP_ID_USER;

        private static final long COMPANY_ID_LICENSEMANAGER;
        private static final long COMPANY_ID_NOTABROTHEL;
        private static final String COMPANY_ADDRESS_NOTABROTHEL = "Reeperbahn 31\nHamburg";

        private static final long COMPANY_DEPARTMENT_ID_IT;
        private static final long COMPANY_DEPARTMENT_ID_ACCOUNTING;
        private static final String COMPANY_DEPARTMENT_NAME_ACCOUNTING = "Accounting";

        private static final long USER_ID_MUSTERMANN;
        private static final long USER_ID_HANNELORE;

        private static final long SERVICE_CONTRACT_ID_A;
        private static final long SERVICE_CONTRACT_ID_B;

        private static final Date SERVICE_CONTRACT_START_A;
        private static final Date SERVICE_CONTRACT_END_A;

        private static final Date SERVICE_CONTRACT_START_B;
        private static final Date SERVICE_CONTRACT_END_B;

        private static final long PRODUCT_VARIANT_ID_MATLAB;
        private static final long PRODUCT_VARIANT_ID_WINDOWS;

        private static final String PRODUCT_VARIANT_VERSION_MATLAB = "r2020";

        private static final long LICENSE_ID_MATLAB;
        private static final long LICENSE_ID_WINDOWS;
        private static final String LICENSE_KEY_MATLAB;
        private static final String LICENSE_KEY_WINDOWS;
        private static final Date LICENSE_EXPIRATION_DATE_MATLAB;
        private static final Date LICENSE_EXPIRATION_DATE_WINDOWS;

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

                id = 1;
                SERVICE_CONTRACT_ID_A = id++;
                SERVICE_CONTRACT_ID_B = id++;

                final Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(0);
                cal.set(2020, 00, 00, 00, 00, 00);
                SERVICE_CONTRACT_START_A = cal.getTime();
                cal.add(Calendar.YEAR, 1);
                SERVICE_CONTRACT_END_A = cal.getTime();

                cal.set(2019, 12, 01, 00, 00, 00);
                SERVICE_CONTRACT_START_B = cal.getTime();
                cal.add(Calendar.YEAR, 2);
                cal.add(Calendar.MONTH, 3);
                SERVICE_CONTRACT_END_B = cal.getTime();

                id = 1;
                LICENSE_ID_MATLAB = id++;
                LICENSE_ID_WINDOWS = id++;

                cal.set(2022, 01, 01);
                LICENSE_EXPIRATION_DATE_MATLAB = cal.getTime();
                cal.add(Calendar.YEAR, 3);
                LICENSE_EXPIRATION_DATE_WINDOWS = cal.getTime();
                LICENSE_KEY_MATLAB = UUID.randomUUID().toString();
                LICENSE_KEY_WINDOWS = UUID.randomUUID().toString();

                id = 1;
                PRODUCT_VARIANT_ID_MATLAB = id++;
                PRODUCT_VARIANT_ID_WINDOWS = id++;
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

                param = 1;
                em.createNativeQuery(
                                "INSERT INTO t_service_contract (id, contractor, start, end) VALUES (?1, ?2, ?3, ?4)")
                                .setParameter(param++, SERVICE_CONTRACT_ID_A)
                                .setParameter(param++, COMPANY_ID_LICENSEMANAGER)
                                .setParameter(param++, SERVICE_CONTRACT_START_A)
                                .setParameter(param++, SERVICE_CONTRACT_END_A).executeUpdate();

                param = 1;
                em.createNativeQuery(
                                "INSERT INTO t_service_contract (id, contractor, start, end) VALUES (?1, ?2, ?3, ?4)")
                                .setParameter(param++, SERVICE_CONTRACT_ID_B)
                                .setParameter(param++, COMPANY_ID_NOTABROTHEL)
                                .setParameter(param++, SERVICE_CONTRACT_START_B)
                                .setParameter(param++, SERVICE_CONTRACT_END_B).executeUpdate();

                param = 1;
                em.createNativeQuery("INSERT INTO t_product_variant (id, product, version) VALUES (?1, ?2, ?3)")
                                .setParameter(param++, PRODUCT_VARIANT_ID_MATLAB).setParameter(param++, "Matlab")
                                .setParameter(param++, PRODUCT_VARIANT_VERSION_MATLAB).executeUpdate();

                param = 1;
                em.createNativeQuery("INSERT INTO t_product_variant (id, product, version) VALUES (?1, ?2, ?3)")
                                .setParameter(param++, PRODUCT_VARIANT_ID_WINDOWS).setParameter(param++, "Windows")
                                .setParameter(param++, "10").executeUpdate();

                param = 1;
                em.createNativeQuery(
                                "INSERT INTO t_license (id, service_contract, expiration_date, `key`, count, product_variant) VALUES (?1, ?2, ?3, ?4, ?5, ?6)")
                                .setParameter(param++, LICENSE_ID_MATLAB).setParameter(param++, SERVICE_CONTRACT_ID_A)
                                .setParameter(param++, LICENSE_EXPIRATION_DATE_MATLAB)
                                .setParameter(param++, LICENSE_KEY_MATLAB).setParameter(param++, 1)
                                .setParameter(param++, PRODUCT_VARIANT_ID_MATLAB).executeUpdate();

                param = 1;
                em.createNativeQuery(
                                "INSERT INTO t_license (id, service_contract, expiration_date, `key`, count, product_variant) VALUES (?1, ?2, ?3, ?4, ?5, ?6)")
                                .setParameter(param++, LICENSE_ID_WINDOWS).setParameter(param++, SERVICE_CONTRACT_ID_B)
                                .setParameter(param++, LICENSE_EXPIRATION_DATE_WINDOWS)
                                .setParameter(param++, LICENSE_KEY_WINDOWS).setParameter(param++, 5)
                                .setParameter(param++, PRODUCT_VARIANT_ID_WINDOWS).executeUpdate();

                et.commit();
        }

        @Test
        public void testInstanceAllocated() {
                assertThat(ServiceContractDao.getInstance(), notNullValue());
        }

        @Test
        public void testInstanceSingleton() {
                final ServiceContractDao a = ServiceContractDao.getInstance();
                final ServiceContractDao b = ServiceContractDao.getInstance();
                assertThat(a, sameInstance(b));
        }

        @Test
        public void testFindServiceContractById() {
                final ServiceContract a = ServiceContractDao.getInstance().getServiceContract(SERVICE_CONTRACT_ID_A);
                assertThat(a, notNullValue());
        }

        @Test
        public void testFoundServiceContractDataPopulated() {
                final ServiceContract contractA = ServiceContractDao.getInstance()
                                .getServiceContract(SERVICE_CONTRACT_ID_A);

                assertThat(contractA.getId(), equalTo(SERVICE_CONTRACT_ID_A));
                assertThat(contractA.getStart(), equalTo(SERVICE_CONTRACT_START_A));
                assertThat(contractA.getEnd(), equalTo(SERVICE_CONTRACT_END_A));
        }

        @Test
        public void testFoundServiceContractNestedDataPopulated() {
                final ServiceContract contractA = ServiceContractDao.getInstance()
                                .getServiceContract(SERVICE_CONTRACT_ID_B);

                assertThat(contractA.getContractor().getAddress(), equalTo(COMPANY_ADDRESS_NOTABROTHEL));
                assertThat(contractA.getLicenses().stream().map((l) -> l.getKey()).collect(Collectors.toList()),
                                containsInAnyOrder(LICENSE_KEY_WINDOWS));
        }

        @Test
        public void testQueryAllServiceContracts() {
                final List<ServiceContract> contracts = ServiceContractDao.getInstance().getServiceContracts();

                assertThat(contracts, not(empty()));
                assertThat(contracts, hasSize(2));
                assertThat(contracts.stream().map((c) -> c.getId()).collect(Collectors.toList()),
                                containsInAnyOrder((long) SERVICE_CONTRACT_ID_A, (long) SERVICE_CONTRACT_ID_B));
        }
}
