package de.hse.licensemanager.dao;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;

import de.hse.licensemanager.model.Company;

public class CompanyDao implements ICompanyDao {

    private static CompanyDao dao;

    private final EntityManager em;

    private CompanyDao() {
        em = DaoManager.getEntityManager();
    }

    public static synchronized ICompanyDao getInstance() {
        if (dao == null) {
            dao = new CompanyDao();
        }
        return dao;
    }

    @Override
    public Company getCompany(final long id) {
        return em.find(Company.class, id);
    }

    @Override
    public List<Company> getCompanies() {
        final List<?> objs = em.createQuery("SELECT c FROM Company c").getResultList();
        Stream<Company> stream = objs.stream().filter(Company.class::isInstance).map(Company.class::cast);
        stream = stream.map((c) -> {
            this.refresh(c);/* this is only needed when running integration tests */
            return c;
        });
        return stream.collect(Collectors.toList());
    }

    @Override
    public void delete(final Company company) {
        em.getTransaction().begin();
        try {
            company.getDepartments().forEach(em::remove);
            em.remove(company);
            em.getTransaction().commit();
        } catch (final Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void delete(final long id) {
        final Company company = em.find(Company.class, id);
        if (company != null) {
            delete(company);
        }
    }

    @Override
    public void modify(final long idToModify, final Company other) {
        em.getTransaction().begin();
        try {
            final Company company = getCompany(idToModify);
            if (company == null)
                throw new IllegalArgumentException("Unable to find object to modify with id: " + idToModify);

            em.refresh(company);
            company.setName(other.getName());
            company.setAddress(other.getAddress());
            em.flush();
            em.getTransaction().commit();
        } catch (final Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void refresh(final Company company) {
        em.getTransaction().begin();
        try {
            em.refresh(company);
            em.getTransaction().commit();
        } catch (final Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void save(final Company company) {
        em.getTransaction().begin();
        try {
            em.persist(company);
            em.getTransaction().commit();
        } catch (final Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        }
    }
}
