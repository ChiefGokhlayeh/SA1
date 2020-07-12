package de.hse.licensemanager.dao;

import java.util.List;
import java.util.stream.Collectors;

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

    public User getUser(final long id) {
        return em.find(User.class, id);
    }

    @Override
    public List<User> getUsers() {
        final List<?> objs = em.createQuery("SELECT u FROM User u").getResultList();
        return objs.stream().filter(User.class::isInstance).map(User.class::cast).collect(Collectors.toList());
    }

    @Override
    public void delete(final User user) {
        em.getTransaction().begin();
        try {
            em.remove(user);
            em.getTransaction().commit();
        } catch (final Exception e) {
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
            user.setCompanyDepartment(other.getCompanyDepartment());
            user.setCredentials(other.getCredentials());
            user.setEmail(other.getEmail());
            user.setFirstname(other.getFirstname());
            user.setLastname(other.getLastname());
            user.setSystemGroup(other.getSystemGroup());
            user.setVerified(other.isVerified());
            em.flush();
            em.getTransaction().commit();
        } catch (final Exception e) {
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
            em.getTransaction().rollback();
            throw e;
        }
    }
}
