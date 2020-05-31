package de.hse.licensemanager.model;

import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "t_company_department")
public class CompanyDepartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "company", nullable = false)
    private Company company;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_department", insertable = false, updatable = false)
    private List<User> users;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Company getCompany() {
        return company;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setCompany(final Company company) {
        this.company = company;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, company.getId());
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other)
            return true;

        if (other == null || other.getClass() != this.getClass())
            return false;

        final CompanyDepartment otherDepartment = (CompanyDepartment) other;
        return Objects.equals(this.id, otherDepartment.id) && Objects.equals(this.name, otherDepartment.name)
                && Objects.equals(this.company.getId(), otherDepartment.company.getId());
    }
}
