package de.hse.licensemanager;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "t_license")
public class License {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "service_contract", nullable = false)
    private ServiceContract serviceContract;

    @Column(name = "expiration_date")
    private Timestamp expirationDate;

    @Column(name = "`key`", nullable = false)
    private String key;

    @Column(name = "count", nullable = false)
    private int count;

    @ManyToOne
    @JoinColumn(name = "product_variant", nullable = false)
    private ProductVariant productVariant;

    public long getId() {
        return id;
    }

    public ServiceContract getServiceContract() {
        return serviceContract;
    }

    public Timestamp getExpirationDate() {
        return expirationDate;
    }

    public String getKey() {
        return key;
    }

    public int getCount() {
        return count;
    }

    public ProductVariant getProductVariant() {
        return productVariant;
    }
}
