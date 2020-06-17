package de.hse.licensemanager.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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

    @Column(name = "loginname", nullable = false, unique = true)
    private String loginname;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private byte passwordHash[];

    @Column(name = "verified", nullable = false)
    private boolean verified;

    @Column(name = "active", nullable = false)
    private boolean active;

    @OneToOne
    @JoinColumn(name = "system_group", nullable = false)
    private SystemGroup systemGroup;

    @ManyToOne
    @JoinColumn(name = "company_department", nullable = false)
    private CompanyDepartment companyDepartment;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "t_service_group", joinColumns = @JoinColumn(name = "`user`"), inverseJoinColumns = @JoinColumn(name = "service_contract"))
    private Set<ServiceContract> serviceContracts;

    public long getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getLoginname() {
        return loginname;
    }

    public String getEmail() {
        return email;
    }

    public byte[] getPasswordHash() {
        return passwordHash;
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

    public void setId(final long id) {
        this.id = id;
    }

    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    public void setLoginname(final String loginname) {
        this.loginname = loginname;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public void setPasswordHash(final byte[] passwordHash) {
        this.passwordHash = passwordHash;
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
}
