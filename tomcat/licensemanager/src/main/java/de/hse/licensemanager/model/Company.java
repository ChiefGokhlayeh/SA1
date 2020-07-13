package de.hse.licensemanager.model;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;

@Entity
@Table(name = "t_company")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "address")
    private String address;

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE })
    @JoinColumn(name = "company", insertable = false, updatable = false)
    @JsonIgnore
    private final List<CompanyDepartment> departments = new ArrayList<>();

    @OneToMany(mappedBy = "contractor", cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE })
    @JsonIgnore
    private final List<ServiceContract> serviceContracts = new ArrayList<>();

    public Company() {
        this(null, null);
    }

    public Company(final String name, final String address) {
        this(0, name, address);
    }

    public Company(final long id, final String name, final String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public List<CompanyDepartment> getDepartments() {
        return departments;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, address);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other)
            return true;

        if (other == null || other.getClass() != this.getClass())
            return false;

        final Company otherCompany = (Company) other;
        return Objects.equals(this.id, otherCompany.id) && Objects.equals(this.name, otherCompany.name)
                && Objects.equals(this.address, otherCompany.address);
    }

    @Override
    public String toString() {
        return String.format("{ id: %s, name: %s, address: %s }", id, name, address);
    }
}
