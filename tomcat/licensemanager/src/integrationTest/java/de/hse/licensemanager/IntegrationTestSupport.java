package de.hse.licensemanager;

import java.util.Collection;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.hse.licensemanager.model.PlainCredentials;

public class IntegrationTestSupport {

    public static String getRestURI() {
        return getBaseURI() + "/rest";
    }

    public static String getBaseURI() {
        if (System.getProperty("gretty.httpsBaseURI") != null) {
            return System.getProperty("gretty.httpsBaseURI");
        } else if (System.getProperty("gretty.httpBaseURI") != null) {
            return System.getProperty("gretty.httpBaseURI");
        } else {
            return "http://localhost:8080/licensemanager";
        }
    }

    public static Collection<NewCookie> login(final Client client, final String loginname, final String password) {
        final PlainCredentials credentials = new PlainCredentials(loginname, password);
        final Response response = client.target(IntegrationTestSupport.getRestURI() + "/auth/login")
                .request(MediaType.APPLICATION_JSON).buildPost(Entity.json(credentials)).invoke();
        if (response.getStatus() != Status.OK.getStatusCode()) {
            throw new IllegalStateException("Unable to login");
        }
        return response.getCookies().values();
    }
}
