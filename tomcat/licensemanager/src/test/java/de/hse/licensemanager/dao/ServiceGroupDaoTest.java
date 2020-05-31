package de.hse.licensemanager.dao;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.PrepareTests;
import de.hse.licensemanager.model.ServiceGroup;

public class ServiceGroupDaoTest {

    @Before
    public void setupBeforeTest() {
        PrepareTests.initDatabase();
    }

    @Test
    public void testInstanceAllocated() {
        assertThat(ServiceGroupDao.getInstance(), notNullValue());
    }

    @Test
    public void testInstanceSingleton() {
        final ServiceGroupDao a = ServiceGroupDao.getInstance();
        final ServiceGroupDao b = ServiceGroupDao.getInstance();
        assertThat(a, sameInstance(b));
    }

    @Test
    public void testFindServiceGroupById() {
        final ServiceGroup groupA = ServiceGroupDao.getInstance().getServiceGroup(PrepareTests.SERVICE_CONTRACT_ID_A,
                PrepareTests.USER_ID_HANNELORE);
        assertThat(groupA, notNullValue());
    }

    @Test
    public void testFoundServiceGroupDataPopulated() {
        final ServiceGroup groupA = ServiceGroupDao.getInstance().getServiceGroup(PrepareTests.SERVICE_CONTRACT_ID_A,
                PrepareTests.USER_ID_HANNELORE);

        assertThat(groupA.getServiceContract().getId(), equalTo(PrepareTests.SERVICE_CONTRACT_ID_A));
        assertThat(groupA.getUser().getId(), equalTo(PrepareTests.USER_ID_HANNELORE));
    }

    @Test
    public void testSaveSimple() {
        final ServiceGroup serviceGroup = new ServiceGroup();
        serviceGroup.setServiceContract(
                ServiceContractDao.getInstance().getServiceContract(PrepareTests.SERVICE_CONTRACT_ID_B));
        serviceGroup.setUser(UserDao.getInstance().getUser(PrepareTests.USER_ID_MUSTERMANN));

        ServiceGroupDao.getInstance().save(serviceGroup);

        final List<ServiceGroup> serviceGroups = ServiceGroupDao.getInstance().getServiceGroups();

        assertThat(serviceGroup, is(in(serviceGroups)));
    }
}
