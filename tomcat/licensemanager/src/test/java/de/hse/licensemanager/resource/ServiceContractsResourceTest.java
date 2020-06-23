package de.hse.licensemanager.resource;

import static org.hamcrest.core.Every.everyItem;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.dao.ServiceContractDao;
import de.hse.licensemanager.dao.UserDao;
import de.hse.licensemanager.model.ServiceContract;
import de.hse.licensemanager.model.User;
import de.hse.licensemanager.PrepareTests;

public class ServiceContractsResourceTest {

    private ServiceContractsResource serviceContractsResource;
    private User loggedInUser;

    @Before
    public void setUp() {
        PrepareTests.initDatabase();

        loggedInUser = UserDao.getInstance().getUser(PrepareTests.USER_ID_HANNELORE);

        serviceContractsResource = new ServiceContractsResource();
    }

    @Test
    public void testGetServiceContractByLoggedInUser() {
        final HttpSession httpSession = mock(HttpSession.class);
        final HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        when(httpRequest.getSession(false)).thenReturn(httpSession);
        when(httpSession.getAttribute(HttpHeaders.AUTHORIZATION)).thenReturn(loggedInUser);

        final List<ServiceContract> serviceContracts = serviceContractsResource.getServiceContracts(httpRequest);

        assertThat(serviceContracts, everyItem(is(in(ServiceContractDao.getInstance().getServiceContracts()))));
        verify(httpRequest).getSession(false);
        verify(httpSession).getAttribute(HttpHeaders.AUTHORIZATION);
    }

    @Test
    public void testCountServiceContracts() {
        final String count = serviceContractsResource.getCount();

        assertThat(Integer.parseInt(count), equalTo(ServiceContractDao.getInstance().getServiceContracts().size()));
    }

    @Test
    public void testGetSingleServiceContract() {
        final UriInfo uriInfo = mock(UriInfo.class);
        final long id = PrepareTests.SERVICE_CONTRACT_ID_A;
        final ServiceContractResource serviceContractResource = serviceContractsResource.getServiceContract(uriInfo,
                id);

        assertThat(serviceContractResource, notNullValue());
    }
}
