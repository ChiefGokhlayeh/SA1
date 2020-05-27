package de.hse.licensemanager.dao;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import de.hse.licensemanager.model.User;

public class UserDao {

    private static UserDao dao;

    private final EntityManager em;

    private UserDao() {
        em = DaoManager.getEntityManager();
    }

    public static synchronized UserDao getInstance() {
        if (dao == null) {
            dao = new UserDao();
        }
        return dao;
    }

    public User getUser(final long id) {
        return em.find(User.class, id);
    }

    public List<User> getUsers() {
        final List<?> objs = em.createQuery("SELECT u FROM User u").getResultList();
        return objs.stream().filter(User.class::isInstance).map(User.class::cast).collect(Collectors.toList());
    }
}
