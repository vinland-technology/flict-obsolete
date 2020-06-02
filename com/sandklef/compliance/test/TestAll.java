package com.sandklef.compliance.test;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.json.JsonComponentParser;
import com.sandklef.compliance.json.JsonLicenseParser;
import com.sandklef.compliance.json.test.TestJsonComponentParser;
import com.sandklef.compliance.json.test.TestLicenseParser;
import com.sandklef.compliance.utils.LicenseArbiter;
import com.sandklef.compliance.utils.LicenseExpressionParser;
import com.sandklef.compliance.utils.LicenseStore;
import com.sandklef.compliance.utils.Log;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TestAll {

    public static void main(String args[]) throws IOException {

        System.out.println("\n");
        Log.level(Log.DEBUG);
        Map<String, License> licenses1 = new JsonLicenseParser().readLicenseDir("licenses/json");
        LicenseStore.getInstance().addLicenses(licenses1);

        System.out.println(" licenses: " + licenses1);

        try {
            LicenseExpressionParser lep = new LicenseExpressionParser();

            LicenseExpression le = lep.parse(" MIT ");
            System.out.println(" expr: " + le);

            le = lep.parse(" MIT & BSD-3-Clause");
            System.out.println(" expr: " + le);

            le = lep.parse(" MIT & BSD-3-Clause & Apache-2.0");
            System.out.println(" expr: " + le);

            le = lep.parse(" ( MIT ) ");
            System.out.println(" expr: " + le);

            le = lep.parse(" ( ( MIT ) ) ");
            System.out.println(" expr: " + le);

            le = lep.parse(" LGPL-2.1-only ");
            System.out.println(" expr: " + le);

            le = lep.parse(" ( LGPL-2.1-only ) ");
            System.out.println(" expr: " + le);

            le = lep.parse(" (( LGPL-2.1-only ) )  ");
            System.out.println(" expr: " + le);

            le = lep.parse(" ( ( ( ( LGPL-2.1-or-later & GPL-2.0-or-later ) ) ) )");
            System.out.println(" expr: " + le);
            System.out.println("\n");

            le = lep.parse("  ( ( LGPL-2.1-or-later & GPL-2.0-or-later ) & BSD-3-Clause ) ");
            System.out.println(" expr: " + le);
            System.out.println("\n");

            le = lep.parse("  ( ( LGPL-2.1-or-later & GPL-2.0-or-later ) & BSD-3-Clause ) & ( BSD-3-Clause & MIT )");
            System.out.println(" expr: " + le);

            le = lep.parse("(  ( ( LGPL-2.1-or-later & GPL-2.0-or-later ) & BSD-3-Clause ) & ( BSD-3-Clause & MIT ) & (GPL-2.0-or-later & BSD-3-Clause) )");
            System.out.println(" expr: " + le);


            System.out.println("\n");
            System.out.println("\n");
            System.out.println("\n");
            le = lep.parse("( GPL-2.0-or-later & MIT )  | ( LGPL-2.1-or-later & BSD-3-Clause)");
            System.out.println(" expr: " + le);
            le = lep.parse("( GPL-2.0-or-later & MIT )  | ( LGPL-2.1-or-later & BSD-3-Clause) | ( GPL-2.0-or-later)");
            System.out.println(" expr: " + le);
            le = lep.parse("GPL-2.0-or-later | MIT | LGPL-2.1-or-later | BSD-3-Clause | GPL-2.0-or-later");
            System.out.println(" expr: " + le);

        } catch (LicenseExpressionException e) {
            e.printStackTrace();
        }

        System.out.println("\n");
        System.exit(1);

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
        System.out.println(" BEGIN\n");
        JsonComponentParser jp = new JsonComponentParser();
        Map<String, License> licenses = new JsonLicenseParser().readLicenseDir("licenses/json");
        LicenseStore.getInstance().addLicenses(licenses);
        List<Component> components = jp.readComponent("com/sandklef/compliance/json/test/archive.json");
        for (Component c : components) {
            System.out.println(" * component: " + c.toStringLong() + "\n");
        }

        Report report = LicenseArbiter.report(components.get(0), null);
        System.out.println(report.component());
        for (Report.ComponentResult cr : report.componentResults()) {
            System.out.println(" * " +
                    cr.compliant() + " " +
                    cr.color() + " " +
                    cr.component() + "\n");
        }

//        LicenseArbiter.report(Utils.bigComponent(), null);
        System.out.println(" END <----\n");
        System.exit(1);

        System.out.println("\n");
        TestComponents.test();
        System.out.println("\n");
        TestConcern.test();
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
