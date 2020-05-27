package de.hse.licensemanager.dao;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class DaoManager {

    private static final String PERSISTENCE_UNIT_NAME = "licensemanager";

    private static DaoManager dm;

    private final EntityManager em;

    private DaoManager() {
        em = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();
    }

    public static synchronized DaoManager getInstance() {
        if (dm == null) {
            dm = new DaoManager();
        }
        return dm;
    }

    public static synchronized EntityManager getEntityManager() {
        return getInstance().em;
    }
}
