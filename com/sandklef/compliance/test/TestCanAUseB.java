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
    boolean ret = LicenseArbiter.aCanUseB(user,usee);
//    System.out.println(user.spdxTag() + " using " + usee.spdxTag() + " :::  " + ret);
    return ret;
  }

  public static void test() {
    printTestStart("TestCanAUseB");
    //Log.level(Log.DEBUG);
    assertHelper("gpl2 can NOT use apache2", !testCanAUseB(gpl20, apache20));

    assertHelper("gpl2+ can NOT use apache2", !testCanAUseB(gpl20_later, apache20));

    assertHelper("gpl3 can use apache2", testCanAUseB(gpl30, apache20));

    assertHelper("apache2 can NOT use gpl2", !testCanAUseB(apache20, gpl20));

    assertHelper("apache2 can NOT use gpl22", !testCanAUseB(apache20, gpl20_later));
  }

  public static void main(String[] args) {
        test();
  }
}
