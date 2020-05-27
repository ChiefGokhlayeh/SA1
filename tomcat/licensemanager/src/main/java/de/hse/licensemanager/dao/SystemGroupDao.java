package de.hse.licensemanager.dao;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import de.hse.licensemanager.model.SystemGroup;

public class SystemGroupDao {

    private static SystemGroupDao dao;

    private final EntityManager em;

    private SystemGroupDao() {
        em = DaoManager.getEntityManager();
    }

    public static synchronized SystemGroupDao getInstance() {
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

}
