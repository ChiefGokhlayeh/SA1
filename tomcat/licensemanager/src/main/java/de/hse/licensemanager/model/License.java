package de.hse.licensemanager.model;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "t_license")
public class License {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private final long id;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name = "service_contract", nullable = false)
    @JsonIgnore
    private ServiceContract serviceContract;

    @Column(name = "expiration_date")
    private Timestamp expirationDate;

    @Column(name = "`key`", nullable = false)
    private String key;

    @Column(name = "count", nullable = false)
    private int count;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name = "product_variant", nullable = false)
    private ProductVariant productVariant;

    @OneToMany(mappedBy = "license", cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE })
    private final Set<IpMapping> ipMappings = new HashSet<>();

    public License() {
        this(null, null, -1, null);
    }

    public License(final ServiceContract serviceContract, final String key, final int count,
            final ProductVariant productVariant) {
        this(0, serviceContract, null, key, count, productVariant);
    }

    public License(final ServiceContract serviceContract, final Timestamp expirationDate, final String key,
            final int count, final ProductVariant productVariant) {
        this(0, serviceContract, expirationDate, key, count, productVariant);
    }

    public License(final long id, final ServiceContract serviceContract, final Timestamp expirationDate,
            final String key, final int count, final ProductVariant productVariant) {
        this.id = id;
        this.serviceContract = serviceContract;
        this.expirationDate = expirationDate;
        this.key = key;
        this.count = count;
        this.productVariant = productVariant;
    }

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

    public Set<IpMapping> getIpMappings() {
        return ipMappings;
    }

    public void setCount(final int count) {
        this.count = count;
    }

    public void setExpirationDate(final Timestamp expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public void setProductVariant(final ProductVariant productVariant) {
        this.productVariant = productVariant;
    }

    public void setServiceContract(final ServiceContract serviceContract) {
        this.serviceContract = serviceContract;
    }
}
