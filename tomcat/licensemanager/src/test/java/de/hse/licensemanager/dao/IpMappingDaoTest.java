package de.hse.licensemanager.dao;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.UnitTestSupport;
import de.hse.licensemanager.model.IpMapping;

public class IpMappingDaoTest {

    @Before
    public void setupBeforeTest() {
        UnitTestSupport.initDatabase();
    }

    @Test
    public void testInstanceAllocated() {
        assertThat(IpMappingDao.getInstance(), notNullValue());
    }

    @Test
    public void testInstanceSingleton() {
        final IIpMappingDao a = IpMappingDao.getInstance();
        final IIpMappingDao b = IpMappingDao.getInstance();
        assertThat(a, sameInstance(b));
    }

    @Test
    public void testQueryAllIpMappings() {
        final List<IpMapping> ipMappings = IpMappingDao.getInstance().getIpMappings();

        assertThat(ipMappings, not(empty()));
        assertThat(ipMappings, hasSize(2));
        assertThat(ipMappings.stream().map((u) -> u.getId()).collect(Collectors.toList()),
                containsInAnyOrder(UnitTestSupport.IP_MAPPING_ID_HOST1, UnitTestSupport.IP_MAPPING_ID_HOST2));
    }

    @Test
    public void testFindIpMappingById() {
        final IpMapping host1 = IpMappingDao.getInstance().getIpMapping(UnitTestSupport.IP_MAPPING_ID_HOST1);
        assertThat(host1, notNullValue());
        assertThat(host1.getIpAddress(), equalTo(UnitTestSupport.IP_MAPPING_IP_ADDRESS_HOST1));
    }

    @Test
    public void testQueryIpMappingsByLicense() {
        final List<IpMapping> ipMappings = IpMappingDao.getInstance()
                .getIpMappingsByLicense(UnitTestSupport.LICENSE_ID_CANOE_NEW);

        assertThat(ipMappings, hasSize(1));
        assertThat(ipMappings, everyItem(notNullValue()));
    }
}
