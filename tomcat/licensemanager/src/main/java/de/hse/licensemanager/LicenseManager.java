package de.hse.licensemanager;

import org.glassfish.jersey.server.ResourceConfig;

import de.hse.licensemanager.filter.AdminOnlyFilter;
import de.hse.licensemanager.filter.LoginFilter;

public class LicenseManager extends ResourceConfig {

    public LicenseManager() {
        packages(getClass().getPackageName());
        register(AdminOnlyFilter.class);
        register(LoginFilter.class);
    }
}
