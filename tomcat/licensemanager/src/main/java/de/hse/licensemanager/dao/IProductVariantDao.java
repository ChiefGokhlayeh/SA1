package de.hse.licensemanager.dao;

import java.util.List;

import de.hse.licensemanager.model.ProductVariant;

public interface IProductVariantDao {

    public ProductVariant getProductVariant(final long id);

    public List<ProductVariant> getProductVariants();

    public void delete(final ProductVariant productVariant);

    public void delete(final long id);

    public void modify(final long idToModify, final ProductVariant other);

    public void refresh(final ProductVariant productVariant);

    public void save(final ProductVariant productVariant);
}
