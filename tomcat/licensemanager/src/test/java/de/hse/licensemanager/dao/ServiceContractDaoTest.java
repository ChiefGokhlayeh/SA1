package de.hse.licensemanager.dao;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.PrepareTests;
import de.hse.licensemanager.model.ServiceContract;

public class ServiceContractDaoTest {

    @Before
    public void setupBeforeTest() {
        PrepareTests.initDatabase();
    }

    @Test
    public void testInstanceAllocated() {
        assertThat(ServiceContractDao.getInstance(), notNullValue());
    }

    @Test
    public void testInstanceSingleton() {
        final ServiceContractDao a = ServiceContractDao.getInstance();
        final ServiceContractDao b = ServiceContractDao.getInstance();
        assertThat(a, sameInstance(b));
    }

    @Test
    public void testFindServiceContractById() {
        final ServiceContract a = ServiceContractDao.getInstance()
                .getServiceContract(PrepareTests.SERVICE_CONTRACT_ID_A);
        assertThat(a, notNullValue());
    }

    @Test
    public void testFoundServiceContractDataPopulated() {
        final ServiceContract contractA = ServiceContractDao.getInstance()
                .getServiceContract(PrepareTests.SERVICE_CONTRACT_ID_A);

        assertThat(contractA.getId(), equalTo(PrepareTests.SERVICE_CONTRACT_ID_A));
        assertThat(contractA.getStart(), equalTo(PrepareTests.SERVICE_CONTRACT_START_A));
        assertThat(contractA.getEnd(), equalTo(PrepareTests.SERVICE_CONTRACT_END_A));
    }

    @Test
    public void testFoundServiceContractNestedDataPopulated() {
        final ServiceContract contractA = ServiceContractDao.getInstance()
                .getServiceContract(PrepareTests.SERVICE_CONTRACT_ID_B);

        assertThat(contractA.getContractor().getAddress(), equalTo(PrepareTests.COMPANY_ADDRESS_NOTABROTHEL));
        assertThat(contractA.getLicenses().stream().map((l) -> l.getKey()).collect(Collectors.toList()),
                containsInAnyOrder(PrepareTests.LICENSE_KEY_WINDOWS));
    }

    @Test
    public void testQueryAllServiceContracts() {
        final List<ServiceContract> contracts = ServiceContractDao.getInstance().getServiceContracts();

        assertThat(contracts, not(empty()));
        assertThat(contracts, hasSize(2));
        assertThat(contracts.stream().map((c) -> c.getId()).collect(Collectors.toList()), containsInAnyOrder(
                (long) PrepareTests.SERVICE_CONTRACT_ID_A, (long) PrepareTests.SERVICE_CONTRACT_ID_B));
    }
}
