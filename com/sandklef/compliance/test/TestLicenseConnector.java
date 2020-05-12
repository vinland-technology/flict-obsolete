package com.sandklef.compliance.test;


import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.utils.LicenseUtils;

import static com.sandklef.compliance.test.Utils.*;

public class TestLicenseConnector {


    private static boolean aCanUseB(LicenseConnector a, LicenseConnector b) {
/*        System.out.println(" a0 : " + a.license() );
        System.out.println(" a1 : " + a.license().spdxTag() );
        System.out.println(" a2 : " + a.license().spdxTag() + " " + a.canBeUsedBy() );
  */
        System.out.println("--> aCanUseB : " + a.license().spdxTag() + " " + b.license().spdxTag());

        System.out.println(" a : " + a.license().spdxTag() + " " + a.canBeUsedBy() + " " + a.canUse());
        System.out.println(" b : " + b.license().spdxTag() + " " + b.canBeUsedBy() + " " + b.canUse());

        System.out.println(" Try 1 ---> : " + a.license().spdxTag() + " " + a.canUse() + " contains " + b.license());
        if (a.canUse().contains(b)) {
            return true;
        }
        System.out.println(" Try 1 <--- : " + a.license().spdxTag() + " " + a.canUse() + " contains " + b.license());

        // Loop through all b's canBeUsed licenses
        for (LicenseConnector l : b.canBeUsedBy()) {
            System.out.println(" Try 2 ---> : " + a.license().spdxTag() + "   can use: " + l.license().spdxTag());
            if (l.license().spdxTag().equals(a.license().spdxTag())) {
                System.out.println("  FOUND: " + a.license().spdxTag() + " can use: " + l.license().spdxTag());
                System.out.println(" Try 2 <--- : " + a.license().spdxTag() + "   can use: " + l.license().spdxTag());
                return true;
            }
            System.out.println(" Try 2 <--- : " + a.license().spdxTag() + "   can use: " + l.license().spdxTag());

            System.out.println(" Try 3 ---> : " + a.license().spdxTag() + "   CHECK: " + l.license().spdxTag());
            if (aCanUseB(a, l)) {
                System.out.println(" Try 3 <--- : " + a.license().spdxTag() + "   CHECK: " + l.license().spdxTag());
                return true;
            }
            System.out.println(" Try 3 <--- : " + a.license().spdxTag() + "   CHECK: " + l.license().spdxTag());
        }

        // non recursive:
//        return a.canUse().contains(b) ;
        //
        System.out.println("<-- aCanUseB : " + a.license().spdxTag() + " CAN NOT " + b.license().spdxTag());
        return false;
    }

    public static void test() {
        printTestStart("TestLicenseConnector");

        LicenseConnector bsd3Conn = new LicenseConnector(bsd3);
        LicenseConnector lgpl21Conn = new LicenseConnector(lgpl21);
        LicenseConnector gpl20Conn = new LicenseConnector(gpl20);
        LicenseConnector gpl20_laterConn = new LicenseConnector(gpl20_later);
        LicenseConnector apache20Conn = new LicenseConnector(apache20);
        LicenseConnector gpl30Conn = new LicenseConnector(gpl30);

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

        // bsd3 ---> lgpl21 ---> gpl20
        assertHelper("gpl20 can use bsd3", aCanUseB(gpl20Conn, bsd3Conn));
        assertHelper("bsd3 can NOT use gpl20", !aCanUseB(bsd3Conn, gpl20Conn));

        // bsd3 ---> lgpl21 ---> gpl20 --> gpl20_later
        assertHelper("gpl20_later can use bsd3", aCanUseB(gpl20_laterConn, bsd3Conn));
        assertHelper("bsd3 can NOT use gpl20_later", !aCanUseB(bsd3Conn, gpl20_laterConn));

        // bsd3 ---> lgpl21 ---> gpl20 --> gpl20_later ---> gpl30
        assertHelper("gpl30 can use bsd3", aCanUseB(gpl30Conn, bsd3Conn));
        assertHelper("bsd3 can NOT use gpl30", !aCanUseB(bsd3Conn, gpl30Conn));

        System.out.println(LicenseUtils.listCanUse(gpl30Conn));
        System.out.println(LicenseUtils.listCanBeUsedBy(bsd3Conn));

    }

    public static void main(String[] args) {
        test();
    }

}
