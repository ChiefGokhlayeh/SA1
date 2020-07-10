package de.hse.licensemanager.resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Every.everyItem;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.HttpHeaders;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.PrepareTests;
import de.hse.licensemanager.dao.CompanyDao;
import de.hse.licensemanager.dao.UserDao;
import de.hse.licensemanager.model.Company;
import de.hse.licensemanager.model.User;

public class CompaniesResourceTest {
    private CompaniesResource companiesResource;

    @Before
    public void setUp() {
        PrepareTests.initDatabase();

        companiesResource = new CompaniesResource();
    }

    @Test
    public void testGetCompanies() {
        final List<Company> companies = companiesResource.getCompanies();

        assertThat(companies, everyItem(is(in(CompanyDao.getInstance().getCompanies()))));
    }

    @Test
    public void testGetCount() {
        final String countString = companiesResource.getCount();

        assertThat(Integer.parseInt(countString), is(equalTo(CompanyDao.getInstance().getCompanies().size())));
    }

    @Test
    public void testMine() {
        final User testUser = UserDao.getInstance().getUser(PrepareTests.USER_ID_MUSTERMANN);

        final HttpServletRequest fakeRequest = mock(HttpServletRequest.class);
        final HttpSession fakeSession = mock(HttpSession.class);
        when(fakeRequest.getSession(anyBoolean())).thenReturn(fakeSession);
        when(fakeSession.getAttribute(HttpHeaders.AUTHORIZATION)).thenReturn(testUser);

        final Company company = companiesResource.mine(fakeRequest);

        assertThat(company, is(equalTo(testUser.getCompany())));
        verify(fakeRequest, atLeastOnce()).getSession(anyBoolean());
        verify(fakeSession, atLeastOnce()).getAttribute(HttpHeaders.AUTHORIZATION);
    }
}
