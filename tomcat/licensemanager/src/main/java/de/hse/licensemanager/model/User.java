package de.hse.licensemanager.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "t_user")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@query_id")
public class User {
    public enum Group {
        USER, COMPANY_ADMIN, SYSTEM_ADMIN;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "`group`", nullable = false)
    @Enumerated(EnumType.STRING)
    private Group group;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name = "company_department", nullable = false)
    @JsonIgnore
    private CompanyDepartment companyDepartment;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE })
    @JoinTable(name = "t_service_group", joinColumns = @JoinColumn(name = "`user`", nullable = false), inverseJoinColumns = @JoinColumn(name = "service_contract", nullable = false))
    @JsonIgnore
    private final Set<ServiceContract> serviceContracts = new HashSet<>();

    @OneToOne(cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REFRESH, CascadeType.REMOVE })
    @JoinColumn(name = "credentials", nullable = false, unique = true)
    private Credentials credentials;

    public User() {
        this(null, null, null, null, null, null);
    }

    public User(final String firstname, final String lastname, final String email,
            final CompanyDepartment companyDepartment, final Group group, final Credentials credentials) {
        this(0, firstname, lastname, email, companyDepartment, group, credentials);
    }

    public User(final long id, final String firstname, final String lastname, final String email,
            final CompanyDepartment companyDepartment, final Group group, final Credentials credentials) {
        this(id, firstname, lastname, email, companyDepartment, true, group, credentials);
    }

    public User(final long id, final String firstname, final String lastname, final String email,
            final CompanyDepartment companyDepartment, final boolean active, final Group group,
            final Credentials credentials) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.companyDepartment = companyDepartment;
        this.active = active;
        this.group = group;
        this.credentials = credentials;
    }

    public long getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActive() {
        return active;
    }

    public Group getGroup() {
        return group;
    }

    public CompanyDepartment getCompanyDepartment() {
        return companyDepartment;
    }

    @JsonIgnore
    public Company getCompany() {
        return getCompanyDepartment().getCompany();
    }

    public Set<ServiceContract> getServiceContracts() {
        return serviceContracts;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public void setGroup(final Group group) {
        this.group = group;
    }

    public void setCompanyDepartment(final CompanyDepartment companyDepartment) {
        this.companyDepartment = companyDepartment;
    }

    public void setCredentials(final Credentials credentials) {
        this.credentials = credentials;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstname, lastname, email, active, credentials);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other)
            return true;

        if (other == null || other.getClass() != this.getClass())
            return false;

        final User otherUser = (User) other;
        return Objects.equals(this.id, otherUser.id) && Objects.equals(this.firstname, otherUser.firstname)
                && Objects.equals(this.lastname, otherUser.lastname) && Objects.equals(this.email, otherUser.email)
                && Objects.equals(this.active, otherUser.active);
    }
}
