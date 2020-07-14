package de.hse.licensemanager.dao;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;

import de.hse.licensemanager.model.User;

public class UserDao implements IUserDao {

    private static UserDao dao;

    private final EntityManager em;

    private UserDao() {
        em = DaoManager.getEntityManager();
    }

    public static synchronized IUserDao getInstance() {
        if (dao == null) {
            dao = new UserDao();
        }
        return dao;
    }

    @Override
    public List<User> getUsersByCompany(final long id) {
        final List<?> objs = em.createQuery("SELECT u FROM User u WHERE u.companyDepartment.company.id=:id")
                .setParameter("id", id).getResultList();
        Stream<User> stream = objs.stream().filter(User.class::isInstance).map(User.class::cast);
        stream = stream.map((c) -> {
            this.refresh(c);/* this is only needed when running integration tests */
            return c;
        });
        return stream.collect(Collectors.toList());
    }

    @Override
    public List<User> getUsersByCompanyDepartment(final long id) {
        final List<?> objs = em.createQuery("SELECT u FROM User u WHERE u.companyDepartment.id=:id")
                .setParameter("id", id).getResultList();
        Stream<User> stream = objs.stream().filter(User.class::isInstance).map(User.class::cast);
        stream = stream.map((c) -> {
            this.refresh(c);/* this is only needed when running integration tests */
            return c;
        });
        return stream.collect(Collectors.toList());
    }

    @Override
    public List<User> getUsers() {
        final List<?> objs = em.createQuery("SELECT u FROM User u").getResultList();
        Stream<User> stream = objs.stream().filter(User.class::isInstance).map(User.class::cast);
        stream = stream.map((c) -> {
            this.refresh(c);/* this is only needed when running integration tests */
            return c;
        });
        return stream.collect(Collectors.toList());
    }

    @Override
    public User getUser(final long id) {
        return em.find(User.class, id);
    }

    @Override
    public void delete(final User user) {
        em.getTransaction().begin();
        try {
            em.remove(user);
            em.getTransaction().commit();
        } catch (final Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void delete(final long id) {
        final User user = em.find(User.class, id);
        if (user != null) {
            delete(user);
        }
    }

    @Override
    public void modify(final long idToModify, final User other) {
        em.getTransaction().begin();
        try {
            final User user = getUser(idToModify);
            if (user == null)
                throw new IllegalArgumentException("Unable to find object to modify with id: " + idToModify);

            em.refresh(user);
            user.setActive(other.isActive());
            if (other.getCompanyDepartment() != null)
                user.setCompanyDepartment(other.getCompanyDepartment());
            if (other.getCredentials() != null)
                user.setCredentials(other.getCredentials());
            if (other.getEmail() != null)
                user.setEmail(other.getEmail());
            if (other.getFirstname() != null)
                user.setFirstname(other.getFirstname());
            if (other.getLastname() != null)
                user.setLastname(other.getLastname());
            if (other.getGroup() != null)
                user.setGroup(other.getGroup());
            em.flush();
            em.getTransaction().commit();
        } catch (final Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void refresh(final User user) {
        em.getTransaction().begin();
        try {
            em.refresh(user);
            em.getTransaction().commit();
        } catch (final Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void save(final User user) {
        em.getTransaction().begin();
        try {
            em.persist(user);
            em.getTransaction().commit();
        } catch (final Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        }
    }
}
