package de.hse.licensemanager.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.sql.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "t_service_contract")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@query_id")
public class ServiceContract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private final long id;

    @JoinColumn(name = "contractor", nullable = false)
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
    private Company contractor;

    @Column(name = "start")
    private Date start;

    @Column(name = "end")
    private Date end;

    @ManyToMany(mappedBy = "serviceContracts", cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
    @JsonIgnore
    private final Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "serviceContract", cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE })
    private final Set<License> licenses = new HashSet<>();

    public ServiceContract() {
        this(null);
    }

    public ServiceContract(final Company contractor) {
        this(0, contractor, null, null);
    }

    public ServiceContract(final Company contractor, final Date start, final Date end) {
        this(0, contractor, start, end);
    }

    public ServiceContract(final long id, final Company contractor, final Date start, final Date end) {
        this.id = id;
        this.contractor = contractor;
        this.start = start;
        this.end = end;
    }

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

    public void setContractor(final Company contractor) {
        this.contractor = contractor;
    }

    public void setEnd(final Date end) {
        this.end = end;
    }

    public void setStart(final Date start) {
        this.start = start;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, contractor, start, end);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other)
            return true;

        if (other == null || other.getClass() != this.getClass())
            return false;

        final ServiceContract otherServiceContract = (ServiceContract) other;
        return Objects.equals(this.id, otherServiceContract.id)
                && Objects.equals(this.start, otherServiceContract.start)
                && Objects.equals(this.end, otherServiceContract.end)
                && Objects.equals(this.contractor, otherServiceContract.contractor);
    }
}
