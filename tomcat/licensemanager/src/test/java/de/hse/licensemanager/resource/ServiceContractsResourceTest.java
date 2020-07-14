package de.hse.licensemanager.resource;

import static org.hamcrest.core.Every.everyItem;
import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.dao.ServiceContractDao;
import de.hse.licensemanager.model.ServiceContract;
import de.hse.licensemanager.UnitTestSupport;

public class ServiceContractsResourceTest {

    private ServiceContractsResource serviceContractsResource;
    private ILoginChecker checker;

    @Before
    public void setUp() {
        UnitTestSupport.initDatabase();

        checker = mock(LoginChecker.class);

        serviceContractsResource = new ServiceContractsResource(checker);
    }

    @Test
    public void testGetAllServiceContract() {
        final List<ServiceContract> serviceContracts = serviceContractsResource.all();

        assertThat(serviceContracts, everyItem(is(in(ServiceContractDao.getInstance().getServiceContracts()))));
    }

    @Test
    public void testCountServiceContracts() {
        final String count = serviceContractsResource.count();

        assertThat(Integer.parseInt(count), equalTo(ServiceContractDao.getInstance().getServiceContracts().size()));
    }

    @Test
    public void testGetSpecificServiceContract() throws IOException {
        final HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        final HttpServletResponse servletResponse = mock(HttpServletResponse.class);

        final long id = UnitTestSupport.SERVICE_CONTRACT_ID_B;
        final ServiceContractResource serviceContractResource = serviceContractsResource.serviceContract(id,
                servletRequest, servletResponse);

        assertThat(serviceContractResource, notNullValue());
    }
}
