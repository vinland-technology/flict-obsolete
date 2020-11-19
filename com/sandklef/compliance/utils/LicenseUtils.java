package com.sandklef.compliance.utils;

import com.sandklef.compliance.domain.*;

import java.io.PrintStream;

public class LicenseUtils {


    private static final String LOG_TAG = LicenseUtils.class.getSimpleName();

    public static void connectionsPrintDot(PrintStream writer) throws LicenseCompatibility.LicenseConnectorException {
        writer.println("digraph depends {\nnode [shape=plaintext]");
        for ( LicenseCompatibility lc : LicenseStore.getInstance().connectors().values()) {
            for (LicenseCompatibility l : lc.canBeUsedBy()) {
                String from = lc.hasLicense()?lc.license().spdx():lc.licenseGroup().name();
                String to = l.hasLicense()?l.license().spdx():l.licenseGroup().name();

                writer.println("\"" + from +  "\" ->  \"" + to + "\"");

            }
        }
        writer.println("}");
    }



    public static void verifyLicenses() throws LicenseCompatibility.LicenseConnectorException {
        Log.d(LOG_TAG, "Verify Licenses and connectors");
        // Verify all licenses either is part of a connector or
        // is part of a group that is part of a connector
        Log.d(LOG_TAG, " * Licenses are part of a connector:");
        for (License license : LicenseStore.getInstance().licenses().values() ) {
            Log.dn(LOG_TAG, "   * " + license.spdx() + ": ");
            String key = null;
            if (license.licenseGroup()!=null) {
                key = license.licenseGroup();
            } else {
                key = license.spdx();
            }
            LicenseCompatibility c = LicenseStore.getInstance().connectors().get(key);
            if (c != null) {
                Log.d(null, " OK");
            } else {
                throw new LicenseCompatibility.LicenseConnectorException("Missing connector for: " + key);
            }
        }
    }

    public static ListType licenseType(String licenseSPDX, LicensePolicy policy) throws LicenseExpressionException {
        if (policy != null) {
            License license = LicenseStore.getInstance().license(licenseSPDX);
            if (policy.allowedList().contains(license)) {
                return ListType.ALLOWED_LIST;
            } else if (policy.avoidList().contains(license)) {
                return ListType.AVOID_LIST;
            } else if (policy.deniedList().contains(license)) {
                return ListType.DENIED_LIST;
            }
        }
        // default to allowed
        return ListType.ALLOWED_LIST;
    }



}
