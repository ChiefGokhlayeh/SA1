package de.hse.licensemanager.dao;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import de.hse.licensemanager.model.Company;
import de.hse.licensemanager.model.ServiceContract;

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
    public List<ServiceContract> getServiceContractsByCompany(final Company company) {
        final List<?> objs = em.createQuery("SELECT s FROM ServiceContract s WHERE s.contractor=:contractor")
                .setParameter("contractor", company).getResultList();
        return objs.stream().filter(ServiceContract.class::isInstance).map(ServiceContract.class::cast)
                .collect(Collectors.toList());
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
        em.remove(serviceContract);
        em.getTransaction().commit();
    }

    @Override
    public void modify(final long idToModify, final ServiceContract other) {
        em.getTransaction().begin();
        final ServiceContract serviceContract = getServiceContract(idToModify);
        if (serviceContract == null)
            throw new IllegalArgumentException("Unable to find object to modify with id: " + idToModify);

        em.refresh(serviceContract);
        serviceContract.setContractor(other.getContractor());
        serviceContract.setEnd(other.getEnd());
        serviceContract.setStart(other.getStart());
        em.flush();
        em.getTransaction().commit();
    }

    @Override
    public void refresh(final ServiceContract serviceContract) {
        em.getTransaction().begin();
        em.refresh(serviceContract);
        em.getTransaction().commit();
    }

    @Override
    public void save(final ServiceContract serviceContract) {
        em.getTransaction().begin();
        em.persist(serviceContract);
        em.getTransaction().commit();
    }
}
