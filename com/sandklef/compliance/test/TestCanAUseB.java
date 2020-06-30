// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.test;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.utils.LicenseArbiter;
import com.sandklef.compliance.utils.Log;

import static com.sandklef.compliance.test.Utils.*;

public class TestCanAUseB {

  private static boolean testCanAUseB(License user, License usee) throws IllegalLicenseExpression, LicenseConnector.LicenseConnectorException {
    boolean ret = LicenseArbiter.aCanUseB(user,usee);
//    System.out.println(user.spdxTag() + " using " + usee.spdxTag() + " :::  " + ret);
    return ret;
  }

  public static void test() throws IllegalLicenseExpression {
    printTestStart("TestCanAUseB");
    //Log.level(Log.DEBUG);
    assertHelper("gpl2 can NOT use apache2", !testCanAUseB(gpl20, apache20));

    assertHelper("gpl2+ can NOT use apache2", !testCanAUseB(gpl20_later, apache20));

    assertHelper("gpl3 can use apache2", testCanAUseB(gpl30, apache20));

    assertHelper("apache2 can NOT use gpl2", !testCanAUseB(apache20, gpl20));

    assertHelper("apache2 can NOT use gpl22", !testCanAUseB(apache20, gpl20_later));

    assertHelper("GPLv2+ can use LGPL 2.1", testCanAUseB(gpl20_later, lgpl21_only));

    assertHelper("LGPL 2.1 can NOT use GPLv2+", !testCanAUseB(lgpl21_only, gpl20_later));

    assertHelper("GPLv3 can use LGPL 2.1", testCanAUseB(gpl30, lgpl21_only));

    assertHelper("LGPL 2.1 can NOT use GPLv3", !testCanAUseB(lgpl21_only, gpl30));

    assertHelper("GPLv3 can use LGPL 2.1", testCanAUseB(gpl30, lgpl21_only));

    assertHelper("LGPL 2.1 can NOT use GPLv3", !testCanAUseB(lgpl21_only, gpl30));

    assertHelper("GPLv3 can use GPLV3+", testCanAUseB(gpl30, gpl30_later));

    assertHelper("GPLv3 can use GPLV3+", testCanAUseB(gpl30_later, gpl30));


  }


  public static void main(String[] args) throws IllegalLicenseExpression {
        test();
  }
}
