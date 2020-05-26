package de.hse.licensemanager;

import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
@Table(name = "t_company_department")
public class CompanyDepartment {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "company", nullable = false)
    private Company company;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_department", insertable = false, updatable = false)
    private Set<User> users;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Company getCompany() {
        return company;
    }

    public Set<User> getUsers() {
        return users;
    }
}
