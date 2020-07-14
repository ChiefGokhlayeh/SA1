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

import de.hse.licensemanager.dao.IpMappingDao;
import de.hse.licensemanager.dao.LicenseDao;
import de.hse.licensemanager.model.IpMapping;
import de.hse.licensemanager.model.License;

public class IpMappingTest {

    private static final ReadWriteLock RW_LOCK = new ReentrantReadWriteLock();

    private Client client;
    private String restURI;

    private static final String COUNT_ENDPOINT = "/count";
    private static final String BY_LICENSE_ENDPOINT = "/by-license";

    @Before
    public void setUp() {
        UnitTestSupport.initDatabase();

        client = ClientBuilder.newClient();
        restURI = IntegrationTestSupport.getRestURI() + "/ip-mappings";
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
            assertThat(count, equalTo(IpMappingDao.getInstance().getIpMappings().size()));
        } finally {
            l.unlock();
        }
    }

    @Test
    public void testGetAllIpMappings() {
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
            final List<IpMapping> ipMappings = response.readEntity(new GenericType<List<IpMapping>>() {
            });
            assertThat(ipMappings, hasSize(IpMappingDao.getInstance().getIpMappings().size()));
            assertThat(ipMappings, everyItem(in(IpMappingDao.getInstance().getIpMappings())));
        } finally {
            l.unlock();
        }
    }

    @Test
    public void testGetLicensesByLicense() {
        final Lock l = RW_LOCK.readLock();
        l.lock();
        try {
            final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                    UnitTestSupport.CREDENTIALS_LOGINNAME_MUSTERMANN,
                    UnitTestSupport.CREDENTIALS_PASSWORD_PLAIN_MUSTERMANN);

            final License license = LicenseDao.getInstance().getLicense(UnitTestSupport.LICENSE_ID_MATLAB);

            final Invocation.Builder b = client
                    .target(restURI + String.format("%s/%d", BY_LICENSE_ENDPOINT, license.getId()))
                    .request(MediaType.APPLICATION_JSON);
            cookies.forEach(b::cookie);
            final Response response = b.buildGet().invoke();

            assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
            final List<IpMapping> ipMappings = response.readEntity(new GenericType<List<IpMapping>>() {
            });
            assertThat(ipMappings, hasSize(IpMappingDao.getInstance().getIpMappingsByLicense(license).size()));
            assertThat(ipMappings, everyItem(in(IpMappingDao.getInstance().getIpMappingsByLicense(license))));
        } finally {
            l.unlock();
        }
    }
}
