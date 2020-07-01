package de.hse.licensemanager.dao;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import de.hse.licensemanager.model.Credentials;

public class CredentialsDao {
    private static CredentialsDao dao;

    private final EntityManager em;

    private CredentialsDao() {
        em = DaoManager.getEntityManager();
    }

    public static synchronized CredentialsDao getInstance() {
        if (dao == null) {
            dao = new CredentialsDao();
        }
        return dao;
    }

    public Credentials getCredentials(final long id) {
        return em.find(Credentials.class, id);
    }

    public Credentials getCredentialsByLoginname(final String login) {
        try {
            return (Credentials) em.createQuery("SELECT u FROM Credentials u WHERE u.loginname=:login")
                    .setParameter("login", login).getSingleResult();
        } catch (final NoResultException e) {
            /* No user has been found. Indicate result via null value. */
            return null;
        }
    }

    public List<Credentials> getCredentials() {
        final List<?> objs = em.createQuery("SELECT u FROM Credentials u").getResultList();
        return objs.stream().filter(Credentials.class::isInstance).map(Credentials.class::cast)
                .collect(Collectors.toList());
    }

    public void save(final Credentials credentials) {
        em.getTransaction().begin();
        em.persist(credentials);
        em.getTransaction().commit();
    }

    public void delete(final long id) {
        final Credentials credentials = em.find(Credentials.class, id);
        if (credentials != null) {
            em.getTransaction().begin();
            em.remove(credentials);
            em.getTransaction().commit();
        }
    }
}
