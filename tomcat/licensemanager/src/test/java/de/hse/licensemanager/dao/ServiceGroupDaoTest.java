package de.hse.licensemanager.dao;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.PrepareTests;
import de.hse.licensemanager.model.Credentials;
import de.hse.licensemanager.model.ServiceContract;
import de.hse.licensemanager.model.ServiceGroup;
import de.hse.licensemanager.model.User;

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
        final IServiceGroupDao a = ServiceGroupDao.getInstance();
        final IServiceGroupDao b = ServiceGroupDao.getInstance();
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
        final ServiceGroup serviceGroup = new ServiceGroup(
                ServiceContractDao.getInstance().getServiceContract(PrepareTests.SERVICE_CONTRACT_ID_B),
                UserDao.getInstance().getUser(PrepareTests.USER_ID_MUSTERMANN));

        ServiceGroupDao.getInstance().save(serviceGroup);

        final List<ServiceGroup> serviceGroups = ServiceGroupDao.getInstance().getServiceGroups();

        assertThat(serviceGroup, is(in(serviceGroups)));
    }

    @Test
    public void testSaveCascadePersistence() {
        final ServiceGroup serviceGroup = new ServiceGroup(
                new ServiceContract(CompanyDao.getInstance().getCompany(PrepareTests.COMPANY_ID_LICENSEMANAGER)),
                new User("Homer", "Simpson", "homer.simpson@email.com",
                        CompanyDepartmentDao.getInstance()
                                .getCompanyDepartment(PrepareTests.COMPANY_DEPARTMENT_ID_ACCOUNTING),
                        SystemGroupDao.getInstance().getSystemGroup(PrepareTests.SYSTEM_GROUP_ID_ADMIN),
                        new Credentials("homi", "1234")));

        ServiceGroupDao.getInstance().save(serviceGroup);

        assertThat(serviceGroup, is(in(ServiceGroupDao.getInstance().getServiceGroups())));
    }
}
