package de.hse.licensemanager.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "t_credentials")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@query_id")
public class Credentials {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "loginname", nullable = false, unique = true)
    private String loginname;

    @Column(name = "password_hash", nullable = false)
    private byte passwordHash[];

    @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "t_user", joinColumns = @JoinColumn(name = "credentials"), inverseJoinColumns = @JoinColumn(name = "credentials"))
    private User user;

    public long getId() {
        return id;
    }

    public String getLoginname() {
        return loginname;
    }

    public User getUser() {
        return user;
    }

    public byte[] getPasswordHash() {
        return passwordHash;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public void setLoginname(final String loginname) {
        this.loginname = loginname;
    }

    public void setPasswordHash(final byte[] passwordHash) {
        this.passwordHash = passwordHash;
    }
}
