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

  public static void test() throws IOException, LicenseExpressionException, IllegalLicenseExpression {
    useAsserts=true;
    printTestStart("TestPolicy");
    test1();
    test2();
    test3();
    test4();
  }

  public static void test2() throws IOException, LicenseExpressionException, IllegalLicenseExpression {
    JsonPolicyParser pp = new JsonPolicyParser();
    LicensePolicy policy = pp.readLicensePolicy("com/sandklef/compliance/json/test/blacklist-apache.json");

    JsonComponentParser jp = new JsonComponentParser();
    Component c = jp.readComponent("com/sandklef/compliance/json/test/simple-dual.json");

    printSubTestStart("Valid component and apache blacklisted (using JSON)");
//    Log.level(Log.DEBUG);
  //  Log.filterTag(LicenseArbiter.class.getSimpleName());
//    Log.level(Log.DEBUG);
    Report validReport = LicenseArbiter.report(c, policy);


//    System.exit(0);
  }

  public static void test1() throws LicenseExpressionException, IllegalLicenseExpression {
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


        policy.addWhiteLicense(apache20);
        policy.addGrayLicense(lgpl21);
        policy.addBlackLicense(gpl20);

     */
//    Log.level(Log.DEBUG);
//    Log.filterTag(LicenseArbiter.LOG_TAG);
    Report validReport = LicenseArbiter.report(validComponent(), policy);


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
//   Log.level(Log.DEBUG);
    validReport = LicenseArbiter.report(validComponent(), policy);


//    System.out.println("violations: " + validReport.violation().obligations());
  }

    //Report invalidReport = LicenseArbiter.report(invalidComponent(), policy);

  public static void test3() throws LicenseExpressionException, IllegalLicenseExpression {

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



  }

  public static void test4() throws LicenseExpressionException, IllegalLicenseExpression {

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

  //  Log.level(Log.DEBUG);


  }


  public static void main(String[] args) throws IOException, LicenseExpressionException, IllegalLicenseExpression {
    test();
  }
}
