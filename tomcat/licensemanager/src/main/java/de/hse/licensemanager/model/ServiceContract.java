package de.hse.licensemanager.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.sql.Date;
import java.util.Set;

@Entity
@Table(name = "t_service_contract")
public class ServiceContract {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "contractor", nullable = false)
    private Company contractor;

    @Column(name = "start")
    private Date start;

    @Column(name = "end")
    private Date end;

    @ManyToMany(mappedBy = "serviceContracts")
    private Set<User> users;

    @OneToMany(mappedBy = "serviceContract")
    @JoinColumn(name = "service_contract")
    private Set<License> licenses;

    public long getId() {
        return id;
    }

    public Company getContractor() {
        return contractor;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public Set<License> getLicenses() {
        return licenses;
    }
}
