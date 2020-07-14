package de.hse.licensemanager.dao;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import de.hse.licensemanager.model.Credentials;

public class CredentialsDao implements ICredentialsDao {
    private static CredentialsDao dao;

    private final EntityManager em;

    private CredentialsDao() {
        em = DaoManager.getEntityManager();
    }

    public static synchronized ICredentialsDao getInstance() {
        if (dao == null) {
            dao = new CredentialsDao();
        }
        return dao;
    }

    @Override
    public Credentials getCredentials(final long id) {
        return em.find(Credentials.class, id);
    }

    @Override
    public Credentials getCredentialsByLoginname(final String login) {
        try {
            return (Credentials) em.createQuery("SELECT u FROM Credentials u WHERE u.loginname=:login")
                    .setParameter("login", login).getSingleResult();
        } catch (final NoResultException e) {
            /* No user has been found. Indicate result via null value. */
            return null;
        }
    }

    @Override
    public List<Credentials> getCredentials() {
        final List<?> objs = em.createQuery("SELECT u FROM Credentials u").getResultList();
        return objs.stream().filter(Credentials.class::isInstance).map(Credentials.class::cast)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(final Credentials credentials) {
        em.getTransaction().begin();
        try {
            em.remove(credentials);
            em.getTransaction().commit();
        } catch (final Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void delete(final long id) {
        final Credentials credentials = em.find(Credentials.class, id);
        if (credentials != null) {
            delete(credentials);
        }
    }

    @Override
    public void modify(final long idToModify, final Credentials other) {
        em.getTransaction().begin();
        try {
            final Credentials credentials = getCredentials(idToModify);
            if (credentials == null)
                throw new IllegalArgumentException("Unable to find object to modify with id: " + idToModify);

            em.refresh(credentials);
            credentials.setLoginname(other.getLoginname());
            credentials.setPasswordHash(other.getPasswordHash());
            credentials.setPasswordSalt(other.getPasswordSalt());
            credentials.setPasswordIterations(other.getPasswordIterations());
            em.flush();
            em.getTransaction().commit();
        } catch (final Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void refresh(final Credentials credentials) {
        em.getTransaction().begin();
        try {
            em.refresh(credentials);
            em.getTransaction().commit();
        } catch (final Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void save(final Credentials credentials) {
        em.getTransaction().begin();
        try {
            em.persist(credentials);
            em.getTransaction().commit();
        } catch (final Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        }
    }
}
