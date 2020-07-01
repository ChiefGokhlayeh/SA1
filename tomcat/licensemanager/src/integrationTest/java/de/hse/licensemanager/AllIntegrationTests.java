package de.hse.licensemanager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

public class AllIntegrationTests {

    private Client client;
    private String restURI;

    @Before
    public void setUp() {
        client = ClientBuilder.newClient();
        restURI = IntegrationTestSupport.getRestURI();

        System.out.println("Integration Test is using base-URI: " + IntegrationTestSupport.getBaseURI()
                + "\n\tAnd REST-URI: " + IntegrationTestSupport.getRestURI());
    }

    @Test
    public void testSmoke() {
        final Response response = client.target(restURI + "/users").request(MediaType.APPLICATION_JSON).buildGet()
                .invoke();

        assertThat(response.getStatus(), is(HttpServletResponse.SC_UNAUTHORIZED));
    }
}
