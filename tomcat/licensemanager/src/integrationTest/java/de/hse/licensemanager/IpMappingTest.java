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
    public void testGetIpMappingByLicense() {
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

    @Test
    public void testCreateNewIpMapping() {
        final Lock l = RW_LOCK.writeLock();
        l.lock();
        try {
            final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                    UnitTestSupport.CREDENTIALS_LOGINNAME_MUSTERMANN,
                    UnitTestSupport.CREDENTIALS_PASSWORD_PLAIN_MUSTERMANN);

            final IpMapping newIpMapping = new IpMapping(
                    LicenseDao.getInstance().getLicense(UnitTestSupport.LICENSE_ID_WINDOWS), "231.240.0.3");

            final Invocation.Builder b = client.target(restURI).request(MediaType.APPLICATION_JSON);
            cookies.forEach(b::cookie);
            final Response response = b.buildPost(Entity.json(newIpMapping)).invoke();

            assertThat(response.getStatus(), equalTo(Response.Status.CREATED.getStatusCode()));
            final IpMapping createdIpMapping = response.readEntity(IpMapping.class);
            assertThat(createdIpMapping.getIpAddress(), equalTo(newIpMapping.getIpAddress()));
            assertThat(createdIpMapping.getLicense(), equalTo(newIpMapping.getLicense()));
            assertThat(createdIpMapping,
                    in(IpMappingDao.getInstance().getIpMappingsByLicense(newIpMapping.getLicense())));
        } finally {
            l.unlock();
        }
    }

    @Test
    public void testGetSpecificIpMapping() {
        final Lock l = RW_LOCK.readLock();
        l.lock();
        try {
            final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                    UnitTestSupport.CREDENTIALS_LOGINNAME_MUSTERMANN,
                    UnitTestSupport.CREDENTIALS_PASSWORD_PLAIN_MUSTERMANN);

            final IpMapping ipMapping = IpMappingDao.getInstance().getIpMapping(UnitTestSupport.IP_MAPPING_ID_HOST2);

            final Invocation.Builder b = client.target(restURI + String.format("/%d", ipMapping.getId()))
                    .request(MediaType.APPLICATION_JSON);
            cookies.forEach(b::cookie);
            final Response response = b.buildGet().invoke();

            assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
            final IpMapping respIpMappings = response.readEntity(IpMapping.class);
            assertThat(respIpMappings, equalTo(ipMapping));
        } finally {
            l.unlock();
        }
    }

    @Test
    public void testChangeAddress() {
        final Lock l = RW_LOCK.writeLock();
        l.lock();
        try {
            final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                    UnitTestSupport.CREDENTIALS_LOGINNAME_MUSTERMANN,
                    UnitTestSupport.CREDENTIALS_PASSWORD_PLAIN_MUSTERMANN);

            final IpMapping originalIpMapping = IpMappingDao.getInstance()
                    .getIpMapping(UnitTestSupport.IP_MAPPING_ID_HOST2);
            final IpMapping ipMapping = new IpMapping(originalIpMapping.getLicense(), "231.240.0.3");

            final Invocation.Builder b = client.target(restURI + String.format("/%d", originalIpMapping.getId()))
                    .request(MediaType.APPLICATION_JSON);
            cookies.forEach(b::cookie);
            final Response response = b.buildPut(Entity.json(ipMapping)).invoke();

            assertThat(response.getStatus(), equalTo(Response.Status.CREATED.getStatusCode()));
            final IpMapping modifiedIpMapping = response.readEntity(IpMapping.class);
            assertThat(modifiedIpMapping.getId(), equalTo(originalIpMapping.getId()));
            assertThat(modifiedIpMapping.getIpAddress(), equalTo(ipMapping.getIpAddress()));
            assertThat(modifiedIpMapping.getLicense(), equalTo(originalIpMapping.getLicense()));
            assertThat(modifiedIpMapping,
                    in(IpMappingDao.getInstance().getIpMappingsByLicense(originalIpMapping.getLicense())));
        } finally {
            l.unlock();
        }
    }

    @Test
    public void testDeleteIpMapping() {
        final Lock l = RW_LOCK.writeLock();
        l.lock();
        try {
            final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                    UnitTestSupport.CREDENTIALS_LOGINNAME_MUSTERMANN,
                    UnitTestSupport.CREDENTIALS_PASSWORD_PLAIN_MUSTERMANN);

            final IpMapping ipMapping = IpMappingDao.getInstance().getIpMapping(UnitTestSupport.IP_MAPPING_ID_HOST2);

            final Invocation.Builder b = client.target(restURI + String.format("/%d", ipMapping.getId())).request();
            cookies.forEach(b::cookie);
            final Response response = b.buildDelete().invoke();

            assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
            assertThat(ipMapping, not(in(IpMappingDao.getInstance().getIpMappings())));
        } finally {
            l.unlock();
        }
    }
}
