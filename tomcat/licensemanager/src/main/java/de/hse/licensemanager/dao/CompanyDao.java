package de.hse.licensemanager.dao;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import de.hse.licensemanager.model.Company;

public class CompanyDao {

    private static CompanyDao dao;

    private final EntityManager em;

    private CompanyDao() {
        em = DaoManager.getEntityManager();
    }

    public static synchronized CompanyDao getInstance() {
        if (dao == null) {
            dao = new CompanyDao();
        }
        return dao;
    }

    public Company getCompany(final long id) {
        return em.find(Company.class, id);
    }

    public List<Company> getCompanys() {
        final List<?> objs = em.createQuery("SELECT c FROM Company c").getResultList();
        return objs.stream().filter(Company.class::isInstance).map(Company.class::cast).collect(Collectors.toList());
    }
}
