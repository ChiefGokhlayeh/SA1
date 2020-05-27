package de.hse.licensemanager;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.hse.licensemanager.dao.CompanyDaoTest;
import de.hse.licensemanager.dao.ServiceContractDaoTest;
import de.hse.licensemanager.dao.SystemGroupDaoTest;
import de.hse.licensemanager.dao.UserDaoTest;

@RunWith(Suite.class)
@SuiteClasses({ CompanyDaoTest.class, ServiceContractDaoTest.class, UserDaoTest.class, SystemGroupDaoTest.class, })

public class AllTests {
}
