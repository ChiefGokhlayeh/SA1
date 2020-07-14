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
import de.hse.licensemanager.dao.ServiceContractDao;
import de.hse.licensemanager.dao.UserDao;
import de.hse.licensemanager.model.Company;
import de.hse.licensemanager.model.ServiceContract;
import de.hse.licensemanager.model.User;

public class ServiceContractTest {

    private Client client;
    private String restURI;

    private static final ReadWriteLock RW_LOCK = new ReentrantReadWriteLock();
    private static final String BY_CONTRACTOR_ENDPOINT = "/by-contractor";
    private static final String BY_USER_ENDPOINT = "/by-user";
    private static final String COUNT_ENDPOINT = "/count";
    private static final String MINE_ENDPOINT = "/mine";

    @Before
    public void setUp() {
        UnitTestSupport.initDatabase();

        client = ClientBuilder.newClient();
        restURI = IntegrationTestSupport.getRestURI() + "/service-contracts";
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

            assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
            final int count = Integer.parseInt(response.readEntity(String.class));
            assertThat(count, equalTo(ServiceContractDao.getInstance().getServiceContracts().size()));
        } finally {
            l.unlock();
        }
    }

    @Test
    public void testGetAllServiceContracts() {
        final Lock l = RW_LOCK.readLock();
        l.lock();
        try {
            final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                    UnitTestSupport.CREDENTIALS_LOGINNAME_MUSTERMANN,
                    UnitTestSupport.CREDENTIALS_PASSWORD_PLAIN_MUSTERMANN);

            final Invocation.Builder b = client.target(restURI).request(MediaType.APPLICATION_JSON);
            cookies.forEach(b::cookie);
            final Response response = b.buildGet().invoke();

            assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
            final List<ServiceContract> serviceContracts = response
                    .readEntity(new GenericType<List<ServiceContract>>() {
                    });
            assertThat(serviceContracts, everyItem(in(ServiceContractDao.getInstance().getServiceContracts())));
        } finally {
            l.unlock();
        }
    }

    @Test
    public void testGetServiceContractsByContractor() {
        final Lock l = RW_LOCK.readLock();
        l.lock();
        try {
            final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                    UnitTestSupport.CREDENTIALS_LOGINNAME_MUSTERMANN,
                    UnitTestSupport.CREDENTIALS_PASSWORD_PLAIN_MUSTERMANN);

            final Company contractor = CompanyDao.getInstance().getCompany(UnitTestSupport.COMPANY_ID_LICENSEMANAGER);

            final Invocation.Builder b = client
                    .target(restURI + String.format("%s/%d", BY_CONTRACTOR_ENDPOINT, contractor.getId()))
                    .request(MediaType.APPLICATION_JSON);
            cookies.forEach(b::cookie);
            final Response response = b.buildGet().invoke();

            assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
            final List<ServiceContract> serviceContracts = response
                    .readEntity(new GenericType<List<ServiceContract>>() {
                    });
            assertThat(serviceContracts,
                    everyItem(in(ServiceContractDao.getInstance().getServiceContractsByContractor(contractor))));
        } finally {
            l.unlock();
        }
    }

    @Test
    public void testGetServiceContractsByUser() {
        final Lock l = RW_LOCK.readLock();
        l.lock();
        try {
            final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                    UnitTestSupport.CREDENTIALS_LOGINNAME_MUSTERMANN,
                    UnitTestSupport.CREDENTIALS_PASSWORD_PLAIN_MUSTERMANN);

            final User user = UserDao.getInstance().getUser(UnitTestSupport.USER_ID_HANNELORE);

            final Invocation.Builder b = client.target(restURI + String.format("%s/%d", BY_USER_ENDPOINT, user.getId()))
                    .request(MediaType.APPLICATION_JSON);
            cookies.forEach(b::cookie);
            final Response response = b.buildGet().invoke();

            assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
            final List<ServiceContract> serviceContracts = response
                    .readEntity(new GenericType<List<ServiceContract>>() {
                    });
            assertThat(serviceContracts,
                    everyItem(in(ServiceContractDao.getInstance().getServiceContractsByUser(user))));
        } finally {
            l.unlock();
        }
    }

    @Test
    public void testGetMyServiceContracts() {
        final Lock l = RW_LOCK.readLock();
        l.lock();
        try {
            final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                    UnitTestSupport.CREDENTIALS_LOGINNAME_HANNELORE,
                    UnitTestSupport.CREDENTIALS_PASSWORD_PLAIN_HANNELORE);

            final Invocation.Builder b = client.target(restURI + MINE_ENDPOINT).request(MediaType.APPLICATION_JSON);
            cookies.forEach(b::cookie);
            final Response response = b.buildGet().invoke();

            assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
            final List<ServiceContract> serviceContracts = response
                    .readEntity(new GenericType<List<ServiceContract>>() {
                    });
            assertThat(serviceContracts, everyItem(
                    in(ServiceContractDao.getInstance().getServiceContractsByUser(UnitTestSupport.USER_ID_HANNELORE))));
        } finally {
            l.unlock();
        }
    }

    @Test
    public void testCreateNewServiceContract() {
        final Lock l = RW_LOCK.writeLock();
        l.lock();
        try {
            final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                    UnitTestSupport.CREDENTIALS_LOGINNAME_MUSTERMANN,
                    UnitTestSupport.CREDENTIALS_PASSWORD_PLAIN_MUSTERMANN);

            final ServiceContract newServiceContract = new ServiceContract(
                    CompanyDao.getInstance().getCompany(UnitTestSupport.COMPANY_ID_NOTABROTHEL));

            final Invocation.Builder b = client.target(restURI).request(MediaType.APPLICATION_JSON);
            cookies.forEach(b::cookie);
            final Response response = b.buildPost(Entity.json(newServiceContract)).invoke();

            assertThat(response.getStatus(), equalTo(Response.Status.CREATED.getStatusCode()));
            final ServiceContract createdServiceContract = response.readEntity(ServiceContract.class);
            assertThat(createdServiceContract.getContractor(), equalTo(newServiceContract.getContractor()));
            assertThat(createdServiceContract, in(ServiceContractDao.getInstance()
                    .getServiceContractsByContractor(newServiceContract.getContractor())));
        } finally {
            l.unlock();
        }
    }
}
