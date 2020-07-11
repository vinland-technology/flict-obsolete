package com.sandklef.compliance.test;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.json.JsonLicenseCompatibilityParser;

import java.io.IOException;
import java.util.Map;

import static com.sandklef.compliance.test.Utils.*;

public class TestLicenseCompatibility {


    private static final String LOG_TAG = TestLicenseCompatibility.class.getSimpleName();

    private static boolean aCanUseB(LicenseCompatibility a, LicenseCompatibility b) throws LicenseCompatibility.LicenseConnectorException {
        if (a.canUse().contains(b)) {
            return true;
        }

        for (LicenseCompatibility l : b.canBeUsedBy()) {
            if (l.license().spdx().equals(a.license().spdx())) {
                return true;
            }
        }

        return false;
    }

    public static void test() throws IOException, LicenseCompatibility.LicenseConnectorException {
        test_simple();
        test_read_json();
    }

    public static void test_simple() throws LicenseCompatibility.LicenseConnectorException {
        printTestStart("TestLicenseConnector");

        LicenseCompatibility bsd3Conn = new LicenseCompatibility(bsd3);
        LicenseCompatibility lgpl21Conn = new LicenseCompatibility(lgpl21);
        LicenseCompatibility gpl20Conn = new LicenseCompatibility(gpl20);
        LicenseCompatibility gpl20_laterConn = new LicenseCompatibility(gpl20_later);
        LicenseCompatibility apache20Conn = new LicenseCompatibility(apache20);
        LicenseCompatibility gpl30Conn = new LicenseCompatibility(gpl30);

        //   bsd3 ---> lgpl21
        lgpl21Conn.addCanUse(bsd3Conn);

        //   lgpl21 ---> gpl20
        gpl20Conn.addCanUse(lgpl21Conn);

        //   gpl20 ---> gpl20_later
        gpl20_laterConn.addCanUse(gpl20Conn);

        //   gpl20_later ---> gpl30
        gpl30Conn.addCanUse(gpl20_laterConn);

        // graphically representation
        // bsd3 ---> lgpl21 ---> gpl20 --> gpl20_later ---> gpl30

        // bsd3 ---> lgpl21
        assertHelper("lgpl21 can use bsd3", aCanUseB(lgpl21Conn, bsd3Conn));
        assertHelper("bsd3 can NOT use lgpl21", (!aCanUseB(bsd3Conn, lgpl21Conn)));

    }


    public static void test_read_json() throws IOException, LicenseCompatibility.LicenseConnectorException {
        JsonLicenseCompatibilityParser jcp = new JsonLicenseCompatibilityParser();
        Map<String, LicenseCompatibility> licenseConnectors = JsonLicenseCompatibilityParser.readLicenseConnection("etc/licenses/connections/dwheeler.json");

        for (Map.Entry<String, LicenseCompatibility> entry : licenseConnectors.entrySet()) {
            String key = entry.getKey();
            LicenseCompatibility value = entry.getValue();
    //        System.out.println(key);
    //            System.out.println(LicenseUtils.listCanBeUsedBy(value));
        }
//        System.out.println(LicenseUtils.listCanBeUsedBy(licenseConnectors.get("PD")));

        assertHelper("lgpl3 can use pd",
                aCanUseB(licenseConnectors.get("LGPL-3.0-or-later"),
                        licenseConnectors.get("BSD-3-Clause")));

        assertHelper("pd can NOT use lgpl3",
                !aCanUseB(licenseConnectors.get("BSD-3-Clause"),
                        licenseConnectors.get("LGPL-3.0-or-later")));

        assertHelper("apache can use bsd3",
                aCanUseB(licenseConnectors.get("Apache-2.0"),
                        licenseConnectors.get("BSD-3-Clause")));

        assertHelper("bsd3 can NOT use apache",
                !aCanUseB(licenseConnectors.get("BSD-3-Clause"),
                        licenseConnectors.get("Apache-2.0")));

        assertHelper("pd can NOT use gpl3",
                !aCanUseB(licenseConnectors.get("BSD-3-Clause"),
                        licenseConnectors.get("GPL-3.0-or-later")));


    }

    public static void main(String[] args) throws IOException, LicenseCompatibility.LicenseConnectorException {
        test();
    }

}
