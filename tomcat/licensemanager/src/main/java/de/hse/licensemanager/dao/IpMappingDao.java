package de.hse.licensemanager.dao;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;

import de.hse.licensemanager.model.IpMapping;
import de.hse.licensemanager.model.License;

public class IpMappingDao implements IIpMappingDao {

    private static IpMappingDao dao;

    private final EntityManager em;

    private IpMappingDao() {
        em = DaoManager.getEntityManager();
    }

    public static synchronized IIpMappingDao getInstance() {
        if (dao == null) {
            dao = new IpMappingDao();
        }
        return dao;
    }

    @Override
    public List<IpMapping> getIpMappingsByLicense(final long id) {
        final List<?> objs = em.createQuery("SELECT i FROM IpMapping i WHERE i.license.id=:id").setParameter("id", id)
                .getResultList();
        Stream<IpMapping> stream = objs.stream().filter(IpMapping.class::isInstance).map(IpMapping.class::cast);
        stream = stream.map((c) -> {
            this.refresh(c);/* this is only needed when running integration tests */
            return c;
        });
        return stream.collect(Collectors.toList());
    }

    @Override
    public List<IpMapping> getIpMappingsByLicense(final License license) {
        return getIpMappingsByLicense(license.getId());
    }

    @Override
    public IpMapping getIpMapping(final long id) {
        return em.find(IpMapping.class, id);
    }

    @Override
    public List<IpMapping> getIpMappings() {
        final List<?> objs = em.createQuery("SELECT i FROM IpMapping i").getResultList();
        Stream<IpMapping> stream = objs.stream().filter(IpMapping.class::isInstance).map(IpMapping.class::cast);
        stream = stream.map((c) -> {
            this.refresh(c);/* this is only needed when running integration tests */
            return c;
        });
        return stream.collect(Collectors.toList());
    }

    @Override
    public void delete(final IpMapping ipMapping) {
        em.getTransaction().begin();
        try {
            em.remove(ipMapping);
            em.getTransaction().commit();
        } catch (final Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void delete(final long id) {
        final IpMapping ipMapping = em.find(IpMapping.class, id);
        if (ipMapping != null) {
            delete(ipMapping);
        }
    }

    @Override
    public void modify(final long idToModify, final IpMapping other) {
        em.getTransaction().begin();
        try {
            final IpMapping ipMapping = getIpMapping(idToModify);
            if (ipMapping == null)
                throw new IllegalArgumentException("Unable to find object to modify with id: " + idToModify);

            em.refresh(ipMapping);
            ipMapping.setIpAddress(other.getIpAddress());
            ipMapping.setLicense(other.getLicense());
            em.flush();
            em.getTransaction().commit();
        } catch (final Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void refresh(final IpMapping ipMapping) {
        em.getTransaction().begin();
        try {
            em.refresh(ipMapping);
            em.getTransaction().commit();
        } catch (final Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void save(final IpMapping ipMapping) {
        em.getTransaction().begin();
        try {
            em.persist(ipMapping);
            em.getTransaction().commit();
        } catch (final Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }
}
