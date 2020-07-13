package de.hse.licensemanager.dao;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import de.hse.licensemanager.model.CompanyDepartment;;

public class CompanyDepartmentDao implements ICompanyDepartmentDao {

    private static CompanyDepartmentDao dao;

    private final EntityManager em;

    private CompanyDepartmentDao() {
        em = DaoManager.getEntityManager();
    }

    public static synchronized ICompanyDepartmentDao getInstance() {
        if (dao == null) {
            dao = new CompanyDepartmentDao();
        }
        return dao;
    }

    @Override
    public List<CompanyDepartment> getCompanyDepartmentsByCompany(final long id) {
        final List<?> objs = em.createQuery("SELECT d FROM CompanyDepartment d WHERE d.company.id=:id")
                .setParameter("id", id).getResultList();
        return objs.stream().filter(CompanyDepartment.class::isInstance).map(CompanyDepartment.class::cast)
                .collect(Collectors.toList());
    }

    @Override
    public CompanyDepartment getCompanyDepartment(final long id) {
        return em.find(CompanyDepartment.class, id);
    }

    @Override
    public List<CompanyDepartment> getCompanyDepartments() {
        final List<?> objs = em.createQuery("SELECT c FROM CompanyDepartment c").getResultList();
        return objs.stream().filter(CompanyDepartment.class::isInstance).map(CompanyDepartment.class::cast)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(final CompanyDepartment department) {
        em.getTransaction().begin();
        try {
            em.remove(department);
            em.getTransaction().commit();
        } catch (final Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void delete(final long id) {
        final CompanyDepartment department = em.find(CompanyDepartment.class, id);
        if (department != null) {
            delete(department);
        }
    }

    @Override
    public void modify(final long idToModify, final CompanyDepartment other) {
        em.getTransaction().begin();
        try {
            final CompanyDepartment department = getCompanyDepartment(idToModify);
            if (department == null)
                throw new IllegalArgumentException("Unable to find object to modify with id: " + idToModify);

            em.refresh(department);
            department.setName(other.getName());
            department.setCompany(other.getCompany());
            em.flush();
            em.getTransaction().commit();
        } catch (final Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void refresh(final CompanyDepartment department) {
        em.getTransaction().begin();
        try {
            em.refresh(department);
            em.getTransaction().commit();
        } catch (final Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void save(final CompanyDepartment department) {
        em.getTransaction().begin();
        try {
            em.persist(department);
            em.getTransaction().commit();
        } catch (final Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }
}
