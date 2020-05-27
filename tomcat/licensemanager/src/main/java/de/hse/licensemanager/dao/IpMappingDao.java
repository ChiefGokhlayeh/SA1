package de.hse.licensemanager.dao;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import de.hse.licensemanager.model.IpMapping;

public class IpMappingDao {

    private static IpMappingDao dao;

    private final EntityManager em;

    private IpMappingDao() {
        em = DaoManager.getEntityManager();
    }

    public static synchronized IpMappingDao getInstance() {
        if (dao == null) {
            dao = new IpMappingDao();
        }
        return dao;
    }

    public IpMapping getIpMapping(final long id) {
        return em.find(IpMapping.class, id);
    }

    public List<IpMapping> getIpMappings() {
        final List<?> objs = em.createQuery("SELECT i FROM IpMapping i").getResultList();
        return objs.stream().filter(IpMapping.class::isInstance).map(IpMapping.class::cast)
                .collect(Collectors.toList());
    }

}
