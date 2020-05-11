package com.sandklef.compliance.test;


import com.sandklef.compliance.domain.*;

import static com.sandklef.compliance.test.Utils.*;

public class TestLicenseConnector {


    private static boolean aCanUseB(LicenseConnector a, LicenseConnector b) {
/*        System.out.println(" a : " + a.license().spdxTag() + " " + a.canBeUsedBy() + " " + a.canUse());
        System.out.println(" b : " + b.license().spdxTag() + " " + b.canBeUsedBy() + " " + b.canUse());
        System.out.println(" a : " + a.license().spdxTag() + " " + a.canUse() + " contains " + b.license());
  */
        return a.canUse().contains(b.license());
    }

    public static void test() {
        printTestStart("TestLicenseConnector");

        LicenseConnector bsd3Conn = new LicenseConnector(bsd3);
        LicenseConnector lgpl21Conn = new LicenseConnector(lgpl21);
        LicenseConnector gpl20Conn = new LicenseConnector(gpl20);
        LicenseConnector gpl20_laterConn = new LicenseConnector(gpl20_later);
        LicenseConnector apache20Conn = new LicenseConnector(apache20);

        //   bsd3 ---> lgpl21
        lgpl21Conn.addCanUse(bsd3Conn);

        //   lgpl21 ---> gpl20
        gpl20Conn.addCanUse(lgpl21Conn);

        //   gpl20 ---> gpl20_later
        gpl20Conn.addCanUse(gpl20_laterConn);

        assertHelper("lgpl21 can use bsd3", aCanUseB(lgpl21Conn, bsd3Conn));
        assertHelper("bsd3 can use lgpl21", !aCanUseB(bsd3Conn, lgpl21Conn));

    }

    public static void main(String[] args) {
        test();
    }

}
