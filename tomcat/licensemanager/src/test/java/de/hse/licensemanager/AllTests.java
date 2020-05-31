package de.hse.licensemanager;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.hse.licensemanager.dao.CompanyDaoTest;
import de.hse.licensemanager.dao.IpMappingDaoTest;
import de.hse.licensemanager.dao.LicenseDaoTest;
import de.hse.licensemanager.dao.ProductVariantDaoTest;
import de.hse.licensemanager.dao.ServiceContractDaoTest;
import de.hse.licensemanager.dao.ServiceGroupDaoTest;
import de.hse.licensemanager.dao.SystemGroupDaoTest;
import de.hse.licensemanager.dao.UserDaoTest;

@RunWith(Suite.class)
@SuiteClasses({ CompanyDaoTest.class, IpMappingDaoTest.class, LicenseDaoTest.class, ProductVariantDaoTest.class,
        ServiceContractDaoTest.class, ServiceGroupDaoTest.class, SystemGroupDaoTest.class, UserDaoTest.class, })

public class AllTests {
}
