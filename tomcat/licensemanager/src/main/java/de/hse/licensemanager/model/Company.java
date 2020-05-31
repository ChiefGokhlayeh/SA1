package de.hse.licensemanager.model;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

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

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "company", insertable = false, updatable = false)
    private final List<CompanyDepartment> departments;

    public Company() {
        departments = new ArrayList<>();
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

    public void setName(final String name) {
        this.name = name;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public void addDepartment(final CompanyDepartment department) {
        final Company prevCompany = department.getCompany();
        department.setCompany(this);
        try {
            this.departments.add(department);
        } catch (final Exception e) {
            department.setCompany(prevCompany);
            throw e;
        }
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
                && Objects.equals(this.address, otherCompany.address)
                && Objects.equals(this.departments, otherCompany.departments);
    }
}
