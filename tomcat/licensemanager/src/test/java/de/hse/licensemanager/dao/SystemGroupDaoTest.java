package de.hse.licensemanager.dao;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.PrepareTests;
import de.hse.licensemanager.model.SystemGroup;

public class SystemGroupDaoTest {

    @Before
    public void setupBeforeTest() {
        PrepareTests.initDatabase();
    }

    @Test
    public void testInstanceAllocated() {
        assertThat(SystemGroupDao.getInstance(), notNullValue());
    }

    @Test
    public void testInstanceSingleton() {
        final SystemGroupDao a = SystemGroupDao.getInstance();
        final SystemGroupDao b = SystemGroupDao.getInstance();
        assertThat(a, sameInstance(b));
    }

    @Test
    public void testFindSystemGroupById() {
        final SystemGroup admin = SystemGroupDao.getInstance().getSystemGroup(PrepareTests.SYSTEM_GROUP_ID_ADMIN);
        assertThat(admin, notNullValue());
    }

    @Test
    public void testFoundUserDataPopulated() {
        final SystemGroup admin = SystemGroupDao.getInstance().getSystemGroup(PrepareTests.SYSTEM_GROUP_ID_ADMIN);

        assertThat(admin.getId(), equalTo(PrepareTests.SYSTEM_GROUP_ID_ADMIN));
        assertThat(admin.getDisplayName(), equalTo(PrepareTests.SYSTEM_GROUP_DISPLAYNAME_ADMIN));
    }

    @Test
    public void testSave() {
        final SystemGroup systemGroup = new SystemGroup();
        systemGroup.setDisplayName("test");

        SystemGroupDao.getInstance().save(systemGroup);

        final List<SystemGroup> systemGroups = SystemGroupDao.getInstance().getSystemGroups();

        assertThat(systemGroup.getId(),
                is(in(systemGroups.stream().map((s) -> s.getId()).collect(Collectors.toList()))));
    }
}
