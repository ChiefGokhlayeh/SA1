package de.hse.licensemanager.model;

import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

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
}
