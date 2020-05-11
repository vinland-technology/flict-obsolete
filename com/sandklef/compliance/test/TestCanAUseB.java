// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.test;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.utils.LicenseArbiter;
import com.sandklef.compliance.utils.Log;

import static com.sandklef.compliance.test.Utils.*;

public class TestCanAUseB {

  private static boolean testCanAUseB(License user, License usee) {
    boolean ret = LicenseArbiter.canAUseB(null, null, user,usee);
//    System.out.println(user.spdxTag() + " using " + usee.spdxTag() + " :::  " + ret);
    return ret;
  }

  public static void test() {
    printTestStart("TestCanAUseB");
    Log.level(Log.DEBUG);
    assertHelper("gpl2 can use apache2", testCanAUseB(gpl20, apache20));
    System.out.println("   **** gpl use apache2: " + testCanAUseB(gpl20, apache20));
    System.out.println("   **** apache2 use gpl: " + testCanAUseB(apache20, gpl20));
    assertHelper("apache2 can NOT use gpl2", !testCanAUseB(apache20, gpl20));
    System.exit(1);
  }

  public static void main(String[] args) {
      test();
  }
}
