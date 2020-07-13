package de.hse.licensemanager.resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.UnitTestSupport;
import de.hse.licensemanager.dao.ServiceContractDao;
import de.hse.licensemanager.model.ServiceContract;

public class ServiceContractResourceTest {

    private ServiceContract serviceContract;
    private ServiceContractResource serviceContractResource;

    @Before
    public void setUp() {
        UnitTestSupport.initDatabase();

        serviceContract = ServiceContractDao.getInstance().getServiceContract(UnitTestSupport.SERVICE_CONTRACT_ID_A);

        serviceContractResource = new ServiceContractResource(serviceContract.getId());
    }

    @Test
    public void testGet() {
        final Response testServiceContractResponse = serviceContractResource.get();

        assertThat(testServiceContractResponse, notNullValue());
        assertThat(testServiceContractResponse.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(testServiceContractResponse.getEntity(), equalTo(serviceContract));
    }

    @Test
    public void testDelete() {
        assertThat(serviceContract, in(ServiceContractDao.getInstance().getServiceContracts()));

        serviceContractResource.delete();

        assertThat(serviceContract, not(in(ServiceContractDao.getInstance().getServiceContracts())));
    }

    @Test
    public void testPut() {
        final UriInfo uriInfo = mock(UriInfo.class);

        serviceContractResource.put(serviceContract, uriInfo);
    }
}
