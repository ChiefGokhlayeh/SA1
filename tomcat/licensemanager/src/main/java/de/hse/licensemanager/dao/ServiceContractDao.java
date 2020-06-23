package de.hse.licensemanager.dao;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import de.hse.licensemanager.model.Company;
import de.hse.licensemanager.model.ServiceContract;
import de.hse.licensemanager.model.User;

public class ServiceContractDao {

    private static ServiceContractDao dao;

    private final EntityManager em;

    private ServiceContractDao() {
        em = DaoManager.getEntityManager();
    }

    public static synchronized ServiceContractDao getInstance() {
        if (dao == null) {
            dao = new ServiceContractDao();
        }
        return dao;
    }

    public ServiceContract getServiceContract(final long id) {
        return em.find(ServiceContract.class, id);
    }

    public List<ServiceContract> getServiceContracts() {
        final List<?> objs = em.createQuery("SELECT s FROM ServiceContract s").getResultList();
        return objs.stream().filter(ServiceContract.class::isInstance).map(ServiceContract.class::cast)
                .collect(Collectors.toList());
    }

    public List<ServiceContract> getServiceContractsOfUser(final User user) {
        final List<?> objs = em.createQuery("SELECT s FROM ServiceContract s, ServiceGroup sg WHERE sg.user=:user")
                .setParameter("user", user).getResultList();
        return objs.stream().filter(ServiceContract.class::isInstance).map(ServiceContract.class::cast)
                .collect(Collectors.toList());
    }

    public List<ServiceContract> getServiceContractsOfCompany(final Company company) {
        final List<?> objs = em.createQuery("SELECT s FROM ServiceContract s WHERE s.contractor=:contractor")
                .setParameter("contractor", company).getResultList();
        return objs.stream().filter(ServiceContract.class::isInstance).map(ServiceContract.class::cast)
                .collect(Collectors.toList());
    }

    public void delete(long id) {
        delete(em.find(ServiceContract.class, id));
    }

    public void delete(final ServiceContract serviceContract) {
        em.getTransaction().begin();
        em.remove(serviceContract);
        em.getTransaction().commit();
    }

    public void save(final ServiceContract serviceContract) {
        em.getTransaction().begin();
        em.persist(serviceContract);
        em.getTransaction().commit();
    }
}
