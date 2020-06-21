package de.hse.licensemanager;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.Test;

import de.hse.licensemanager.dao.DaoManager;
import de.hse.licensemanager.model.Credentials;

public class PrepareTests {

    public static final long SYSTEM_GROUP_ID_ADMIN;
    public static final long SYSTEM_GROUP_ID_USER;
    public static final String SYSTEM_GROUP_DISPLAYNAME_ADMIN = "admin";

    public static final long COMPANY_ID_LICENSEMANAGER;
    public static final long COMPANY_ID_NOTABROTHEL;
    public static final String COMPANY_NAME_LICENSEMANAGER = "LicenseManager GmbH";
    public static final String COMPANY_NAME_NOTABROTHEL = "Not a Brothel e. K.";
    public static final String COMPANY_ADDRESS_LICENSEMANAGER = "Sesamstraße 123\nIrgendwo in Deutschland";
    public static final String COMPANY_ADDRESS_NOTABROTHEL = "Reeperbahn 31\nHamburg";

    public static final long COMPANY_DEPARTMENT_ID_IT;
    public static final long COMPANY_DEPARTMENT_ID_ACCOUNTING;
    public static final String COMPANY_DEPARTMENT_NAME_ACCOUNTING = "Accounting";

    public static final long USER_ID_MUSTERMANN;
    public static final long USER_ID_HANNELORE;
    public static final long USER_ID_DELETEME;

    public static final long SERVICE_CONTRACT_ID_A;
    public static final long SERVICE_CONTRACT_ID_B;

    public static final Date SERVICE_CONTRACT_START_A;
    public static final Date SERVICE_CONTRACT_END_A;

    public static final Date SERVICE_CONTRACT_START_B;
    public static final Date SERVICE_CONTRACT_END_B;

    public static final long PRODUCT_VARIANT_ID_MATLAB;
    public static final long PRODUCT_VARIANT_ID_WINDOWS;
    public static final String PRODUCT_VARIANT_PRODUCT_MATLAB = "Matlab";
    public static final String PRODUCT_VARIANT_PRODUCT_WINDOWS = "Windows";
    public static final String PRODUCT_VARIANT_VERSION_MATLAB = "r2020";
    public static final String PRODUCT_VARIANT_VERSION_WINDOWS = "10";

    public static final long LICENSE_ID_MATLAB;
    public static final long LICENSE_ID_WINDOWS;
    public static final String LICENSE_KEY_MATLAB;
    public static final String LICENSE_KEY_WINDOWS;
    public static final Date LICENSE_EXPIRATION_DATE_MATLAB;
    public static final Date LICENSE_EXPIRATION_DATE_WINDOWS;

    public static final long IP_MAPPING_ID_HOST1;
    public static final String IP_MAPPING_IP_ADDRESS_HOST1 = "123";

    public static final long CREDENTIALS_ID_MUSTERMANN;
    public static final long CREDENTIALS_ID_HANNELORE;
    public static final long CREDENTIALS_ID_DELETEME;

    public static final String CREDENTIALS_LOGINNAME_MUSTERMANN = "maexle";
    public static final String CREDENTIALS_LOGINNAME_HANNELORE = "hanni";
    public static final String CREDENTIALS_LOGINNAME_DELETEME = "hansi";

    public static final String CREDENTIALS_PASSWORD_PLAIN_MUSTERMANN = "test password 123";
    public static final byte[] CREDENTIALS_PASSWORD_HASH_MUSTERMANN;

    public static final byte[] CREDENTIALS_PASSWORD_SALT_MUSTERMANN = Credentials.generateSalt();

    public static final int CREDENTIALS_PASSWORD_ITERATIONS_MUSTERMANN = Credentials.ITERATIONS;

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
        USER_ID_DELETEME = id++;

        id = 1;
        SERVICE_CONTRACT_ID_A = id++;
        SERVICE_CONTRACT_ID_B = id++;

        id = 1;
        IP_MAPPING_ID_HOST1 = id++;

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

        id = 1;
        CREDENTIALS_ID_MUSTERMANN = id++;
        CREDENTIALS_ID_HANNELORE = id++;
        CREDENTIALS_ID_DELETEME = id++;

        CREDENTIALS_PASSWORD_HASH_MUSTERMANN = Credentials.generateSecret(CREDENTIALS_PASSWORD_PLAIN_MUSTERMANN,
                CREDENTIALS_PASSWORD_SALT_MUSTERMANN, CREDENTIALS_PASSWORD_ITERATIONS_MUSTERMANN);
    }

    @Test
    public void clearDatabase() {
        initDatabase();
    }

    public static void initDatabase() {
        final EntityManager em = DaoManager.getEntityManager();

        final EntityTransaction et = em.getTransaction();
        et.begin();

        em.createNativeQuery("DELETE FROM t_service_group").executeUpdate();
        em.createNativeQuery("DELETE FROM t_ip_mapping").executeUpdate();
        em.createNativeQuery("DELETE FROM t_license").executeUpdate();
        em.createNativeQuery("DELETE FROM t_product_variant").executeUpdate();
        em.createNativeQuery("DELETE FROM t_service_contract").executeUpdate();
        em.createNativeQuery("DELETE FROM t_user").executeUpdate();
        em.createNativeQuery("DELETE FROM t_company_department").executeUpdate();
        em.createNativeQuery("DELETE FROM t_company").executeUpdate();
        em.createNativeQuery("DELETE FROM t_system_group").executeUpdate();
        em.createNativeQuery("DELETE FROM t_credentials").executeUpdate();

        int param = 1;
        em.createNativeQuery("INSERT INTO t_system_group (id, displayname) VALUES (?1, ?2)")
                .setParameter(param++, SYSTEM_GROUP_ID_ADMIN).setParameter(param++, SYSTEM_GROUP_DISPLAYNAME_ADMIN)
                .executeUpdate();

        param = 1;
        em.createNativeQuery("INSERT INTO t_system_group (id, displayname) VALUES (?1, ?2)")
                .setParameter(param++, SYSTEM_GROUP_ID_USER).setParameter(param++, "user").executeUpdate();

        param = 1;
        em.createNativeQuery("INSERT INTO t_company (id, name, address) VALUES (?1, ?2, ?3)")
                .setParameter(param++, COMPANY_ID_LICENSEMANAGER).setParameter(param++, COMPANY_NAME_LICENSEMANAGER)
                .setParameter(param++, COMPANY_ADDRESS_LICENSEMANAGER).executeUpdate();

        param = 1;
        em.createNativeQuery("INSERT INTO t_company (id, name, address) VALUES (?1, ?2, ?3)")
                .setParameter(param++, COMPANY_ID_NOTABROTHEL).setParameter(param++, COMPANY_NAME_NOTABROTHEL)
                .setParameter(param++, COMPANY_ADDRESS_NOTABROTHEL).executeUpdate();

        param = 1;
        em.createNativeQuery("INSERT INTO t_company_department (id, name, company) VALUES (?1, ?2, ?3)")
                .setParameter(param++, COMPANY_DEPARTMENT_ID_IT).setParameter(param++, "IT")
                .setParameter(param++, COMPANY_ID_LICENSEMANAGER).executeUpdate();

        param = 1;
        em.createNativeQuery("INSERT INTO t_company_department (id, name, company) VALUES (?1, ?2, ?3)")
                .setParameter(param++, COMPANY_DEPARTMENT_ID_ACCOUNTING)
                .setParameter(param++, COMPANY_DEPARTMENT_NAME_ACCOUNTING).setParameter(param++, COMPANY_ID_NOTABROTHEL)
                .executeUpdate();

        param = 1;
        em.createNativeQuery(
                "INSERT INTO t_credentials (id, loginname, password_hash, password_salt, password_iterations) VALUES (?1, ?2, ?3, ?4, ?5)")
                .setParameter(param++, CREDENTIALS_ID_MUSTERMANN)
                .setParameter(param++, CREDENTIALS_LOGINNAME_MUSTERMANN)
                .setParameter(param++, CREDENTIALS_PASSWORD_HASH_MUSTERMANN)
                .setParameter(param++, CREDENTIALS_PASSWORD_SALT_MUSTERMANN)
                .setParameter(param++, CREDENTIALS_PASSWORD_ITERATIONS_MUSTERMANN).executeUpdate();

        param = 1;
        em.createNativeQuery(
                "INSERT INTO t_credentials (id, loginname, password_hash, password_salt, password_iterations) VALUES (?1, ?2, ?3, ?4, ?5)")
                .setParameter(param++, CREDENTIALS_ID_HANNELORE).setParameter(param++, CREDENTIALS_LOGINNAME_HANNELORE)
                .setParameter(param++, new byte[Credentials.HASH_LENGTH])
                .setParameter(param++, new byte[Credentials.SALT_LENGTH]).setParameter(param++, Credentials.ITERATIONS)
                .executeUpdate();
        param = 1;
        em.createNativeQuery(
                "INSERT INTO t_credentials (id, loginname, password_hash, password_salt, password_iterations) VALUES (?1, ?2, ?3, ?4, ?5)")
                .setParameter(param++, CREDENTIALS_ID_DELETEME).setParameter(param++, CREDENTIALS_LOGINNAME_DELETEME)
                .setParameter(param++, new byte[Credentials.HASH_LENGTH])
                .setParameter(param++, new byte[Credentials.SALT_LENGTH])
                .setParameter(param++, Credentials.ITERATIONS - 1000).executeUpdate();

        param = 1;
        em.createNativeQuery(
                "INSERT INTO t_user (id, firstname, lastname, email, verified, active, system_group, company_department, credentials) VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9)")
                .setParameter(param++, USER_ID_MUSTERMANN).setParameter(param++, "Max")
                .setParameter(param++, "Mustermann").setParameter(param++, "mustermann@example.com")
                .setParameter(param++, true).setParameter(param++, true).setParameter(param++, SYSTEM_GROUP_ID_ADMIN)
                .setParameter(param++, COMPANY_DEPARTMENT_ID_IT).setParameter(param++, CREDENTIALS_ID_MUSTERMANN)
                .executeUpdate();

        param = 1;
        em.createNativeQuery(
                "INSERT INTO t_user (id, firstname, lastname, email, verified, active, system_group, company_department, credentials) VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9)")
                .setParameter(param++, USER_ID_HANNELORE).setParameter(param++, "Greta")
                .setParameter(param++, "Hannelore").setParameter(param++, "hanni@notabrothel.com")
                .setParameter(param++, true).setParameter(param++, true).setParameter(param++, SYSTEM_GROUP_ID_USER)
                .setParameter(param++, COMPANY_DEPARTMENT_ID_ACCOUNTING).setParameter(param++, CREDENTIALS_ID_HANNELORE)
                .executeUpdate();

        param = 1;
        em.createNativeQuery(
                "INSERT INTO t_user (id, firstname, lastname, email, verified, active, system_group, company_department, credentials) VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9)")
                .setParameter(param++, USER_ID_DELETEME).setParameter(param++, "Hans").setParameter(param++, "Deleteme")
                .setParameter(param++, "hans@licensemanager.com").setParameter(param++, true)
                .setParameter(param++, true).setParameter(param++, SYSTEM_GROUP_ID_USER)
                .setParameter(param++, COMPANY_DEPARTMENT_ID_IT).setParameter(param++, CREDENTIALS_ID_DELETEME)
                .executeUpdate();

        param = 1;
        em.createNativeQuery("INSERT INTO t_service_contract (id, contractor, start, end) VALUES (?1, ?2, ?3, ?4)")
                .setParameter(param++, SERVICE_CONTRACT_ID_A).setParameter(param++, COMPANY_ID_LICENSEMANAGER)
                .setParameter(param++, SERVICE_CONTRACT_START_A).setParameter(param++, SERVICE_CONTRACT_END_A)
                .executeUpdate();

        param = 1;
        em.createNativeQuery("INSERT INTO t_service_contract (id, contractor, start, end) VALUES (?1, ?2, ?3, ?4)")
                .setParameter(param++, SERVICE_CONTRACT_ID_B).setParameter(param++, COMPANY_ID_NOTABROTHEL)
                .setParameter(param++, SERVICE_CONTRACT_START_B).setParameter(param++, SERVICE_CONTRACT_END_B)
                .executeUpdate();

        param = 1;
        em.createNativeQuery("INSERT INTO t_product_variant (id, product, version) VALUES (?1, ?2, ?3)")
                .setParameter(param++, PRODUCT_VARIANT_ID_MATLAB).setParameter(param++, PRODUCT_VARIANT_PRODUCT_MATLAB)
                .setParameter(param++, PRODUCT_VARIANT_VERSION_MATLAB).executeUpdate();

        param = 1;
        em.createNativeQuery("INSERT INTO t_product_variant (id, product, version) VALUES (?1, ?2, ?3)")
                .setParameter(param++, PRODUCT_VARIANT_ID_WINDOWS)
                .setParameter(param++, PRODUCT_VARIANT_PRODUCT_WINDOWS)
                .setParameter(param++, PRODUCT_VARIANT_VERSION_WINDOWS).executeUpdate();

        param = 1;
        em.createNativeQuery(
                "INSERT INTO t_license (id, service_contract, expiration_date, `key`, count, product_variant) VALUES (?1, ?2, ?3, ?4, ?5, ?6)")
                .setParameter(param++, LICENSE_ID_MATLAB).setParameter(param++, SERVICE_CONTRACT_ID_A)
                .setParameter(param++, LICENSE_EXPIRATION_DATE_MATLAB).setParameter(param++, LICENSE_KEY_MATLAB)
                .setParameter(param++, 1).setParameter(param++, PRODUCT_VARIANT_ID_MATLAB).executeUpdate();

        param = 1;
        em.createNativeQuery(
                "INSERT INTO t_license (id, service_contract, expiration_date, `key`, count, product_variant) VALUES (?1, ?2, ?3, ?4, ?5, ?6)")
                .setParameter(param++, LICENSE_ID_WINDOWS).setParameter(param++, SERVICE_CONTRACT_ID_B)
                .setParameter(param++, LICENSE_EXPIRATION_DATE_WINDOWS).setParameter(param++, LICENSE_KEY_WINDOWS)
                .setParameter(param++, 5).setParameter(param++, PRODUCT_VARIANT_ID_WINDOWS).executeUpdate();

        param = 1;
        em.createNativeQuery("INSERT INTO t_ip_mapping (id, license, ip_address) VALUES (?1, ?2, ?3)")
                .setParameter(param++, IP_MAPPING_ID_HOST1).setParameter(param++, LICENSE_ID_MATLAB)
                .setParameter(param++, IP_MAPPING_IP_ADDRESS_HOST1).executeUpdate();

        param = 1;
        em.createNativeQuery("INSERT INTO t_service_group (service_contract, `user`) VALUES (?1, ?2)")
                .setParameter(param++, SERVICE_CONTRACT_ID_A).setParameter(param++, USER_ID_HANNELORE).executeUpdate();

        et.commit();
    }
}
