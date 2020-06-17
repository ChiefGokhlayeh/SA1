package de.hse.licensemanager.dao;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.PrepareTests;
import de.hse.licensemanager.model.Company;
import de.hse.licensemanager.model.CompanyDepartment;

public class CompanyDaoTest {

    @Before
    public void setupBeforeTest() {
        PrepareTests.initDatabase();
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
        final Company licensemanager = CompanyDao.getInstance().getCompany(PrepareTests.COMPANY_ID_LICENSEMANAGER);
        assertThat(licensemanager, notNullValue());
    }

    @Test
    public void testFoundCompanyDataPopulated() {
        final Company notABrothel = CompanyDao.getInstance().getCompany(PrepareTests.COMPANY_ID_NOTABROTHEL);

        assertThat(notABrothel.getName(), equalTo(PrepareTests.COMPANY_NAME_NOTABROTHEL));
        assertThat(notABrothel.getAddress(), equalTo(PrepareTests.COMPANY_ADDRESS_NOTABROTHEL));
    }

    @Test
    public void testFoundCompanyNestedDataPopulated() {
        final Company notABrothel = CompanyDao.getInstance().getCompany(PrepareTests.COMPANY_ID_NOTABROTHEL);

        assertThat(PrepareTests.COMPANY_DEPARTMENT_NAME_ACCOUNTING,
                equalTo(notABrothel.getDepartments().get(0).getName()));
        assertThat(notABrothel.getDepartments().stream().map((d) -> d.getName()).collect(Collectors.toList()),
                containsInAnyOrder(PrepareTests.COMPANY_DEPARTMENT_NAME_ACCOUNTING));
    }

    @Test
    public void testQueryAllCompanies() {
        final List<Company> companies = CompanyDao.getInstance().getCompanies();

        assertThat(companies, not(empty()));
        assertThat(companies, hasSize(2));
        assertThat(companies.stream().map((c) -> c.getId()).collect(Collectors.toList()), containsInAnyOrder(
                (long) PrepareTests.COMPANY_ID_LICENSEMANAGER, (long) PrepareTests.COMPANY_ID_NOTABROTHEL));
    }

    @Test
    public void testSaveSimple() {
        final Company company = new Company();
        company.setName("Test Firma");
        company.setAddress("Langestraße 123\n123456 Bielefeld");

        CompanyDao.getInstance().save(company);

        final List<Company> companies = CompanyDao.getInstance().getCompanies();

        assertThat(company.getId(), is(in(companies.stream().map((c) -> c.getId()).collect(Collectors.toList()))));
    }

    @Test
    public void testSaveNewDepartment() {
        final Company company = new Company();

        final CompanyDepartment department = new CompanyDepartment();

        department.setName("Test Abteilung");

        company.setName("Test Firma Mit Abteilung");
        company.setAddress("Langestraße 123\n123456 Bielefeld");
        company.addDepartment(department);

        CompanyDao.getInstance().save(company);

        final List<Company> companies = CompanyDao.getInstance().getCompanies();

        assertThat(company.getId(), is(in(companies.stream().map((c) -> c.getId()).collect(Collectors.toList()))));
    }

    @Test
    public void testSaveNameChange() {
        final Company company = CompanyDao.getInstance().getCompany(PrepareTests.COMPANY_ID_LICENSEMANAGER);

        company.setName("LicenseManager 2 GmbH");

        CompanyDao.getInstance().save(company);

        final List<Company> companies = CompanyDao.getInstance().getCompanies();

        assertThat(company.getName(), is(in(companies.stream().map((c) -> c.getName()).collect(Collectors.toList()))));
    }

    @Test
    public void testAddDepartment() {
        final CompanyDepartment department = new CompanyDepartment();
        department.setName("Test Department");
        final Company company = CompanyDao.getInstance().getCompany(PrepareTests.COMPANY_ID_LICENSEMANAGER);
        final CompanyDepartment[] currentDepartments = company.getDepartments()
                .toArray(new CompanyDepartment[company.getDepartments().size()]);

        company.addDepartment(department);

        CompanyDao.getInstance().save(company);

        final List<CompanyDepartment> departments = CompanyDepartmentDao.getInstance().getCompanyDepartments();
        assertThat(department, is(in(departments)));
        assertThat(company.getDepartments(), hasItems(currentDepartments));
        assertThat(department, is(in(company.getDepartments())));
    }
}
