package de.hse.licensemanager.model;

import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "t_ip_mapping")
public class IpMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne(cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "license", nullable = false)
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

    @Override
    public int hashCode() {
        return Objects.hash(id, ipAddress);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other)
            return true;

        if (other == null || other.getClass() != this.getClass())
            return false;

        final IpMapping otherIpMapping = (IpMapping) other;
        return Objects.equals(this.id, otherIpMapping.id) && Objects.equals(this.ipAddress, otherIpMapping.ipAddress);
    }

    @Override
    public String toString() {
        return String.format("{ id: %d, ipAddress: %s }", id, ipAddress);
    }
}
