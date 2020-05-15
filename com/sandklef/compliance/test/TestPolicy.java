// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.test;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.json.JsonComponentParser;
import com.sandklef.compliance.json.JsonPolicyParser;
import com.sandklef.compliance.utils.LicenseArbiter;
import com.sandklef.compliance.utils.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.sandklef.compliance.test.Utils.*;

public class TestPolicy {

  private static final String LOG_TAG = TestPolicy.class.getSimpleName();

  private static boolean testCanAUseB(License user, License usee) {
    boolean ret = LicenseArbiter.aCanUseB(user,usee);
    return ret;
  }

  public static void test() throws IOException {
    useAsserts=true;
    printTestStart("TestPolicy");
    test1();
    test2();
    test3();
    test4();
  }

  public static void test2() throws IOException {
    JsonPolicyParser pp = new JsonPolicyParser();
    LicensePolicy policy = pp.readLicensePolicy("com/sandklef/compliance/json/test/blacklist-apache.json");

    JsonComponentParser jp = new JsonComponentParser();
    Component c = jp.readComponent("com/sandklef/compliance/json/test/simple-dual.json");

    printSubTestStart("Valid component and apache blacklisted (using JSON)");
//    Log.level(Log.DEBUG);
  //  Log.filterTag(LicenseArbiter.class.getSimpleName());
    Report validReport = LicenseArbiter.report(c, policy);

//    Log.level(Log.DEBUG);
  //  Log.filterTag(null);
    Log.d(LOG_TAG, " component: " + c.toStringLong());
    Log.d(LOG_TAG, " top " + validReport.component().name());
    Log.d(LOG_TAG, " concerns " + validReport.concerns().size());
    Log.d(LOG_TAG, " concerns " + validReport.concerns());
    Log.d(LOG_TAG, " conclusions " + validReport.conclusions().size());
    Log.d(LOG_TAG, " conclusions " + validReport.conclusions());
    Log.d(LOG_TAG, " violations " + validReport.violations().size());
    Log.d(LOG_TAG, " violations " + validReport.violations());

    //Log.level(Log.ERROR);
    assertHelper("4 conclusions made", validReport.conclusions().size()==3);
    assertHelper("0 violations made", validReport.violations().size()==0);
    assertHelper("0 concerns made", validReport.concerns().size()==0);
    assertHelper("Both deps are lgpl",
            c.dependencies().get(0).concludedLicense().spdx().equals("LGPL-2.1-only") &&
                    c.dependencies().get(1).concludedLicense().spdx().equals("LGPL-2.1-only"));

//    System.exit(0);
  }

  public static void test1() {
    /*
        policy.addWhiteLicense(apache2);
        policy.addGrayLicense(lgpl2);
        policy.addBlackLicense(gpl2);
     */
    LicensePolicy policy = Utils.permissiveAndWeakPolicy();

    printSubTestStart("Valid component and Permissive/weak policy");
    /*
             top (gpl2)
              |
           +--+--------------------+
           |                       |
           a (apache2)             b (gpl2)
           |                       |
        +--+---------+             +------+--------+
        |            |             |               |
      a1 (apache2)  a2 (lgpl2)     b1 (apache2)   b2 (gpl2)
                                   |
                         +---------+-----+
                         |               |
                         b11 (apache2)   b12 (apache2)
     */
//    Log.level(Log.DEBUG);
//    Log.filterTag(LicenseArbiter.LOG_TAG);
    Report validReport = LicenseArbiter.report(validComponent(), policy);

    //    Log.level(Log.DEBUG);
    Log.d(LOG_TAG, "  result: " +
            " " + validReport.concerns().size() +
            " " + validReport.conclusions().size() +
            " " + validReport.violations().size());
    Log.d(LOG_TAG, "  result:  \nconclusions: " + validReport.conclusions() +
            "\n concerns: " + validReport.concerns() +
            "\nviolations: " + validReport.violations());

    assertHelper("concerns: ", validReport.concerns().size() == 1);
    assertHelper("conclusions", validReport.conclusions().size() == 0);
    assertHelper("violations: ", validReport.violations().size() == 4);

    printSubTestStart("Valid component and Copyleft/weak policy");
/*
                   top (gpl2)
              |
           +--+--------------------+
           |                       |
           a (apache2)             b (gpl2)
           |                       |
        +--+---------+             +------+--------+
        |            |             |               |
      a1 (apache2)  a2 (lgpl2)     b1 (apache2)   b2 (gpl2)
                                   |
                         +---------+-----+
                         |               |
                         b11 (apache2)   b12 (apache2)

        policy.addWhiteLicense(gpl2);
        policy.addGrayLicense(lgpl2);
        policy.addBlackLicense(apache2);
*/
    policy = Utils.copyleftAndWeakPolicy();
  // Log.level(Log.DEBUG);
    validReport = LicenseArbiter.report(validComponent(), policy);
    Log.d(LOG_TAG, "  result sizes: " + validReport.conclusions().size() +
            " " + validReport.concerns().size() +
            " " + validReport.violations().size() + " violations");
    Log.d(LOG_TAG, "  result:  \nconclusions: " + validReport.conclusions() +
            "\n concerns: " + validReport.concerns() +
            "\nviolations: " + validReport.violations());

    assertHelper(" concern: ", validReport.concerns().size() == 1);
    assertHelper("conclusion", validReport.conclusions().size() == 0);
    assertHelper("violations: ", validReport.violations().size() == 7);


//    System.out.println("violations: " + validReport.violation().obligations());
  }

    //Report invalidReport = LicenseArbiter.report(invalidComponent(), policy);

  public static void test3() {

    printSubTestStart("Valid but concerns and conclusions");

    LicensePolicy policy = new LicensePolicy();
    policy.addGrayLicense(gpl20);
    policy.addBlackLicense(gpl30);

    Component a11 = new Component("a11", Utils.lgpl21, null);
    Component a12 = new Component("a12", apache20, null);
    ArrayList<Component> a1Deps = new ArrayList<>();
    a1Deps.add(a11);
    a1Deps.add(a12);
    Component a1 = new Component("a1", gpl20, a1Deps); // concern since gray

    Component a21 = new Component("a21", apache20, null);
    List<License> a22Licenses = new ArrayList<>();
    a22Licenses.add(gpl20);     // no concern since not chosen
    a22Licenses.add(apache20);  // conclusion
    Component a22 = new Component("a22", a22Licenses, null);
    ArrayList<Component> a2Deps = new ArrayList<>();
    a2Deps.add(a21);
    a2Deps.add(a22);
    Component a2 = new Component("a1", gpl20, a2Deps); // concern since gray

    ArrayList<Component> aDeps = new ArrayList<>();
    aDeps.add(a1);
    aDeps.add(a2);
    Component a = new Component("A", gpl20, aDeps); // concern since gray

    // 3 concerns
    // 0 violations
    // 1 conclusion
 //   Log.level(Log.DEBUG);
    Report report = LicenseArbiter.report(a, policy);

    //    Log.level(Log.DEBUG);
    Log.d(LOG_TAG, "  result sizes: conclusions: " + report.conclusions().size() +
            " concerns: " + report.concerns().size() +
            " violations: " + report.violations().size() );
    Log.d(LOG_TAG, "  result:  \nconclusions: " + report.conclusions() +
            "\n concerns: " + report.concerns() +
            "\nviolations: " + report.violations());

    assertHelper(" concern: ", report.concerns().size() == 0);
    assertHelper(" conclusion: ", report.conclusions().size() == 1);
    assertHelper(" concern: ", report.violations().size() == 3);


  }

  public static void test4() {

    printSubTestStart("Valid but concerns, conclusions and violation");

    LicensePolicy policy = new LicensePolicy();
    policy.addGrayLicense(Utils.lgpl21);
    policy.addBlackLicense(gpl20);

    // a1
    Component a11 = new Component("a11", gpl20, null); // violations since black +1
    Component a12 = new Component("a12", apache20, null);
    ArrayList<Component> a1Deps = new ArrayList<>();
    a1Deps.add(a11);
    a1Deps.add(a12);
    Component a1 = new Component("a1", apache20, a1Deps); // violations since gpl not used +1

    Component a21 = new Component("a21", apache20, null);
    List<License> a22Licenses = new ArrayList<>();
    a22Licenses.add(gpl20);     // no concern since not chosen
    a22Licenses.add(apache20);  // conclusion
    Component a22 = new Component("a22", a22Licenses, null);
    ArrayList<Component> a2Deps = new ArrayList<>();
    a2Deps.add(a21);
    a2Deps.add(a22);
    Component a2 = new Component("a2", gpl20, a2Deps); // violation since black +1

    ArrayList<Component> aDeps = new ArrayList<>();
    aDeps.add(a1);
    aDeps.add(a2);
    Component a = new Component("A", gpl20, aDeps); // violation since black +1


    // 0 concerns
    // 3 violations
    // 1 conclusion
    Report report = LicenseArbiter.report(a, policy);

/*    Log.level(Log.DEBUG);

    Log.d(LOG_TAG, "  result sizes: conclusions: " + report.conclusions().size() +
            " concerns: " + report.concerns().size() +
            " violations: " + report.violations().size() );
    Log.d(LOG_TAG, "  result:  \nconclusions: " + report.conclusions() +
            "\n concerns: " + report.concerns() +
            "\nviolations: " + report.violations());
*/
    assertHelper(" concern: ", report.concerns().size() == 0);
    assertHelper(" conclusion: ", report.conclusions().size() == 1);
    assertHelper(" obligations: ", report.violations().size() == 4);

    // a should be in the violation list
    assertHelper(" a in the list of violations", checkViolation(report, a));
    // a21 should have a license concluded
    assertHelper(" a22 in the list of conclusions", checkConclusion(report, a22));
    // a11 and a1 should be in the violation test
    assertHelper(" a11 in the list of violations", checkViolation(report, a11));
    assertHelper(" a1 in the list of violations", checkViolation(report, a1));
  }


  public static void main(String[] args) throws IOException {
    test();
  }
}
