package de.hse.licensemanager.dao;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import de.hse.licensemanager.model.Company;
import de.hse.licensemanager.model.ServiceContract;
import de.hse.licensemanager.model.User;

public class ServiceContractDao implements IServiceContractDao {

    private static ServiceContractDao dao;

    private final EntityManager em;

    private ServiceContractDao() {
        em = DaoManager.getEntityManager();
    }

    public static synchronized IServiceContractDao getInstance() {
        if (dao == null) {
            dao = new ServiceContractDao();
        }
        return dao;
    }

    @Override
    public List<ServiceContract> getServiceContracts() {
        final List<?> objs = em.createQuery("SELECT s FROM ServiceContract s").getResultList();
        return objs.stream().filter(ServiceContract.class::isInstance).map(ServiceContract.class::cast)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceContract> getServiceContractsByContractor(final Company contractor) {
        return getServiceContractsByContractor(contractor.getId());
    }

    @Override
    public List<ServiceContract> getServiceContractsByContractor(final long id) {
        final List<?> objs = em.createQuery("SELECT s FROM ServiceContract s WHERE s.contractor.id=:id")
                .setParameter("id", id).getResultList();
        return objs.stream().filter(ServiceContract.class::isInstance).map(ServiceContract.class::cast)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceContract> getServiceContractsByUser(final long id) {
        final List<?> objs = em.createQuery(
                "SELECT s FROM ServiceContract s INNER JOIN ServiceGroup sg ON sg.serviceContract=s WHERE sg.user.id=:id")
                .setParameter("id", id).getResultList();
        return objs.stream().filter(ServiceContract.class::isInstance).map(ServiceContract.class::cast)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceContract> getServiceContractsByUser(final User user) {
        return getServiceContractsByUser(user.getId());
    }

    @Override
    public ServiceContract getServiceContract(final long id) {
        return em.find(ServiceContract.class, id);
    }

    @Override
    public void delete(final long id) {
        delete(em.find(ServiceContract.class, id));
    }

    @Override
    public void delete(final ServiceContract serviceContract) {
        em.getTransaction().begin();
        try {
            em.remove(serviceContract);
            em.getTransaction().commit();
        } catch (final Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void modify(final long idToModify, final ServiceContract other) {
        em.getTransaction().begin();
        try {
            final ServiceContract serviceContract = getServiceContract(idToModify);
            if (serviceContract == null)
                throw new IllegalArgumentException("Unable to find object to modify with id: " + idToModify);

            em.refresh(serviceContract);
            serviceContract.setContractor(other.getContractor());
            serviceContract.setEnd(other.getEnd());
            serviceContract.setStart(other.getStart());
            em.flush();
            em.getTransaction().commit();
        } catch (final Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void refresh(final ServiceContract serviceContract) {
        em.getTransaction().begin();
        try {
            em.refresh(serviceContract);
            em.getTransaction().commit();
        } catch (final Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void save(final ServiceContract serviceContract) {
        em.getTransaction().begin();
        try {
            em.persist(serviceContract);
            em.getTransaction().commit();
        } catch (final Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        }
    }
}
