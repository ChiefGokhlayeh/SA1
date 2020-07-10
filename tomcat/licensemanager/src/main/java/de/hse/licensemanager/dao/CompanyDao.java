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

    public List<Company> getCompanies() {
        final List<?> objs = em.createQuery("SELECT c FROM Company c").getResultList();
        return objs.stream().filter(Company.class::isInstance).map(Company.class::cast).collect(Collectors.toList());
    }

    public void save(final Company company) {
        em.getTransaction().begin();
        em.persist(company);
        em.getTransaction().commit();
    }

    public void delete(final Company company) {
        em.getTransaction().begin();
        company.getDepartments().forEach(em::remove);
        em.remove(company);
        em.getTransaction().commit();
    }

    public void delete(final long id) {
        final Company company = em.find(Company.class, id);
        if (company != null) {
            delete(company);
        }
    }

    public void modify(final long idToModify, final Company other) {
        em.getTransaction().begin();
        final Company company = getCompany(idToModify);
        if (company == null)
            throw new IllegalArgumentException("Unable to find object to modify with id: " + idToModify);

        em.refresh(company);
        company.setName(other.getName());
        company.setAddress(other.getAddress());
        em.flush();
        em.getTransaction().commit();
    }

    public void refresh(final Company company) {
        em.getTransaction().begin();
        em.refresh(company);
        em.getTransaction().commit();
    }
}
