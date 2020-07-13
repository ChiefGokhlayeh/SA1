package de.hse.licensemanager.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "t_ip_mapping")
public class IpMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name = "license", nullable = false)
    @JsonIgnore
    private License license;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    public IpMapping() {
        this(0, null, null);
    }

    public IpMapping(final License license, final String ipAddress) {
        this(0, license, ipAddress);
    }

    public IpMapping(final long id, final License license, final String ipAddress) {
        this.id = id;
        this.license = license;
        this.ipAddress = ipAddress;
    }

    public long getId() {
        return id;
    }

    public License getLicense() {
        return license;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setLicense(final License license) {
        this.license = license;
    }

    public void setIpAddress(final String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
