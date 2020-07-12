package de.hse.licensemanager.resource;

import static org.hamcrest.core.Every.everyItem;
import static org.mockito.Mockito.mock;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;

import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.dao.ServiceContractDao;
import de.hse.licensemanager.model.ServiceContract;
import de.hse.licensemanager.PrepareTests;

public class ServiceContractsResourceTest {

    private ServiceContractsResource serviceContractsResource;

    @Before
    public void setUp() {
        PrepareTests.initDatabase();

        serviceContractsResource = new ServiceContractsResource();
    }

    @Test
    public void testGetAllServiceContract() {
        final List<ServiceContract> serviceContracts = serviceContractsResource.getServiceContracts();

        assertThat(serviceContracts, everyItem(is(in(ServiceContractDao.getInstance().getServiceContracts()))));
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
