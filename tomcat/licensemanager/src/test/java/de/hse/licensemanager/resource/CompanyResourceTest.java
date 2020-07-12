package de.hse.licensemanager.resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.UnitTestSupport;
import de.hse.licensemanager.dao.CompanyDao;
import de.hse.licensemanager.dao.CompanyDepartmentDao;
import de.hse.licensemanager.model.Company;
import de.hse.licensemanager.model.CompanyDepartment;

public class CompanyResourceTest {
    private Company company;
    private CompanyDepartment department;
    private CompanyResource companyResource;
    private UriInfo uriInfo;

    @Before
    public void setUp() {
        UnitTestSupport.initDatabase();

        company = new Company("Test Company", "Some Address");
        department = new CompanyDepartment("Some New Department", company);
        company.getDepartments().add(department);

        CompanyDao.getInstance().save(company);

        uriInfo = mock(UriInfo.class);

        companyResource = new CompanyResource(uriInfo, company.getId());
    }

    @Test
    public void testGet() {
        final Response response = companyResource.get();

        assertThat(response, notNullValue());
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(response.getEntity(), equalTo(company));
    }

    @Test
    public void testDelete() {
        assertThat(company, in(CompanyDao.getInstance().getCompanies()));
        assertThat(department, in(CompanyDepartmentDao.getInstance().getCompanyDepartments()));

        companyResource.delete();

        assertThat(company, not(in(CompanyDao.getInstance().getCompanies())));
        assertThat(department, not(in(CompanyDepartmentDao.getInstance().getCompanyDepartments())));
    }

    @Test
    public void testPut() {
        final String expectedAddress = "Some Other address";

        final Company modifiedCompany = new Company();
        modifiedCompany.setId(company.getId());
        modifiedCompany.setName(company.getName());
        modifiedCompany.setAddress(expectedAddress);

        companyResource.put(modifiedCompany);

        assertThat(CompanyDao.getInstance().getCompany(company.getId()).getAddress(), is(equalTo(expectedAddress)));
    }
}
