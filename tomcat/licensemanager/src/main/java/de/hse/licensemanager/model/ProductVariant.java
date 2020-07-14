package de.hse.licensemanager.model;

import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

import java.util.Objects;

import javax.persistence.Column;

@Entity
@Table(name = "t_product_variant")
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "product", nullable = false)
    private String product;

    @Column(name = "version", nullable = false)
    private String version;

    public ProductVariant() {
        this(null, null);
    }

    public ProductVariant(final String product, final String version) {
        this(0, product, version);
    }

    public ProductVariant(final long id, final String product, final String version) {
        this.id = id;
        this.product = product;
        this.version = version;
    }

    public long getId() {
        return id;
    }

    public String getProduct() {
        return product;
    }

    public String getVersion() {
        return version;
    }

    public void setProduct(final String product) {
        this.product = product;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, product, version);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other)
            return true;

        if (other == null || other.getClass() != this.getClass())
            return false;

        final ProductVariant otherProductVariant = (ProductVariant) other;
        return Objects.equals(this.id, otherProductVariant.id)
                && Objects.equals(this.product, otherProductVariant.product)
                && Objects.equals(this.version, otherProductVariant.version);
    }

    @Override
    public String toString() {
        return String.format("{ id: %d, product: %s, version: %s }", id, product, version);
    }
}
