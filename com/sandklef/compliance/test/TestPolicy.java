// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.test;

import com.sandklef.compliance.domain.License;
import com.sandklef.compliance.domain.LicensePolicy;
import com.sandklef.compliance.domain.Report;
import com.sandklef.compliance.utils.LicenseArbiter;
import com.sandklef.compliance.utils.Log;

import java.security.Policy;

import static com.sandklef.compliance.test.Utils.*;

public class TestPolicy {

  private static boolean testCanAUseB(License user, License usee) {
    boolean ret = LicenseArbiter.canAUseB(null, null, user,usee);
    return ret;
  }

  public static void test() {
    LicensePolicy policy = Utils.permissiveAndWeakPolicy();

    printTestStart("TestPolicy");
    printSubTestStart("Valid component and Permissive/weak policy");
    Report validReport = LicenseArbiter.report(validComponent(), policy);
/*
    System.out.println("  result: " + validReport.conclusion().licenseConclusions().size() +
            " " + validReport.concern().licenseConcerns().size() +
            " " + validReport.violation().obligations().size());
*/

    assertHelper("concerns", validReport.conclusion().licenseConclusions().size() == 0);
    assertHelper(" conclusions: ", validReport.concern().licenseConcerns().size() == 2);
    assertHelper("violations: ", validReport.violation().obligations().size() == 3);

    printSubTestStart("Valid component and Copyleft/weak policy");
    policy = Utils.copyleftAndWeakPolicy();
  //  Log.level(Log.DEBUG);
    validReport = LicenseArbiter.report(validComponent(), policy);
    //Log.level(Log.ERROR);
/*    System.out.println("  result: " + validReport.conclusion().licenseConclusions().size() +
            " " + validReport.concern().licenseConcerns().size() +
            " " + validReport.violation().obligations().size());
 */

    assertHelper("concerns", validReport.conclusion().licenseConclusions().size() == 0);

    assertHelper(" conclusions: ", validReport.concern().licenseConcerns().size() == 4);
    assertHelper("violations: ", validReport.violation().obligations().size() == 7);


//    System.out.println("violations: " + validReport.violation().obligations());
  }

    //Report invalidReport = LicenseArbiter.report(invalidComponent(), policy);

  public static void main(String[] args) {
      test();
  }
}
