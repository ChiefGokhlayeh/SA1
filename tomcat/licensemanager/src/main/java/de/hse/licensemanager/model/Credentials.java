package de.hse.licensemanager.model;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "t_credentials")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@query_id")
public class Credentials {
    public static final int HASH_LENGTH = 64;
    public static final int SALT_LENGTH = HASH_LENGTH;
    public static final int ITERATIONS = 5000;
    public static final String ALGORITHM = "PBKDF2WithHmacSHA512";

    private static final SecureRandom rng = new SecureRandom();

    public static byte[] generateSecret(final String password, final byte[] salt, final int iterations) {
        final KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, Credentials.HASH_LENGTH * 8);
        try {
            final SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            return factory.generateSecret(spec).getEncoded();
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unknown secret key factory algorithm: " + ALGORITHM, e);
        } catch (final InvalidKeySpecException e) {
            throw new IllegalStateException(
                    "Invalid key spec with " + ITERATIONS + " iterations and " + HASH_LENGTH + " byte length", e);
        }
    }

    public static byte[] generateSalt() {
        final byte[] salt = new byte[SALT_LENGTH];
        rng.nextBytes(salt);
        return salt;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "loginname", nullable = false, unique = true)
    private String loginname;

    @Column(name = "password_hash", nullable = false)
    @JsonIgnore
    private byte passwordHash[];

    @Column(name = "password_salt", nullable = false)
    @JsonIgnore
    private byte[] passwordSalt;

    @Column(name = "password_iterations", nullable = false)
    @JsonIgnore
    private int passwordIterations;

    @OneToOne(cascade = { CascadeType.ALL })
    @JoinTable(name = "t_user", joinColumns = @JoinColumn(name = "credentials"), inverseJoinColumns = @JoinColumn(name = "credentials"))
    @JsonIgnore
    private User user;

    public Credentials() {
    }

    public Credentials(final String loginname, final String passwordPlaintext) {
        this(0, loginname, null, null, 0);
        generateNewHash(passwordPlaintext);
    }

    public Credentials(final String loginname, final byte[] passwordHash, final byte[] passwordSalt,
            final int passwordIterations) {
        this(0, loginname, passwordHash, passwordSalt, passwordIterations);
    }

    public Credentials(final long id, final String loginname, final byte[] passwordHash, final byte[] passwordSalt,
            final int passwordIterations) {
        this.id = id;
        this.loginname = loginname;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
        this.passwordIterations = passwordIterations;
    }

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

    public byte[] getPasswordSalt() {
        return passwordSalt;
    }

    public int getPasswordIterations() {
        return passwordIterations;
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

    public void setPasswordSalt(final byte[] passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public void setPasswordIterations(final int passwordIterations) {
        this.passwordIterations = passwordIterations;
    }

    @Transient
    public void generateNewHash(final String passwordPlaintext) {
        final byte[] salt = generateSalt();
        final int iterations = Credentials.ITERATIONS;
        passwordHash = Credentials.generateSecret(passwordPlaintext, salt, iterations);
        passwordSalt = salt;
        passwordIterations = iterations;
    }
}
