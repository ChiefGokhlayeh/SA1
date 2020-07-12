package de.hse.licensemanager.dao;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import de.hse.licensemanager.model.SystemGroup;

public class SystemGroupDao implements ISystemGroupDao {

    private static SystemGroupDao dao;

    private final EntityManager em;

    private SystemGroupDao() {
        em = DaoManager.getEntityManager();
    }

    public static synchronized ISystemGroupDao getInstance() {
        if (dao == null) {
            dao = new SystemGroupDao();
        }
        return dao;
    }

    public SystemGroup getSystemGroup(final long id) {
        return em.find(SystemGroup.class, id);
    }

    public List<SystemGroup> getSystemGroups() {
        final List<?> objs = em.createQuery("SELECT s FROM SystemGroup s").getResultList();
        return objs.stream().filter(SystemGroup.class::isInstance).map(SystemGroup.class::cast)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(final SystemGroup systemGroup) {
        em.getTransaction().begin();
        try {
            em.remove(systemGroup);
            em.getTransaction().commit();
        } catch (final Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void delete(final long id) {
        final SystemGroup systemGroup = em.find(SystemGroup.class, id);
        if (systemGroup != null) {
            delete(systemGroup);
        }
    }

    @Override
    public void modify(final long idToModify, final SystemGroup other) {
        em.getTransaction().begin();
        try {
            final SystemGroup systemGroup = getSystemGroup(idToModify);
            if (systemGroup == null)
                throw new IllegalArgumentException("Unable to find object to modify with id: " + idToModify);

            em.refresh(systemGroup);
            systemGroup.setDisplayName(other.getDisplayName());
            em.flush();
            em.getTransaction().commit();
        } catch (final Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void refresh(final SystemGroup systemGroup) {
        em.getTransaction().begin();
        try {
            em.refresh(systemGroup);
            em.getTransaction().commit();
        } catch (final Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    public void save(final SystemGroup systemGroup) {
        em.getTransaction().begin();
        try {
            em.persist(systemGroup);
            em.getTransaction().commit();
        } catch (final Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }
}
