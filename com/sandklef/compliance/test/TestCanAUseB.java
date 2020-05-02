// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.test;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.utils.LicenseArbiter;

import static com.sandklef.compliance.test.Utils.*;

public class TestCanAUseB {

  private static boolean testCanAUseB(License user, License usee) {
    boolean ret = LicenseArbiter.canAUseB(null, null, user,usee);
//    System.out.println(user.spdxTag() + " using " + usee.spdxTag() + " :::  " + ret);
    return ret;
  }

  public static void test() {
    printTestStart("TestCanAUseB");
    assertHelper("gpl2 can use apache2", testCanAUseB(gpl2, apache2));
    assertHelper("apache2 can NOT use gpl2", !testCanAUseB(apache2, gpl2));
  }

  public static void main(String[] args) {
      test();
  }
}
