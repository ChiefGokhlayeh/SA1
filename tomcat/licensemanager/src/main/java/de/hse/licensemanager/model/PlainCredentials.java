package de.hse.licensemanager.model;

import java.util.Arrays;

public class PlainCredentials {
    private String loginname;
    private String password;
    private String newPassword;

    public PlainCredentials() {
        this(null, null);
    }

    public PlainCredentials(final String loginname, final String password) {
        this(loginname, password, null);
    }

    public PlainCredentials(final String loginname, final String password, final String newPassword) {
        this.loginname = loginname;
        this.password = password;
        this.newPassword = newPassword;
    }

    public String getLoginname() {
        return loginname;
    }

    public String getPassword() {
        return password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setLoginname(final String loginname) {
        this.loginname = loginname;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public void setNewPassword(final String newPassword) {
        this.newPassword = newPassword;
    }

    public boolean verify(final Credentials credentials) {
        if (credentials.getLoginname().equals(loginname)) {
            byte[] hash = credentials.getPasswordHash();
            byte[] testHash = Credentials.generateSecret(password, credentials.getPasswordSalt(),
                    credentials.getPasswordIterations());

            return Arrays.equals(hash, testHash);
        }

        return false;
    }
}
