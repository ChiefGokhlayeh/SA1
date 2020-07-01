package de.hse.licensemanager;

public class IntegrationTestSupport {

    public static String getRestURI() {
        return getBaseURI() + "/rest";
    }

    public static String getBaseURI() {
        if (System.getProperty("gretty.httpsBaseURI") != null) {
            return System.getProperty("gretty.httpsBaseURI");
        } else {
            return System.getProperty("gretty.httpBaseURI");
        }
    }
}
