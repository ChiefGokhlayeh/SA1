package de.hse.licensemanager.dao;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import de.hse.licensemanager.model.License;

public class LicenseDao implements ILicenseDao {

    private static LicenseDao dao;

    private final EntityManager em;

    private LicenseDao() {
        em = DaoManager.getEntityManager();
    }

    public static synchronized ILicenseDao getInstance() {
        if (dao == null) {
            dao = new LicenseDao();
        }
        return dao;
    }

    @Override
    public License getLicense(final long id) {
        return em.find(License.class, id);
    }

    @Override
    public List<License> getLicenses() {
        final List<?> objs = em.createQuery("SELECT l FROM License l").getResultList();
        return objs.stream().filter(License.class::isInstance).map(License.class::cast).collect(Collectors.toList());
    }

    @Override
    public void delete(final License license) {
        em.getTransaction().begin();
        em.remove(license);
        em.getTransaction().commit();
    }

    @Override
    public void delete(final long id) {
        final License license = em.find(License.class, id);
        if (license != null) {
            delete(license);
        }
    }

    @Override
    public void modify(final long idToModify, final License other) {
        em.getTransaction().begin();
        final License license = getLicense(idToModify);
        if (license == null)
            throw new IllegalArgumentException("Unable to find object to modify with id: " + idToModify);

        em.refresh(license);
        license.setCount(other.getCount());
        license.setExpirationDate(other.getExpirationDate());
        license.setKey(other.getKey());
        license.setProductVariant(other.getProductVariant());
        license.setServiceContract(other.getServiceContract());
        em.flush();
        em.getTransaction().commit();
    }

    @Override
    public void refresh(final License license) {
        em.getTransaction().begin();
        em.refresh(license);
        em.getTransaction().commit();
    }

    @Override
    public void save(final License license) {
        em.getTransaction().begin();
        em.persist(license);
        em.getTransaction().commit();
    }
}
