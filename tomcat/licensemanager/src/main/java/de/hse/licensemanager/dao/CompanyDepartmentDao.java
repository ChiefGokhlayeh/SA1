package de.hse.licensemanager.dao;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import de.hse.licensemanager.model.CompanyDepartment;;

public class CompanyDepartmentDao {

    private static CompanyDepartmentDao dao;

    private final EntityManager em;

    private CompanyDepartmentDao() {
        em = DaoManager.getEntityManager();
    }

    public static synchronized CompanyDepartmentDao getInstance() {
        if (dao == null) {
            dao = new CompanyDepartmentDao();
        }
        return dao;
    }

    public CompanyDepartment getCompanyDepartment(final long id) {
        return em.find(CompanyDepartment.class, id);
    }

    public List<CompanyDepartment> getCompanyDepartments() {
        final List<?> objs = em.createQuery("SELECT c FROM CompanyDepartment c").getResultList();
        return objs.stream().filter(CompanyDepartment.class::isInstance).map(CompanyDepartment.class::cast)
                .collect(Collectors.toList());
    }

    public void save(final CompanyDepartment department) {
        em.getTransaction().begin();
        em.persist(department);
        em.getTransaction().commit();
    }
}
