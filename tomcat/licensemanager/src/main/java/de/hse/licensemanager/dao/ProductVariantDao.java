package de.hse.licensemanager.dao;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import de.hse.licensemanager.model.ProductVariant;

public class ProductVariantDao implements IProductVariantDao {

    private static ProductVariantDao dao;

    private final EntityManager em;

    private ProductVariantDao() {
        em = DaoManager.getEntityManager();
    }

    public static synchronized IProductVariantDao getInstance() {
        if (dao == null) {
            dao = new ProductVariantDao();
        }
        return dao;
    }

    @Override
    public ProductVariant getProductVariant(final long id) {
        return em.find(ProductVariant.class, id);
    }

    @Override
    public List<ProductVariant> getProductVariants() {
        final List<?> objs = em.createQuery("SELECT p FROM ProductVariant p").getResultList();
        return objs.stream().filter(ProductVariant.class::isInstance).map(ProductVariant.class::cast)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(final ProductVariant productVariant) {
        em.getTransaction().begin();
        try {
            em.remove(productVariant);
            em.getTransaction().commit();
        } catch (final Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void delete(final long id) {
        final ProductVariant productVariant = em.find(ProductVariant.class, id);
        if (productVariant != null) {
            delete(productVariant);
        }
    }

    @Override
    public void modify(final long idToModify, final ProductVariant other) {
        em.getTransaction().begin();
        try {
            final ProductVariant productVariant = getProductVariant(idToModify);
            if (productVariant == null)
                throw new IllegalArgumentException("Unable to find object to modify with id: " + idToModify);

            em.refresh(productVariant);
            productVariant.setProduct(other.getProduct());
            productVariant.setVersion(other.getVersion());
            em.flush();
            em.getTransaction().commit();
        } catch (final Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void refresh(final ProductVariant productVariant) {
        em.getTransaction().begin();
        try {
            em.refresh(productVariant);
            em.getTransaction().commit();
        } catch (final Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void save(final ProductVariant productVariant) {
        em.getTransaction().begin();
        try {
            em.persist(productVariant);
            em.getTransaction().commit();
        } catch (final Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }
}
