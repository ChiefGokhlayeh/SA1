package de.hse.licensemanager.dao;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import de.hse.licensemanager.model.ServiceGroup;
import de.hse.licensemanager.model.ServiceGroupId;

public class ServiceGroupDao implements IServiceGroupDao {

    private static ServiceGroupDao dao;

    private final EntityManager em;

    private ServiceGroupDao() {
        em = DaoManager.getEntityManager();
    }

    public static synchronized IServiceGroupDao getInstance() {
        if (dao == null) {
            dao = new ServiceGroupDao();
        }
        return dao;
    }

    @Override
    public List<ServiceGroup> getServiceGroups() {
        final List<?> objs = em.createQuery("SELECT s FROM ServiceGroup s").getResultList();
        return objs.stream().filter(ServiceGroup.class::isInstance).map(ServiceGroup.class::cast)
                .collect(Collectors.toList());
    }

    @Override
    public ServiceGroup getServiceGroup(final long serviceContractId, final long userId) {
        return em.find(ServiceGroup.class, new ServiceGroupId(serviceContractId, userId));
    }

    @Override
    public ServiceGroup getServiceGroup(final ServiceGroupId id) {
        return em.find(ServiceGroup.class, id);
    }

    @Override
    public void delete(final ServiceGroup serviceGroup) {
        em.getTransaction().begin();
        em.remove(serviceGroup);
        em.getTransaction().commit();
    }

    @Override
    public void delete(final long id) {
        final ServiceGroup serviceGroup = em.find(ServiceGroup.class, id);
        if (serviceGroup != null) {
            delete(serviceGroup);
        }
    }

    @Override
    public void modify(final long serviceContractId, final long userId, final ServiceGroup other) {
        modify(new ServiceGroupId(serviceContractId, userId), other);
    }

    @Override
    public void modify(final ServiceGroupId idToModify, final ServiceGroup other) {
        em.getTransaction().begin();
        final ServiceGroup serviceGroup = getServiceGroup(idToModify);
        if (serviceGroup == null)
            throw new IllegalArgumentException("Unable to find object to modify with id: " + idToModify);

        em.refresh(serviceGroup);
        serviceGroup.setServiceContract(other.getServiceContract());
        serviceGroup.setUser(other.getUser());
        em.flush();
        em.getTransaction().commit();
    }

    @Override
    public void refresh(final ServiceGroup serviceGroup) {
        em.getTransaction().begin();
        em.refresh(serviceGroup);
        em.getTransaction().commit();
    }

    @Override
    public void save(final ServiceGroup serviceGroup) {
        em.getTransaction().begin();
        em.persist(serviceGroup);
        em.getTransaction().commit();
    }
}
