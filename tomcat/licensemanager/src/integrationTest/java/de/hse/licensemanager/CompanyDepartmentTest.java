package de.hse.licensemanager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.dao.CompanyDao;
import de.hse.licensemanager.dao.CompanyDepartmentDao;
import de.hse.licensemanager.dao.CredentialsDao;
import de.hse.licensemanager.model.Company;
import de.hse.licensemanager.model.CompanyDepartment;

public class CompanyDepartmentTest {

    private Client client;
    private String restURI;

    private static final ReadWriteLock RW_LOCK = new ReentrantReadWriteLock();
    private static final String BY_COMPANY_ENDPOINT = "/by-company";
    private static final String COUNT_ENDPOINT = "/count";
    private static final String MINE_ENDPOINT = "/mine";

    @Before
    public void setUp() {
        UnitTestSupport.initDatabase();

        client = ClientBuilder.newClient();
        restURI = IntegrationTestSupport.getRestURI() + "/company-departments";
    }

    @Test
    public void testGetOwnCompanyDepartment() {
        final Lock l = RW_LOCK.readLock();
        l.lock();
        try {
            final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                    UnitTestSupport.CREDENTIALS_LOGINNAME_MUSTERMANN,
                    UnitTestSupport.CREDENTIALS_PASSWORD_PLAIN_MUSTERMANN);

            final Invocation.Builder b = client.target(restURI + MINE_ENDPOINT).request(MediaType.APPLICATION_JSON);
            cookies.forEach(b::cookie);
            final Response response = b.buildGet().invoke();

            assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
            assertThat(response.readEntity(CompanyDepartment.class),
                    is(equalTo(CredentialsDao.getInstance()
                            .getCredentialsByLoginname(UnitTestSupport.CREDENTIALS_LOGINNAME_MUSTERMANN).getUser()
                            .getCompanyDepartment())));
        } finally {
            l.unlock();
        }
    }

    @Test
    public void testGetCount() {
        final Lock l = RW_LOCK.readLock();
        l.lock();
        try {
            final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                    UnitTestSupport.CREDENTIALS_LOGINNAME_MUSTERMANN,
                    UnitTestSupport.CREDENTIALS_PASSWORD_PLAIN_MUSTERMANN);

            final Invocation.Builder b = client.target(restURI + COUNT_ENDPOINT).request(MediaType.TEXT_PLAIN);
            cookies.forEach(b::cookie);
            final Response response = b.buildGet().invoke();

            assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
            assertThat(Integer.parseInt(response.readEntity(String.class)),
                    is(CompanyDepartmentDao.getInstance().getCompanyDepartments().size()));
        } finally {
            l.unlock();
        }
    }

    @Test
    public void testGetCompanyDepartmentsByCompany() {
        final Lock l = RW_LOCK.readLock();
        l.lock();
        try {
            final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                    UnitTestSupport.CREDENTIALS_LOGINNAME_MUSTERMANN,
                    UnitTestSupport.CREDENTIALS_PASSWORD_PLAIN_MUSTERMANN);

            final Company company = CompanyDao.getInstance().getCompany(UnitTestSupport.COMPANY_ID_NOTABROTHEL);

            final Invocation.Builder b = client
                    .target(restURI + String.format("%s/%d", BY_COMPANY_ENDPOINT, company.getId()))
                    .request(MediaType.APPLICATION_JSON);
            cookies.forEach(b::cookie);
            final Response response = b.buildGet().invoke();

            final List<CompanyDepartment> companyDepartments = response
                    .readEntity(new GenericType<List<CompanyDepartment>>() {
                    });

            assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
            assertThat(companyDepartments, hasSize(equalTo(company.getDepartments().size())));
            assertThat(companyDepartments, everyItem(in(company.getDepartments())));
        } finally {
            l.unlock();
        }
    }
}
