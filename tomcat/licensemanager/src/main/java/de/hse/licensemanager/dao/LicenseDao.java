package de.hse.licensemanager.dao;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;

import de.hse.licensemanager.model.License;
import de.hse.licensemanager.model.ServiceContract;
import de.hse.licensemanager.model.User;

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
        Stream<License> stream = objs.stream().filter(License.class::isInstance).map(License.class::cast);
        stream = stream.map((c) -> {
            this.refresh(c);/* this is only needed when running integration tests */
            return c;
        });
        return stream.collect(Collectors.toList());
    }

    @Override
    public List<License> getLicensesByServiceContract(final long id) {
        final List<?> objs = em.createQuery("SELECT l FROM License l WHERE l.serviceContract.id=:id")
                .setParameter("id", id).getResultList();
        Stream<License> stream = objs.stream().filter(License.class::isInstance).map(License.class::cast);
        stream = stream.map((c) -> {
            this.refresh(c);/* this is only needed when running integration tests */
            return c;
        });
        return stream.collect(Collectors.toList());
    }

    @Override
    public List<License> getLicensesByServiceContract(final ServiceContract serviceContract) {
        return getLicensesByServiceContract(serviceContract.getId());
    }

    @Override
    public List<License> getLicensesByUser(final long id) {
        final List<?> objs = em.createQuery(
                "SELECT l FROM License l INNER JOIN ServiceGroup sg ON sg.serviceContract=l.serviceContract WHERE sg.user.id=:id")
                .setParameter("id", id).getResultList();
        Stream<License> stream = objs.stream().filter(License.class::isInstance).map(License.class::cast);
        stream = stream.map((c) -> {
            this.refresh(c);/* this is only needed when running integration tests */
            return c;
        });
        return stream.collect(Collectors.toList());
    }

    @Override
    public List<License> getLicensesByUser(final User user) {
        return getLicensesByUser(user.getId());
    }

    @Override
    public void delete(final License license) {
        em.getTransaction().begin();
        try {
            em.remove(license);
            em.getTransaction().commit();
        } catch (final Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        }
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
        try {
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
        } catch (final Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void refresh(final License license) {
        em.getTransaction().begin();
        try {
            em.refresh(license);
            em.getTransaction().commit();
        } catch (final Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void save(final License license) {
        em.getTransaction().begin();
        try {
            em.persist(license);
            em.getTransaction().commit();
        } catch (final Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        }
    }
}
