package de.hse.licensemanager.model;

import java.util.Arrays;

public class PlainCredentials {
    private String loginname;
    private String password;

    public PlainCredentials() {
        this(null, null);
    }

    public PlainCredentials(final String loginname, final String password) {
        this.loginname = loginname;
        this.password = password;
    }

    public String getLoginname() {
        return loginname;
    }

    public String getPassword() {
        return password;
    }

    public void setLoginname(final String loginname) {
        this.loginname = loginname;
    }

    public void setPassword(final String password) {
        this.password = password;
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
