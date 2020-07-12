package de.hse.licensemanager.dao;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.UnitTestSupport;
import de.hse.licensemanager.model.Credentials;
import de.hse.licensemanager.model.ServiceContract;
import de.hse.licensemanager.model.ServiceGroup;
import de.hse.licensemanager.model.User;
import de.hse.licensemanager.model.User.Group;

public class ServiceGroupDaoTest {

    @Before
    public void setupBeforeTest() {
        UnitTestSupport.initDatabase();
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
    public void testFindServiceGroupByUser() {
        final List<ServiceGroup> groups = ServiceGroupDao.getInstance()
                .getServiceGroupsByUser(UserDao.getInstance().getUser(UnitTestSupport.USER_ID_HANNELORE));
        assertThat(groups, notNullValue());
        assertThat(groups.size(), greaterThan(0));
    }

    @Test
    public void testFindServiceGroupByUserId() {
        final List<ServiceGroup> groups = ServiceGroupDao.getInstance()
                .getServiceGroupsByUser(UnitTestSupport.USER_ID_HANNELORE);
        assertThat(groups, notNullValue());
        assertThat(groups.size(), greaterThan(0));
        assertThat(groups.size(), equalTo(ServiceGroupDao.getInstance()
                .getServiceGroupsByUser(UserDao.getInstance().getUser(UnitTestSupport.USER_ID_HANNELORE)).size()));
    }

    @Test
    public void testFindServiceGroupByServiceContract() {
        final List<ServiceGroup> groups = ServiceGroupDao.getInstance().getServiceGroupsByServiceContract(
                ServiceContractDao.getInstance().getServiceContract(UnitTestSupport.SERVICE_CONTRACT_ID_A));
        assertThat(groups, notNullValue());
        assertThat(groups.size(), greaterThan(0));
    }

    @Test
    public void testFindServiceGroupByServiceContractId() {
        final List<ServiceGroup> groups = ServiceGroupDao.getInstance()
                .getServiceGroupsByServiceContract(UnitTestSupport.COMPANY_ID_LICENSEMANAGER);
        assertThat(groups, notNullValue());
        assertThat(groups.size(), greaterThan(0));
        assertThat(groups.size(),
                equalTo(ServiceGroupDao.getInstance().getServiceGroupsByServiceContract(
                        ServiceContractDao.getInstance().getServiceContract(UnitTestSupport.SERVICE_CONTRACT_ID_A))
                        .size()));
    }

    @Test
    public void testFindServiceGroupById() {
        final ServiceGroup groupA = ServiceGroupDao.getInstance().getServiceGroup(UnitTestSupport.SERVICE_CONTRACT_ID_A,
                UnitTestSupport.USER_ID_HANNELORE);
        assertThat(groupA, notNullValue());
    }

    @Test
    public void testFoundServiceGroupDataPopulated() {
        final ServiceGroup groupA = ServiceGroupDao.getInstance().getServiceGroup(UnitTestSupport.SERVICE_CONTRACT_ID_A,
                UnitTestSupport.USER_ID_HANNELORE);

        assertThat(groupA.getServiceContract().getId(), equalTo(UnitTestSupport.SERVICE_CONTRACT_ID_A));
        assertThat(groupA.getUser().getId(), equalTo(UnitTestSupport.USER_ID_HANNELORE));
    }

    @Test
    public void testSaveSimple() {
        final ServiceGroup serviceGroup = new ServiceGroup(
                ServiceContractDao.getInstance().getServiceContract(UnitTestSupport.SERVICE_CONTRACT_ID_B),
                UserDao.getInstance().getUser(UnitTestSupport.USER_ID_MUSTERMANN));

        ServiceGroupDao.getInstance().save(serviceGroup);

        final List<ServiceGroup> serviceGroups = ServiceGroupDao.getInstance().getServiceGroups();

        assertThat(serviceGroup, is(in(serviceGroups)));
    }

    @Test
    public void testSaveCascadePersistence() {
        final ServiceGroup serviceGroup = new ServiceGroup(
                new ServiceContract(CompanyDao.getInstance().getCompany(UnitTestSupport.COMPANY_ID_LICENSEMANAGER)),
                new User("Homer", "Simpson", "homer.simpson@email.com",
                        CompanyDepartmentDao.getInstance()
                                .getCompanyDepartment(UnitTestSupport.COMPANY_DEPARTMENT_ID_ACCOUNTING),
                        Group.COMPANY_ADMIN, new Credentials("homi", "1234")));

        ServiceGroupDao.getInstance().save(serviceGroup);

        assertThat(serviceGroup, is(in(ServiceGroupDao.getInstance().getServiceGroups())));
    }
}
