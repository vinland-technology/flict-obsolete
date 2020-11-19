// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.test;

import com.sandklef.compliance.arbiter.LicenseArbiterFactory;
import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.json.JsonComponentParser;
import com.sandklef.compliance.json.JsonLicenseParser;
import com.sandklef.compliance.json.test.TestJsonComponentParser;
import com.sandklef.compliance.json.test.TestLicenseParser;
import com.sandklef.compliance.utils.ComponentArbiter;
import com.sandklef.compliance.utils.LicenseStore;

import java.io.IOException;
import java.util.Map;

public class TestAll {


    public static void main(String[] args) throws IOException, LicenseExpressionException, IllegalLicenseExpression, LicenseCompatibility.LicenseConnectorException {

      //  System.out.println("\n");
      //  Log.level(Log.DEBUG);
        Map<String, License> licenses1 = new JsonLicenseParser().readLicenseDir("share/licenses/json");
        LicenseStore.getInstance().addLicenses(licenses1);

      //  System.out.println(" licenses: " + licenses1);


        TestJsonComponentParser.test();
        System.out.println("\n");
        System.out.println("\n");
        TestCanAUseB.test();
        System.out.println("\n");

     /*   System.out.println(" BEGIN\n");
        LicenseArbiter.report(Utils.validComponent(), null);
        System.out.println(" END\n");
       */
        System.out.println("\n");

        // TODO: move to separate test
        JsonComponentParser jp = new JsonComponentParser();
        Map<String, License> licenses = new JsonLicenseParser().readLicenseDir("share/licenses/json");
        LicenseStore.getInstance().addLicenses(licenses);
        Component component = jp.readComponent("com/sandklef/compliance/json/test/archive.json");
        Report report = ComponentArbiter.report(LicenseArbiterFactory.defaultArbiter(), component, null);

        System.out.println("\n");
        TestComponents.test();
        System.out.println("\n");
        TestPolicy.test();
        System.out.println("\n");
        TestDualLicenses.test();
        System.out.println("\n");
        TestJsonComponentParser.test();
        System.out.println("\n");
        TestLicenseParser.test();
        System.out.println("\n");
  //      VirtualLicenseBuilderTest.test();

        TestLicenseCompatibility.test();

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
