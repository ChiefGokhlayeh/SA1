package de.hse.licensemanager.dao;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import de.hse.licensemanager.model.ServiceContract;

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
}
