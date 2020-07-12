package de.hse.licensemanager.resource;

import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.HttpHeaders;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.UnitTestSupport;
import de.hse.licensemanager.dao.CredentialsDao;
import de.hse.licensemanager.model.Credentials;
import de.hse.licensemanager.model.PlainCredentials;

public class AuthenticationResourceTest {

    private AuthenticationResource authenticationResource;

    @Before
    public void setUp() {
        UnitTestSupport.initDatabase();

        authenticationResource = new AuthenticationResource();
    }

    @Test
    public void testLoginCorrect() throws IOException {
        final PlainCredentials credentials = new PlainCredentials(UnitTestSupport.CREDENTIALS_LOGINNAME_MUSTERMANN,
                UnitTestSupport.CREDENTIALS_PASSWORD_PLAIN_MUSTERMANN);
        final HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        final HttpServletResponse servletResponse = mock(HttpServletResponse.class);
        final HttpSession httpSession = mock(HttpSession.class);

        when(servletRequest.getSession(true)).thenReturn(httpSession);

        final Map<String, Object> result = authenticationResource.login(credentials, servletRequest, servletResponse);

        final Credentials expectedCredentials = CredentialsDao.getInstance()
                .getCredentialsByLoginname(credentials.getLoginname());

        assertThat(result.entrySet(), is(not(emptyIterable())));
        assertThat(result, hasEntry("success", true));
        assertThat(result, hasEntry("user", expectedCredentials.getUser()));
        verify(servletRequest, atLeastOnce()).getSession(true);
        verify(httpSession, times(1)).setAttribute(HttpHeaders.AUTHORIZATION, expectedCredentials.getUser());
    }

    @Test
    public void testLoginIncorrect() throws IOException {
        final PlainCredentials credentials = new PlainCredentials(UnitTestSupport.CREDENTIALS_LOGINNAME_MUSTERMANN,
                UnitTestSupport.CREDENTIALS_PASSWORD_PLAIN_MUSTERMANN + "I make this password wrong");
        final HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        final HttpServletResponse servletResponse = mock(HttpServletResponse.class);

        final Map<String, Object> result = authenticationResource.login(credentials, servletRequest, servletResponse);

        assertThat(result, hasEntry("success", false));
        verify(servletResponse, times(1)).sendError(HttpServletResponse.SC_UNAUTHORIZED);
        verify(servletRequest, never()).getSession(true);
    }
}
