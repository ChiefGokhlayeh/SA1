package de.hse.licensemanager.dao;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.BeforeClass;
import org.junit.Test;

import de.hse.licensemanager.PrepareTests;
import de.hse.licensemanager.model.SystemGroup;

public class SystemGroupDaoTest {

    private static final long SYSTEM_GROUP_ID_ADMIN;
    private static final long SYSTEM_GROUP_ID_USER;

    private static final String SYSTEM_GROUP_DISPLAYNAME_ADMIN = "admin";
    private static final String SYSTEM_GROUP_DISPLAYNAME_USER = "user";

    static {
        long id = 1;
        SYSTEM_GROUP_ID_ADMIN = id++;
        SYSTEM_GROUP_ID_USER = id++;
    }

    @BeforeClass
    public static void SetUpBeforeClass() {
        PrepareTests.initDatabase();

        final EntityManager em = DaoManager.getEntityManager();

        final EntityTransaction et = em.getTransaction();
        et.begin();

        int param = 1;
        em.createNativeQuery("INSERT INTO t_system_group (id, displayname) VALUES (?1, ?2)")
                .setParameter(param++, SYSTEM_GROUP_ID_ADMIN).setParameter(param++, SYSTEM_GROUP_DISPLAYNAME_ADMIN)
                .executeUpdate();

        param = 1;
        em.createNativeQuery("INSERT INTO t_system_group (id, displayname) VALUES (?1, ?2)")
                .setParameter(param++, SYSTEM_GROUP_ID_USER).setParameter(param++, SYSTEM_GROUP_DISPLAYNAME_USER)
                .executeUpdate();

        et.commit();
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
        final SystemGroup admin = SystemGroupDao.getInstance().getSystemGroup(SYSTEM_GROUP_ID_ADMIN);
        assertThat(admin, notNullValue());
    }

    @Test
    public void testFoundUserDataPopulated() {
        final SystemGroup admin = SystemGroupDao.getInstance().getSystemGroup(SYSTEM_GROUP_ID_ADMIN);

        assertThat(admin.getId(), equalTo(SYSTEM_GROUP_ID_ADMIN));
        assertThat(admin.getDisplayName(), equalTo(SYSTEM_GROUP_DISPLAYNAME_ADMIN));
    }
}
