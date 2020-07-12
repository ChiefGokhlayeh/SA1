package de.hse.licensemanager.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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

    @Column(name = "verified", nullable = false)
    private boolean verified;

    @Column(name = "active", nullable = false)
    private boolean active;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name = "system_group", nullable = false)
    private SystemGroup systemGroup;

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
            final CompanyDepartment companyDepartment, final SystemGroup systemGroup, final Credentials credentials) {
        this(0, firstname, lastname, email, companyDepartment, systemGroup, credentials);
    }

    public User(final long id, final String firstname, final String lastname, final String email,
            final CompanyDepartment companyDepartment, final SystemGroup systemGroup, final Credentials credentials) {
        this(id, firstname, lastname, email, companyDepartment, true, false, systemGroup, credentials);
    }

    public User(final long id, final String firstname, final String lastname, final String email,
            final CompanyDepartment companyDepartment, final boolean active, boolean verified,
            final SystemGroup systemGroup, final Credentials credentials) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.companyDepartment = companyDepartment;
        this.active = active;
        this.verified = verified;
        this.systemGroup = systemGroup;
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

    public boolean isVerified() {
        return verified;
    }

    public boolean isActive() {
        return active;
    }

    public SystemGroup getSystemGroup() {
        return systemGroup;
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

    public void setVerified(final boolean verified) {
        this.verified = verified;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public void setSystemGroup(final SystemGroup systemGroup) {
        this.systemGroup = systemGroup;
    }

    public void setCompanyDepartment(final CompanyDepartment companyDepartment) {
        this.companyDepartment = companyDepartment;
    }

    public void setCredentials(final Credentials credentials) {
        this.credentials = credentials;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstname, lastname, email, verified, active, credentials);
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
                && Objects.equals(this.verified, otherUser.verified) && Objects.equals(this.active, otherUser.active);
    }
}
