package de.hse.licensemanager.dao;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import de.hse.licensemanager.model.ServiceGroup;
import de.hse.licensemanager.model.ServiceGroupId;

public class ServiceGroupDao {

    private static ServiceGroupDao dao;

    private final EntityManager em;

    private ServiceGroupDao() {
        em = DaoManager.getEntityManager();
    }

    public static synchronized ServiceGroupDao getInstance() {
        if (dao == null) {
            dao = new ServiceGroupDao();
        }
        return dao;
    }

    public ServiceGroup getServiceGroup(final long serviceGroupId, final long userId) {
        return em.find(ServiceGroup.class, new ServiceGroupId(serviceGroupId, userId));
    }

    public List<ServiceGroup> getServiceGroups() {
        final List<?> objs = em.createQuery("SELECT s FROM ServiceGroup s").getResultList();
        return objs.stream().filter(ServiceGroup.class::isInstance).map(ServiceGroup.class::cast)
                .collect(Collectors.toList());
    }

    public void save(final ServiceGroup serviceGroup) {
        em.getTransaction().begin();
        em.persist(serviceGroup);
        em.getTransaction().commit();
    }
}
