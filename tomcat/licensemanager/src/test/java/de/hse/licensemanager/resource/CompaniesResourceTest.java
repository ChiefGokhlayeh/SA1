package de.hse.licensemanager.resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Every.everyItem;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.HttpHeaders;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.UnitTestSupport;
import de.hse.licensemanager.dao.CompanyDao;
import de.hse.licensemanager.dao.UserDao;
import de.hse.licensemanager.model.Company;
import de.hse.licensemanager.model.User;

public class CompaniesResourceTest {
    private CompaniesResource companiesResource;

    @Before
    public void setUp() {
        UnitTestSupport.initDatabase();

        companiesResource = new CompaniesResource();
    }

    @Test
    public void testGetCompanies() {
        final List<Company> companies = companiesResource.all();

        assertThat(companies, everyItem(is(in(CompanyDao.getInstance().getCompanies()))));
    }

    @Test
    public void testGetCount() {
        final String countString = companiesResource.count();

        assertThat(Integer.parseInt(countString), is(equalTo(CompanyDao.getInstance().getCompanies().size())));
    }

    @Test
    public void testMine() throws IOException {
        final User testUser = UserDao.getInstance().getUser(UnitTestSupport.USER_ID_MUSTERMANN);

        final HttpServletRequest fakeRequest = mock(HttpServletRequest.class);
        final HttpServletResponse fakeResponse = mock(HttpServletResponse.class);
        final HttpSession fakeSession = mock(HttpSession.class);
        when(fakeRequest.getSession(anyBoolean())).thenReturn(fakeSession);
        when(fakeSession.getAttribute(HttpHeaders.AUTHORIZATION)).thenReturn(testUser);

        final CompanyResource companyResource = companiesResource.mine(fakeRequest, fakeResponse);

        assertThat(companyResource, notNullValue());
        verify(fakeRequest, atLeastOnce()).getSession(anyBoolean());
        verify(fakeSession, atLeastOnce()).getAttribute(HttpHeaders.AUTHORIZATION);
        verifyNoInteractions(fakeResponse);
    }
}
