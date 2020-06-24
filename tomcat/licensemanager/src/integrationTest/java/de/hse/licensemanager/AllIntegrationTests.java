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
    private String baseURI;
    private String restURI;

    @Before
    public void setUp() {
        client = ClientBuilder.newClient();

        if (System.getProperty("gretty.httpsBaseURI") != null) {
            baseURI = System.getProperty("gretty.httpsBaseURI");
        } else {
            baseURI = System.getProperty("gretty.httpBaseURI");
        }

        System.out.println("Integration Test is using base-URI: " + baseURI);

        restURI = baseURI + "/rest";
    }

    @Test
    public void testSmoke() {
        final Response response = client.target(restURI + "/users").request(MediaType.APPLICATION_JSON).buildGet()
                .invoke();

        assertThat(response.getStatus(), is(HttpServletResponse.SC_UNAUTHORIZED));
    }

}
