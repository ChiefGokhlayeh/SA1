package de.hse.licensemanager.dao;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import de.hse.licensemanager.model.ProductVariant;

public class ProductVariantDao {

    private static ProductVariantDao dao;

    private final EntityManager em;

    private ProductVariantDao() {
        em = DaoManager.getEntityManager();
    }

    public static synchronized ProductVariantDao getInstance() {
        if (dao == null) {
            dao = new ProductVariantDao();
        }
        return dao;
    }

    public ProductVariant getProductVariant(final long id) {
        return em.find(ProductVariant.class, id);
    }

    public List<ProductVariant> getProductVariants() {
        final List<?> objs = em.createQuery("SELECT p FROM ProductVariant p").getResultList();
        return objs.stream().filter(ProductVariant.class::isInstance).map(ProductVariant.class::cast)
                .collect(Collectors.toList());
    }
}
