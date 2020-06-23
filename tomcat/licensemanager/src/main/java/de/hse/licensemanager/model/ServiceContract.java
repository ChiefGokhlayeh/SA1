package de.hse.licensemanager.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.sql.Date;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "t_service_contract")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@query_id")
public class ServiceContract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @JoinColumn(name = "contractor", nullable = false)
    private Company contractor;

    @Column(name = "start")
    private Date start;

    @Column(name = "end")
    private Date end;

    @ManyToMany(mappedBy = "serviceContracts")
    @JsonIgnore
    private Set<User> users;

    @OneToMany(mappedBy = "serviceContract", cascade = CascadeType.ALL)
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
