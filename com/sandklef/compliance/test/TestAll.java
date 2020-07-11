package com.sandklef.compliance.test;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.json.JsonComponentParser;
import com.sandklef.compliance.json.JsonLicenseParser;
import com.sandklef.compliance.json.test.TestJsonComponentParser;
import com.sandklef.compliance.json.test.TestLicenseParser;
import com.sandklef.compliance.utils.LicenseArbiter;
import com.sandklef.compliance.utils.LicenseExpressionParser;
import com.sandklef.compliance.utils.LicenseStore;

import java.io.IOException;
import java.util.Map;

public class TestAll {

  private static void testAndPrint(String expr) throws LicenseExpressionException, IllegalLicenseExpression {
    LicenseExpressionParser lep = new LicenseExpressionParser();
    LicenseExpression le = lep.parse(expr);
//    System.out.println("expr: " + expr + "\n  ==>  " + le + " (" + le.paths() + ")\n");
  }

  
  private static void testAndPrintFixed(String expr) throws LicenseExpressionException  {
 //   Log.level(Log.DEBUG);
    expr = expr.replaceAll("\\s", "");
    LicenseExpressionParser lep = new LicenseExpressionParser();
    String fixed = lep.fixLicenseExpression(expr);
//    System.out.println("expr: " + expr + "\n  ==>  " + fixed);
  }

  
    public static void main(String[] args) throws IOException, LicenseExpressionException, IllegalLicenseExpression, LicenseCompatibility.LicenseConnectorException {

      //  System.out.println("\n");
      //  Log.level(Log.DEBUG);
        Map<String, License> licenses1 = new JsonLicenseParser().readLicenseDir("etc/licenses/json");
        LicenseStore.getInstance().addLicenses(licenses1);

      //  System.out.println(" licenses: " + licenses1);

        try {

          LicenseExpressionParser lep = new LicenseExpressionParser();
          LicenseExpression le;

          

          
          testAndPrint(" MIT ");

          
          testAndPrint(" MIT & BSD-3-Clause");

          testAndPrint(" MIT & BSD-3-Clause & Apache-2.0");

          testAndPrint(" ( MIT ) ");


          testAndPrint(" ( ( MIT ) ) ");


          testAndPrint(" LGPL-2.1-only ");


          testAndPrint(" ( LGPL-2.1-only ) ");


          testAndPrint(" (( LGPL-2.1-only ) )  ");


          testAndPrint(" ( ( ( ( LGPL-2.1-or-later & GPL-2.0-or-later ) ) ) )");

          testAndPrint("  ( ( LGPL-2.1-or-later & GPL-2.0-or-later ) & BSD-3-Clause ) ");

          testAndPrint("  ( ( LGPL-2.1-or-later & GPL-2.0-or-later ) & BSD-3-Clause ) & ( BSD-3-Clause & MIT )");

          testAndPrint("  ( ( LGPL-2.1-or-later | GPL-2.0-or-later ) | BSD-3-Clause )");

          testAndPrint(" LGPL-2.1-or-later | GPL-2.0-or-later & BSD-3-Clause  & MIT  & MIT");
          testAndPrint(" LGPL-2.1-or-later & GPL-2.0-or-later | BSD-3-Clause  ");
          testAndPrint(" LGPL-2.1-or-later & GPL-2.0-or-later | LGPL-2.1-only | BSD-3-Clause & MIT   ");


          testAndPrint(" LGPL-2.1-or-later | GPL-2.0-or-later & BSD-3-Clause  ");



          testAndPrint("(  ( ( LGPL-2.1-or-later & GPL-2.0-or-later ) & BSD-3-Clause ) & ( BSD-3-Clause & MIT ) & (GPL-2.0-or-later & BSD-3-Clause) )");


            System.out.println("\n");
            System.out.println("\n");
            System.out.println("\n");
            testAndPrint("( GPL-2.0-or-later & MIT )  | ( LGPL-2.1-or-later & BSD-3-Clause)");

            testAndPrint("( GPL-2.0-or-later & MIT )  | ( LGPL-2.1-or-later & BSD-3-Clause) | ( GPL-2.0-or-later)");

            testAndPrint("GPL-2.0-or-later | MIT | LGPL-2.1-or-later | BSD-3-Clause | GPL-2.0-or-later");


        } catch (LicenseExpressionException | IllegalLicenseExpression e) {
            e.printStackTrace();
        }

        System.out.println("\n");

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
        Map<String, License> licenses = new JsonLicenseParser().readLicenseDir("etc/licenses/json");
        LicenseStore.getInstance().addLicenses(licenses);
        Component component = jp.readComponent("com/sandklef/compliance/json/test/archive.json");
        Report report = LicenseArbiter.report(component, null);

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
