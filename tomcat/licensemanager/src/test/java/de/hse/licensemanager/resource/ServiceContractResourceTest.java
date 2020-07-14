package de.hse.licensemanager.resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.UnitTestSupport;
import de.hse.licensemanager.dao.ServiceContractDao;
import de.hse.licensemanager.dao.ServiceGroupDao;
import de.hse.licensemanager.model.ServiceContract;

public class ServiceContractResourceTest {

    private ServiceContract serviceContract;
    private ServiceContractResource serviceContractResource;
    private LoginChecker checker;

    @Before
    public void setUp() {
        UnitTestSupport.initDatabase();

        checker = mock(LoginChecker.class);

        serviceContract = ServiceContractDao.getInstance().getServiceContract(UnitTestSupport.SERVICE_CONTRACT_ID_A);

        serviceContractResource = new ServiceContractResource(serviceContract.getId(), checker);
    }

    @Test
    public void testGet() {
        final HttpServletRequest request = mock(HttpServletRequest.class);

        when(checker.getLoginUser(eq(request))).thenReturn(ServiceGroupDao.getInstance()
                .getServiceGroupsByServiceContract(serviceContract).stream().findFirst().get().getUser());

        final Response testServiceContractResponse = serviceContractResource.get(request);

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
