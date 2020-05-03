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
import java.security.Policy;
import java.util.ArrayList;
import java.util.List;

import static com.sandklef.compliance.test.Utils.*;

public class TestPolicy {

  private static final String LOG_TAG = TestPolicy.class.getSimpleName();

  private static boolean testCanAUseB(License user, License usee) {
    boolean ret = LicenseArbiter.canAUseB(null, null, user,usee);
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
    Log.d(LOG_TAG, " top " + validReport.concern().component().name());
    Log.d(LOG_TAG, " concerns " + validReport.concern().licenseConcerns().size());
    Log.d(LOG_TAG, " concerns " + validReport.concern().licenseConcerns());
    Log.d(LOG_TAG, " conclusions " + validReport.conclusion().licenseConclusions().size());
    Log.d(LOG_TAG, " conclusions " + validReport.conclusion().licenseConclusions());
    Log.d(LOG_TAG, " violations " + validReport.violation().obligations().size());
    Log.d(LOG_TAG, " violations " + validReport.violation().obligations());

    //Log.level(Log.ERROR);

    assertHelper("4 conclusions made", validReport.conclusion().licenseConclusions().size()==3);
    assertHelper("0 violations made", validReport.violation.obligations().size()==0);
    assertHelper("0 concerns made", validReport.concern().licenseConcerns().size()==0);
    assertHelper("Both deps are lgpl",
            c.dependencies().get(0).concludedLicense().spdxTag().equals("LGPL-2.1-only") &&
                    c.dependencies().get(1).concludedLicense().spdxTag().equals("LGPL-2.1-only"));

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

    Log.d(LOG_TAG, "  result: " +
            " " + validReport.concern().licenseConcerns().size() +
            " " + validReport.conclusion().licenseConclusions().size() +
            " " + validReport.violation().obligations().size());
    Log.d(LOG_TAG, "  result:  \nconclusions: " + validReport.conclusion().licenseConclusions() +
            "\n concerns: " + validReport.concern().licenseConcerns() +
            "\nviolations: " + validReport.violation().obligations());

    assertHelper("concerns: ", validReport.concern().licenseConcerns().size() == 1);
    assertHelper("conclusions", validReport.conclusion().licenseConclusions().size() == 0);
    assertHelper("violations: ", validReport.violation().obligations().size() == 3);

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
  //  Log.level(Log.DEBUG);
    validReport = LicenseArbiter.report(validComponent(), policy);
    Log.d(LOG_TAG, "  result sizes: " + validReport.conclusion().licenseConclusions().size() +
            " " + validReport.concern().licenseConcerns().size() +
            " " + validReport.violation().obligations().size() + " violations");
    Log.d(LOG_TAG, "  result:  \nconclusions: " + validReport.conclusion().licenseConclusions() +
            "\n concerns: " + validReport.concern().licenseConcerns() +
            "\nviolations: " + validReport.violation().obligations());

    assertHelper(" concern: ", validReport.concern().licenseConcerns().size() == 1);
    assertHelper("conclusion", validReport.conclusion().licenseConclusions().size() == 0);
    assertHelper("violations: ", validReport.violation().obligations().size() == 5);


//    System.out.println("violations: " + validReport.violation().obligations());
  }

    //Report invalidReport = LicenseArbiter.report(invalidComponent(), policy);

  public static void test3() {

    printSubTestStart("Valid but concerns and conclusions");

    LicensePolicy policy = new LicensePolicy();
    policy.addGrayLicense(gpl2);
    policy.addBlackLicense(gpl3);

    Component a11 = new Component("a11", lgpl2, null);
    Component a12 = new Component("a12", apache2, null);
    ArrayList<Component> a1Deps = new ArrayList<>();
    a1Deps.add(a11);
    a1Deps.add(a12);
    Component a1 = new Component("a1", gpl2, a1Deps);

    Component a21 = new Component("a21", apache2, null);
    List<License> a22Licenses = new ArrayList<>();
    a22Licenses.add(gpl2);     // no concern since not chosen
    a22Licenses.add(apache2);  // conclusion
    Component a22 = new Component("a22", a22Licenses, null);
    ArrayList<Component> a2Deps = new ArrayList<>();
    a2Deps.add(a21);
    a2Deps.add(a22);
    Component a2 = new Component("a1", gpl2, a2Deps); // concern since gray

    ArrayList<Component> aDeps = new ArrayList<>();
    aDeps.add(a1);
    aDeps.add(a2);
    Component a = new Component("A", gpl2, aDeps); // concern since gray

    // 2 concerns
    // 0 violations
    // 1 conclusion
    Log.level(Log.DEBUG);
    Report report = LicenseArbiter.report(a, policy);

    Log.level(Log.DEBUG);
    Log.d(LOG_TAG, "  result sizes: conclusions: " + report.conclusion().licenseConclusions().size() +
            " concerns: " + report.concern().licenseConcerns().size() +
            " violations: " + report.violation().obligations().size() );
    Log.d(LOG_TAG, "  result:  \nconclusions: " + report.conclusion().licenseConclusions() +
            "\n concerns: " + report.concern().licenseConcerns() +
            "\nviolations: " + report.violation().obligations());

    assertHelper(" concern: ", report.concern().licenseConcerns().size() == 3);
    assertHelper(" conclusion: ", report.conclusion().licenseConclusions().size() == 1);
    assertHelper(" concern: ", report.violation().obligations().size() == 0);


  }

  public static void test4() {

    printSubTestStart("Valid but concerns, conclusions and violation");

    LicensePolicy policy = new LicensePolicy();
    policy.addGrayLicense(lgpl2);
    policy.addBlackLicense(gpl2);

    // a1
    Component a11 = new Component("a11", gpl2, null); // violations since black
    Component a12 = new Component("a12", apache2, null);
    ArrayList<Component> a1Deps = new ArrayList<>();
    a1Deps.add(a11);
    a1Deps.add(a12);
    Component a1 = new Component("a1", apache2, a1Deps); // violations since gpl concluded

    Component a21 = new Component("a21", apache2, null);
    List<License> a22Licenses = new ArrayList<>();
    a22Licenses.add(gpl2);     // no concern since not chosen
    a22Licenses.add(apache2);  // conclusion
    Component a22 = new Component("a22", a22Licenses, null);
    ArrayList<Component> a2Deps = new ArrayList<>();
    a2Deps.add(a21);
    a2Deps.add(a22);
    Component a2 = new Component("a1", gpl2, a2Deps);

    ArrayList<Component> aDeps = new ArrayList<>();
    aDeps.add(a1);
    aDeps.add(a2);
    Component a = new Component("A", gpl2, aDeps); // violation since black


    // 0 concerns
    // 3 violations
    // 1 conclusion
    Report report = LicenseArbiter.report(a, policy);
//    Log.level(Log.DEBUG);
//    Log.level(Log.DEBUG);
    Log.d(LOG_TAG, "  result sizes: conclusions: " + report.conclusion().licenseConclusions().size() +
            " concerns: " + report.concern().licenseConcerns().size() +
            " violations: " + report.violation().obligations().size() );
    Log.d(LOG_TAG, "  result:  \nconclusions: " + report.conclusion().licenseConclusions() +
            "\n concerns: " + report.concern().licenseConcerns() +
            "\nviolations: " + report.violation().obligations());

    assertHelper(" concern: ", report.concern().licenseConcerns().size() == 0);
    assertHelper(" conclusion: ", report.conclusion().licenseConclusions().size() == 1);
    assertHelper(" obligations: ", report.violation().obligations().size() == 3);

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
