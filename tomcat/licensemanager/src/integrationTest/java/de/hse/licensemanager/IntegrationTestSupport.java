package de.hse.licensemanager;

public class IntegrationTestSupport {

    public static String getRestURI() {
        return getBaseURI() + "/rest";
    }

    public static String getBaseURI() {
        if (System.getProperty("gretty.httpsBaseURI") != null) {
            return System.getProperty("gretty.httpsBaseURI");
        } else if (System.getProperty("gretty.httpBaseURI") != null) {
            return System.getProperty("gretty.httpBaseURI");
        } else {
            return "http://localhost:8080/licensemanager";
        }
    }
}
