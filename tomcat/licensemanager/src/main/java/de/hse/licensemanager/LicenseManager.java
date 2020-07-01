package de.hse.licensemanager;

import org.glassfish.jersey.server.ResourceConfig;

import de.hse.licensemanager.filter.LoginFilter;

public class LicenseManager extends ResourceConfig {

    public LicenseManager() {
        packages(getClass().getPackageName());
        register(LoginFilter.class);
    }
}
