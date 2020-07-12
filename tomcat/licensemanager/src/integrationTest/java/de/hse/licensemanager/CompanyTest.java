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
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.dao.CompanyDao;
import de.hse.licensemanager.dao.CredentialsDao;
import de.hse.licensemanager.model.Company;

public class CompanyTest {

    private Client client;
    private String restURI;

    private static final ReadWriteLock RW_LOCK = new ReentrantReadWriteLock();
    private static final String COUNT_ENDPOINT = "/count";
    private static final String MINE_ENDPOINT = "/mine";

    @Before
    public void setUp() {
        UnitTestSupport.initDatabase();

        client = ClientBuilder.newClient();
        restURI = IntegrationTestSupport.getRestURI() + "/companies";
    }

    @Test
    public void testGetOwnCompany() {
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
            assertThat(response.readEntity(Company.class).getName(),
                    is(equalTo(CredentialsDao.getInstance()
                            .getCredentialsByLoginname(UnitTestSupport.CREDENTIALS_LOGINNAME_MUSTERMANN).getUser()
                            .getCompany().getName())));
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
                    is(CompanyDao.getInstance().getCompanies().size()));
        } finally {
            l.unlock();
        }
    }

    @Test
    public void testGetAllCompanies() {
        final Lock l = RW_LOCK.readLock();
        l.lock();
        try {
            final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                    UnitTestSupport.CREDENTIALS_LOGINNAME_MUSTERMANN,
                    UnitTestSupport.CREDENTIALS_PASSWORD_PLAIN_MUSTERMANN);

            final Invocation.Builder b = client.target(restURI).request(MediaType.APPLICATION_JSON);
            cookies.forEach(b::cookie);
            final Response response = b.buildGet().invoke();

            assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
            assertThat(response.readEntity(new GenericType<List<Company>>() {
            }), everyItem(in(CompanyDao.getInstance().getCompanies())));
        } finally {
            l.unlock();
        }
    }

    @Test
    public void testGetSpecificCompany() {
        final Lock l = RW_LOCK.readLock();
        l.lock();
        try {
            final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                    UnitTestSupport.CREDENTIALS_LOGINNAME_MUSTERMANN,
                    UnitTestSupport.CREDENTIALS_PASSWORD_PLAIN_MUSTERMANN);

            final Company company = CompanyDao.getInstance().getCompanies().stream().findFirst().get();

            final Invocation.Builder b = client.target(restURI + String.format("/%d", company.getId()))
                    .request(MediaType.APPLICATION_JSON);
            cookies.forEach(b::cookie);
            final Response response = b.buildGet().invoke();

            assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
            assertThat(response.readEntity(Company.class).getId(), is(equalTo(company.getId())));
        } finally {
            l.unlock();
        }
    }

    @Test
    public void testPutModifiedCompany() {
        final Lock l = RW_LOCK.writeLock();
        l.lock();
        try {
            final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                    UnitTestSupport.CREDENTIALS_LOGINNAME_MUSTERMANN,
                    UnitTestSupport.CREDENTIALS_PASSWORD_PLAIN_MUSTERMANN);

            final Company originalCompany = CompanyDao.getInstance().getCompanies().stream().findFirst().get();
            final Company modifiedCompany = new Company(originalCompany.getId(), originalCompany.getName(),
                    "Some other address");

            final Invocation.Builder b = client.target(restURI + String.format("/%d", modifiedCompany.getId()))
                    .request(MediaType.APPLICATION_JSON);
            cookies.forEach(b::cookie);
            final Response response = b.buildPut(Entity.json(modifiedCompany)).invoke();

            CompanyDao.getInstance().refresh(originalCompany);

            assertThat(response.getStatus(), is(Response.Status.CREATED.getStatusCode()));
            assertThat(CompanyDao.getInstance().getCompany(originalCompany.getId()).getAddress(),
                    is(equalTo(modifiedCompany.getAddress())));
        } finally {
            l.unlock();
        }
    }
}
