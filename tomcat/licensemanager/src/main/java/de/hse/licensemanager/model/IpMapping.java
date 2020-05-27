package de.hse.licensemanager.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "t_ip_mapping")
public class IpMapping {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private long id;

    @OneToOne
    @JoinColumn(name = "license", nullable = false)
    private License license;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    public long getId() {
        return id;
    }

    public License getLicense() {
        return license;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}
