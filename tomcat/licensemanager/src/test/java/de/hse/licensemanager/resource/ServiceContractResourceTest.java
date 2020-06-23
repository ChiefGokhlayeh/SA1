package de.hse.licensemanager.resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.PrepareTests;
import de.hse.licensemanager.dao.ServiceContractDao;
import de.hse.licensemanager.model.ServiceContract;

public class ServiceContractResourceTest {

    private ServiceContract serviceContract;
    private ServiceContractResource serviceContractResource;
    private UriInfo uriInfo;

    @Before
    public void setUp() {
        PrepareTests.initDatabase();

        serviceContract = ServiceContractDao.getInstance().getServiceContract(PrepareTests.SERVICE_CONTRACT_ID_A);

        uriInfo = mock(UriInfo.class);

        serviceContractResource = new ServiceContractResource(uriInfo, serviceContract.getId());
    }

    @Test
    public void testGet() {
        final ServiceContract testServiceContract = serviceContractResource.get();

        assertThat(testServiceContract, notNullValue());
        assertThat(testServiceContract, equalTo(serviceContract));
    }

    @Test
    public void testDelete() {
        assertThat(serviceContract, in(ServiceContractDao.getInstance().getServiceContracts()));

        serviceContractResource.delete();

        assertThat(serviceContract, not(in(ServiceContractDao.getInstance().getServiceContracts())));
    }

    @Test
    public void testPut() throws URISyntaxException {
        when(uriInfo.getAbsolutePath())
                .thenReturn(new URI("http://www.test.org/service-contracts/" + serviceContract.getId()));
        final JAXBElement<ServiceContract> dummyElement = new JAXBElement<>(
                new QName("http://www.test.org", "service-contract"), ServiceContract.class, serviceContract);
        serviceContractResource.put(dummyElement);
    }
}
