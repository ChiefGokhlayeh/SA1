package de.hse.licensemanager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
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

import de.hse.licensemanager.dao.LicenseDao;
import de.hse.licensemanager.dao.ProductVariantDao;
import de.hse.licensemanager.dao.ServiceContractDao;
import de.hse.licensemanager.dao.UserDao;
import de.hse.licensemanager.model.License;
import de.hse.licensemanager.model.ServiceContract;
import de.hse.licensemanager.model.User;

public class LicenseTest {

    private static final ReadWriteLock RW_LOCK = new ReentrantReadWriteLock();

    private Client client;
    private String restURI;

    private static final String BY_SERVICE_CONTRACT_ENDPOINT = "/by-service-contract";
    private static final String BY_USER_ENDPOINT = "/by-user";
    private static final String COUNT_ENDPOINT = "/count";
    private static final String MINE_ENDPOINT = "/mine";

    @Before
    public void setUp() {
        UnitTestSupport.initDatabase();

        client = ClientBuilder.newClient();
        restURI = IntegrationTestSupport.getRestURI() + "/licenses";
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
            assertThat(count, equalTo(LicenseDao.getInstance().getLicenses().size()));
        } finally {
            l.unlock();
        }
    }

    @Test
    public void testGetAllLicenses() {
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
            final List<License> licenses = response.readEntity(new GenericType<List<License>>() {
            });
            assertThat(licenses, hasSize(LicenseDao.getInstance().getLicenses().size()));
            assertThat(licenses, everyItem(in(LicenseDao.getInstance().getLicenses())));
        } finally {
            l.unlock();
        }
    }

    @Test
    public void testGetLicensesByServiceContract() {
        final Lock l = RW_LOCK.readLock();
        l.lock();
        try {
            final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                    UnitTestSupport.CREDENTIALS_LOGINNAME_MUSTERMANN,
                    UnitTestSupport.CREDENTIALS_PASSWORD_PLAIN_MUSTERMANN);

            final ServiceContract serviceContract = ServiceContractDao.getInstance()
                    .getServiceContract(UnitTestSupport.SERVICE_CONTRACT_ID_A);

            final Invocation.Builder b = client
                    .target(restURI + String.format("%s/%d", BY_SERVICE_CONTRACT_ENDPOINT, serviceContract.getId()))
                    .request(MediaType.APPLICATION_JSON);
            cookies.forEach(b::cookie);
            final Response response = b.buildGet().invoke();

            assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
            final List<License> licenses = response.readEntity(new GenericType<List<License>>() {
            });
            assertThat(licenses,
                    hasSize(LicenseDao.getInstance().getLicensesByServiceContract(serviceContract).size()));
            assertThat(licenses, everyItem(in(LicenseDao.getInstance().getLicensesByServiceContract(serviceContract))));
        } finally {
            l.unlock();
        }
    }

    @Test
    public void testGetLicensesByUser() {
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
            final List<License> licenses = response.readEntity(new GenericType<List<License>>() {
            });
            assertThat(licenses, hasSize(LicenseDao.getInstance().getLicensesByUser(user).size()));
            assertThat(licenses, everyItem(in(LicenseDao.getInstance().getLicensesByUser(user))));
        } finally {
            l.unlock();
        }
    }

    @Test
    public void testGetMyLicenses() {
        final Lock l = RW_LOCK.readLock();
        l.lock();
        try {
            final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                    UnitTestSupport.CREDENTIALS_LOGINNAME_HANNELORE,
                    UnitTestSupport.CREDENTIALS_PASSWORD_PLAIN_HANNELORE);

            final User user = UserDao.getInstance().getUser(UnitTestSupport.USER_ID_HANNELORE);

            final Invocation.Builder b = client.target(restURI + MINE_ENDPOINT).request(MediaType.APPLICATION_JSON);
            cookies.forEach(b::cookie);
            final Response response = b.buildGet().invoke();

            assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
            final List<License> licenses = response.readEntity(new GenericType<List<License>>() {
            });
            assertThat(licenses, hasSize(LicenseDao.getInstance().getLicensesByUser(user).size()));
            assertThat(licenses, everyItem(in(LicenseDao.getInstance().getLicensesByUser(user))));
        } finally {
            l.unlock();
        }
    }

    @Test
    public void testCreateNewLicense() {
        final Lock l = RW_LOCK.writeLock();
        l.lock();
        try {
            final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                    UnitTestSupport.CREDENTIALS_LOGINNAME_MUSTERMANN,
                    UnitTestSupport.CREDENTIALS_PASSWORD_PLAIN_MUSTERMANN);

            final License newLicense = new License(
                    ServiceContractDao.getInstance().getServiceContract(UnitTestSupport.SERVICE_CONTRACT_ID_A),
                    UUID.randomUUID().toString(), 10,
                    ProductVariantDao.getInstance().getProductVariant(UnitTestSupport.PRODUCT_VARIANT_ID_CANOE_OLD));

            final Invocation.Builder b = client.target(restURI).request(MediaType.APPLICATION_JSON);
            cookies.forEach(b::cookie);
            final Response response = b.buildPost(Entity.json(newLicense)).invoke();

            assertThat(response.getStatus(), equalTo(Response.Status.CREATED.getStatusCode()));
            final License createdLicense = response.readEntity(License.class);
            assertThat(createdLicense.getCount(), equalTo(newLicense.getCount()));
            assertThat(createdLicense.getExpirationDate(), equalTo(newLicense.getExpirationDate()));
            assertThat(createdLicense.getKey(), equalTo(newLicense.getKey()));
            assertThat(createdLicense.getProductVariant(), equalTo(newLicense.getProductVariant()));
            assertThat(createdLicense,
                    in(LicenseDao.getInstance().getLicensesByServiceContract(newLicense.getServiceContract())));
        } finally {
            l.unlock();
        }
    }
}
