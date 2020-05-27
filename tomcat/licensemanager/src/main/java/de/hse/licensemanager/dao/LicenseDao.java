package de.hse.licensemanager.dao;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import de.hse.licensemanager.model.License;

public class LicenseDao {

    private static LicenseDao dao;

    private final EntityManager em;

    private LicenseDao() {
        em = DaoManager.getEntityManager();
    }

    public static synchronized LicenseDao getInstance() {
        if (dao == null) {
            dao = new LicenseDao();
        }
        return dao;
    }

    public License getLicense(final long id) {
        return em.find(License.class, id);
    }

    public List<License> getLicenses() {
        final List<?> objs = em.createQuery("SELECT l FROM License l").getResultList();
        return objs.stream().filter(License.class::isInstance).map(License.class::cast).collect(Collectors.toList());
    }

}
