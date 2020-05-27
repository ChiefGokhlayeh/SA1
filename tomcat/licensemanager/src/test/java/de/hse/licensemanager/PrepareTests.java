package de.hse.licensemanager;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.Test;

import de.hse.licensemanager.dao.DaoManager;

public class PrepareTests {

    @Test
    public void clearDatabase() {
        initDatabase();
    }

    public static void initDatabase() {
        EntityManager em = DaoManager.getEntityManager();

        EntityTransaction et = em.getTransaction();
        et.begin();

        em.createNativeQuery("DELETE FROM t_service_group").executeUpdate();
        em.createNativeQuery("DELETE FROM t_ip_mapping").executeUpdate();
        em.createNativeQuery("DELETE FROM t_license").executeUpdate();
        em.createNativeQuery("DELETE FROM t_product_variant").executeUpdate();
        em.createNativeQuery("DELETE FROM t_service_contract").executeUpdate();
        em.createNativeQuery("DELETE FROM t_user").executeUpdate();
        em.createNativeQuery("DELETE FROM t_company_department").executeUpdate();
        em.createNativeQuery("DELETE FROM t_company").executeUpdate();
        em.createNativeQuery("DELETE FROM t_system_group").executeUpdate();

        et.commit();
    }
}
