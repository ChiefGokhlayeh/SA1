package de.hse.licensemanager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Collection;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.dao.ServiceGroupDao;
import de.hse.licensemanager.model.ServiceGroup;

public class ServiceGroupTest {

    private Client client;
    private String restURI;

    private static final String COUNT_ENDPOINT = "/count";
    private static final String MINE_ENDPOINT = "/mine";
    private static final String BY_CONTRACTOR_ENDPOINT = "/by-contractor";
    private static final String BY_USER_ENDPOINT = "/by-user";

    @Before
    public void setUp() {
        PrepareTests.initDatabase();

        client = ClientBuilder.newClient();
        restURI = IntegrationTestSupport.getRestURI() + "/service-groups";
    }

    @Test
    public void testGetOwnServiceGroups() {
        final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                PrepareTests.CREDENTIALS_LOGINNAME_HANNELORE, PrepareTests.CREDENTIALS_PASSWORD_PLAIN_HANNELORE);

        final Invocation.Builder b = client.target(restURI + MINE_ENDPOINT).request(MediaType.APPLICATION_JSON);
        cookies.forEach(b::cookie);
        final Response response = b.buildGet().invoke();

        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(response.readEntity(new GenericType<List<ServiceGroup>>() {
        }), everyItem(in(ServiceGroupDao.getInstance().getServiceGroupsByUser(PrepareTests.USER_ID_HANNELORE))));
    }

    @Test
    public void testGetCount() {
        final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                PrepareTests.CREDENTIALS_LOGINNAME_HANNELORE, PrepareTests.CREDENTIALS_PASSWORD_PLAIN_HANNELORE);

        final Invocation.Builder b = client.target(restURI + COUNT_ENDPOINT).request(MediaType.TEXT_PLAIN);
        cookies.forEach(b::cookie);
        final Response response = b.buildGet().invoke();

        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(Integer.parseInt(response.readEntity(String.class)),
                is(ServiceGroupDao.getInstance().getServiceGroups().size()));
    }

    @Test
    public void testGetAllServiceGroups() {
        final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                PrepareTests.CREDENTIALS_LOGINNAME_MUSTERMANN, PrepareTests.CREDENTIALS_PASSWORD_PLAIN_MUSTERMANN);

        final Invocation.Builder b = client.target(restURI).request(MediaType.APPLICATION_JSON);
        cookies.forEach(b::cookie);
        final Response response = b.buildGet().invoke();

        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(response.readEntity(new GenericType<List<ServiceGroup>>() {
        }), everyItem(in(ServiceGroupDao.getInstance().getServiceGroups())));
    }

    @Test
    public void testGetServiceGroupsByContrator() {
        final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                PrepareTests.CREDENTIALS_LOGINNAME_HANNELORE, PrepareTests.CREDENTIALS_PASSWORD_PLAIN_HANNELORE);

        final ServiceGroup serviceGroup = ServiceGroupDao.getInstance().getServiceGroups().stream().findFirst().get();

        final Invocation.Builder b = client
                .target(restURI + BY_CONTRACTOR_ENDPOINT
                        + String.format("/%d", serviceGroup.getServiceContract().getId()))
                .request(MediaType.APPLICATION_JSON);
        cookies.forEach(b::cookie);
        final Response response = b.buildGet().invoke();

        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(response.readEntity(new GenericType<List<ServiceGroup>>() {
        }), everyItem(in(
                ServiceGroupDao.getInstance().getServiceGroupsByServiceContract(serviceGroup.getServiceContract()))));
    }

    @Test
    public void testGetServiceGroupsByUser() {
        final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                PrepareTests.CREDENTIALS_LOGINNAME_MUSTERMANN, PrepareTests.CREDENTIALS_PASSWORD_PLAIN_MUSTERMANN);

        final ServiceGroup serviceGroup = ServiceGroupDao.getInstance().getServiceGroups().stream().findFirst().get();

        final Invocation.Builder b = client
                .target(restURI + BY_USER_ENDPOINT + String.format("/%d", serviceGroup.getUser().getId()))
                .request(MediaType.APPLICATION_JSON);
        cookies.forEach(b::cookie);
        final Response response = b.buildGet().invoke();

        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(response.readEntity(new GenericType<List<ServiceGroup>>() {
        }), everyItem(in(
                ServiceGroupDao.getInstance().getServiceGroupsByServiceContract(serviceGroup.getServiceContract()))));
    }
}
