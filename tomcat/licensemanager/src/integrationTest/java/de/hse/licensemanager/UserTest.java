package de.hse.licensemanager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Collection;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.dao.UserDao;
import de.hse.licensemanager.model.User;

public class UserTest {
    private Client client;
    private String restURI;

    private static final String COUNT_ENDPOINT = "/count";
    private static final String ME_ENDPOINT = "/me";

    @Before
    public void setUp() {
        PrepareTests.initDatabase();

        client = ClientBuilder.newClient();
        restURI = IntegrationTestSupport.getRestURI() + "/users";
    }

    @Test
    public void testGetCount() {
        final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                PrepareTests.CREDENTIALS_LOGINNAME_HANNELORE, PrepareTests.CREDENTIALS_PASSWORD_PLAIN_HANNELORE);

        final Invocation.Builder b = client.target(restURI + COUNT_ENDPOINT).request(MediaType.TEXT_PLAIN);
        cookies.forEach(b::cookie);
        final Response response = b.buildGet().invoke();

        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(Integer.parseInt(response.readEntity(String.class)), is(UserDao.getInstance().getUsers().size()));
    }

    @Test
    public void testGetMe() {
        final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                PrepareTests.CREDENTIALS_LOGINNAME_HANNELORE, PrepareTests.CREDENTIALS_PASSWORD_PLAIN_HANNELORE);

        final Invocation.Builder b = client.target(restURI + ME_ENDPOINT).request(MediaType.APPLICATION_JSON);
        cookies.forEach(b::cookie);
        final Response response = b.buildGet().invoke();

        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(response.readEntity(User.class),
                is(equalTo(UserDao.getInstance().getUser(PrepareTests.USER_ID_HANNELORE))));
    }
}
