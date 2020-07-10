package de.hse.licensemanager.dao;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.PrepareTests;
import de.hse.licensemanager.model.IpMapping;

public class IpMappingDaoTest {

    @Before
    public void setupBeforeTest() {
        PrepareTests.initDatabase();
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
        assertThat(ipMappings, hasSize(1));
        assertThat(ipMappings.stream().map((u) -> u.getId()).collect(Collectors.toList()),
                containsInAnyOrder((long) PrepareTests.IP_MAPPING_ID_HOST1));
    }

    @Test
    public void testFindIpMappingById() {
        final IpMapping host1 = IpMappingDao.getInstance().getIpMapping(PrepareTests.IP_MAPPING_ID_HOST1);
        assertThat(host1, notNullValue());
        assertThat(host1.getIpAddress(), equalTo(PrepareTests.IP_MAPPING_IP_ADDRESS_HOST1));
    }
}
