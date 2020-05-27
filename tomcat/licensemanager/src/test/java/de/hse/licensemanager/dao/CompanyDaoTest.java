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
import de.hse.licensemanager.model.Company;

public class CompanyDaoTest {

    private static final int COMPANY_ID_LICENSEMANAGER;
    private static final int COMPANY_ID_NOTABROTHEL;
    private static final String COMPANY_NAME_NOTABROTHEL = "Not a Brothel e. K.";
    private static final String COMPANY_ADDRESS_NOTABROTHEL = "Reeperbahn 31\nHamburg";

    private static final int COMPANY_DEPARTMENT_ID_IT;
    private static final int COMPANY_DEPARTMENT_ID_ACCOUNTING;
    private static final String COMPANY_DEPARTMENT_NAME_ACCOUNTING = "Accounting";

    static {
        int id = 1;
        COMPANY_ID_LICENSEMANAGER = id++;
        COMPANY_ID_NOTABROTHEL = id++;

        id = 1;
        COMPANY_DEPARTMENT_ID_IT = id++;
        COMPANY_DEPARTMENT_ID_ACCOUNTING = id++;
    }

    @BeforeClass
    public static void SetUpBeforeClass() {
        PrepareTests.initDatabase();

        final EntityManager em = DaoManager.getEntityManager();

        final EntityTransaction et = em.getTransaction();
        et.begin();

        int param = 1;
        em.createNativeQuery("INSERT INTO t_company (id, name, address) VALUES (?1, ?2, ?3)")
                .setParameter(param++, COMPANY_ID_LICENSEMANAGER).setParameter(param++, "LicenseManager GmbH")
                .setParameter(param++, "Sesamstraße 123\nIrgendwo in Deutschland").executeUpdate();

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

        et.commit();
    }

    @Test
    public void testInstanceAllocated() {
        assertThat(CompanyDao.getInstance(), notNullValue());
    }

    @Test
    public void testInstanceSingleton() {
        final CompanyDao a = CompanyDao.getInstance();
        final CompanyDao b = CompanyDao.getInstance();
        assertThat(a, sameInstance(b));
    }

    @Test
    public void testFindCompanyById() {
        final Company licensemanager = CompanyDao.getInstance().getCompany(COMPANY_ID_LICENSEMANAGER);
        assertThat(licensemanager, notNullValue());
    }

    @Test
    public void testFoundCompanyDataPopulated() {
        final Company notABrothel = CompanyDao.getInstance().getCompany(COMPANY_ID_NOTABROTHEL);

        assertThat(notABrothel.getName(), equalTo(COMPANY_NAME_NOTABROTHEL));
        assertThat(notABrothel.getAddress(), equalTo(COMPANY_ADDRESS_NOTABROTHEL));
    }

    @Test
    public void testFoundCompanyNestedDataPopulated() {
        final Company notABrothel = CompanyDao.getInstance().getCompany(COMPANY_ID_NOTABROTHEL);

        assertThat(notABrothel.getDepartments().stream().map((d) -> d.getName()).collect(Collectors.toList()),
                containsInAnyOrder(COMPANY_DEPARTMENT_NAME_ACCOUNTING));
    }

    @Test
    public void testQueryAllCompanies() {
        final List<Company> companies = CompanyDao.getInstance().getCompanies();

        assertThat(companies, not(empty()));
        assertThat(companies, hasSize(2));
        assertThat(companies.stream().map((c) -> c.getId()).collect(Collectors.toList()),
                containsInAnyOrder((long) COMPANY_ID_LICENSEMANAGER, (long) COMPANY_ID_NOTABROTHEL));
    }
}
