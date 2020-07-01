package de.hse.licensemanager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.model.PlainCredentials;

public class LoginTest {

    private Client client;
    private String restURI;

    private static final String LOGIN_ENDPOINT = "/login";
    private static final String COUNT_ENDPOINT = "/count";

    @Before
    public void setUp() {
        PrepareTests.initDatabase();

        client = ClientBuilder.newClient();
        restURI = IntegrationTestSupport.getRestURI() + "/users";
    }

    @Test
    public void testLoginCorrect() {
        final PlainCredentials credentials = new PlainCredentials(PrepareTests.CREDENTIALS_LOGINNAME_MUSTERMANN,
                PrepareTests.CREDENTIALS_PASSWORD_PLAIN_MUSTERMANN);

        final Response response = client.target(restURI + LOGIN_ENDPOINT).request(MediaType.APPLICATION_JSON)
                .buildPost(Entity.json(credentials)).invoke();

        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));
        assertThat(response.getCookies().entrySet(), hasSize(greaterThanOrEqualTo(1)));
    }

    @Test
    public void testLoginIncorrectPassword() {
        final PlainCredentials credentials = new PlainCredentials(PrepareTests.CREDENTIALS_LOGINNAME_MUSTERMANN,
                PrepareTests.CREDENTIALS_PASSWORD_PLAIN_MUSTERMANN + "123");

        final Response response = client.target(restURI + LOGIN_ENDPOINT).request(MediaType.APPLICATION_JSON)
                .buildPost(Entity.json(credentials)).invoke();

        assertThat(response.getStatus(), is(HttpServletResponse.SC_UNAUTHORIZED));
    }

    @Test
    public void testLoginIncorrectUser() {
        final PlainCredentials credentials = new PlainCredentials(PrepareTests.CREDENTIALS_LOGINNAME_MUSTERMANN + "123",
                PrepareTests.CREDENTIALS_PASSWORD_PLAIN_MUSTERMANN);

        final Response response = client.target(restURI + LOGIN_ENDPOINT).request(MediaType.APPLICATION_JSON)
                .buildPost(Entity.json(credentials)).invoke();

        assertThat(response.getStatus(), is(HttpServletResponse.SC_UNAUTHORIZED));
    }

    @Test
    public void testLoginAndReadResource() {
        Response response = client.target(restURI + COUNT_ENDPOINT).request(MediaType.TEXT_PLAIN).buildGet().invoke();

        assertThat(response.getStatus(), is(HttpServletResponse.SC_UNAUTHORIZED));

        final PlainCredentials credentials = new PlainCredentials(PrepareTests.CREDENTIALS_LOGINNAME_MUSTERMANN,
                PrepareTests.CREDENTIALS_PASSWORD_PLAIN_MUSTERMANN);
        response = client.target(restURI + LOGIN_ENDPOINT).request(MediaType.APPLICATION_JSON)
                .buildPost(Entity.json(credentials)).invoke();

        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));
        assertThat(response.getCookies().entrySet(), hasSize(greaterThanOrEqualTo(1)));

        final Invocation.Builder b = client.target(restURI + COUNT_ENDPOINT).request(MediaType.TEXT_PLAIN);
        response.getCookies().entrySet().forEach((c) -> b.cookie(c.getValue()));

        response = b.buildGet().invoke();

        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));
    }
}
