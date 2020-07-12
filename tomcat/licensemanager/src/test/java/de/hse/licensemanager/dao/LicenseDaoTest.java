package de.hse.licensemanager.dao;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.UnitTestSupport;
import de.hse.licensemanager.model.License;

public class LicenseDaoTest {

    @Before
    public void setupBeforeTest() {
        UnitTestSupport.initDatabase();
    }

    @Test
    public void testInstanceAllocated() {
        assertThat(LicenseDao.getInstance(), notNullValue());
    }

    @Test
    public void testInstanceSingleton() {
        final ILicenseDao a = LicenseDao.getInstance();
        final ILicenseDao b = LicenseDao.getInstance();
        assertThat(a, sameInstance(b));
    }

    @Test
    public void testQueryAllLicenses() {
        final List<License> license = LicenseDao.getInstance().getLicenses();

        assertThat(license, not(empty()));
        assertThat(license, hasSize(7));
        assertThat(license.stream().map((u) -> u.getId()).collect(Collectors.toList()),
                containsInAnyOrder(UnitTestSupport.LICENSE_ID_WINDOWS, UnitTestSupport.LICENSE_ID_MATLAB,
                        UnitTestSupport.LICENSE_ID_QUARTUS_OLD, UnitTestSupport.LICENSE_ID_QUARTUS_NEW,
                        UnitTestSupport.LICENSE_ID_PHOTOSHOP, UnitTestSupport.LICENSE_ID_CANOE_OLD,
                        UnitTestSupport.LICENSE_ID_CANOE_NEW));
    }

    @Test
    public void testFindLicenseById() {
        final License windows = LicenseDao.getInstance().getLicense(UnitTestSupport.LICENSE_ID_WINDOWS);
        assertThat(windows, notNullValue());
        assertThat(windows.getId(), equalTo(UnitTestSupport.LICENSE_ID_WINDOWS));
    }
}
