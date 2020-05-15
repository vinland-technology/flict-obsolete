package com.sandklef.compliance.test;

import com.sandklef.compliance.domain.LicenseConnector;
import com.sandklef.compliance.json.test.TestJsonComponentParser;
import com.sandklef.compliance.json.test.TestLicenseParser;

import java.io.IOException;

public class TestAll {

    public static void main(String args[]) throws IOException {
        System.out.println("\n");
        TestComponents.test();
        System.out.println("\n");
        TestCanAUseB.test();
        System.out.println("\n");
        TestPolicy.test();
        System.out.println("\n");
        TestDualLicenses.test();
        System.out.println("\n");
        TestMostPermissiveLicenseComparator.test();
        System.out.println("\n");
        TestJsonComponentParser.test();
        System.out.println("\n");
        TestLicenseParser.test();
        System.out.println("\n");
  //      VirtualLicenseBuilderTest.test();

        TestLicenseConnector.test();

        System.out.println("\nTests finished\n-------------------------");
        System.out.println(" * Test:      " + Utils.counter());
        System.out.println(" * Failures:  " + Utils.erorCounter());
        System.out.println(" * Successes: " + Utils.sucessCounter());
        if (Utils.erorCounter()>0) {
            System.out.println("\n");
            System.out.println(" * Failures:" + Utils.fails());
        }
        System.out.println("\n");
    }
}
