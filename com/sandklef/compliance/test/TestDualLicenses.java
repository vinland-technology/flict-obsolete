// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.test;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.utils.*;

import static com.sandklef.compliance.test.Utils.*;

public class TestDualLicenses {

  private final static String LOG_TAG = TestDualLicenses.class.getSimpleName();

  public static void test() {
    printTestStart("TestDualLicenses");

//    Log.filterTag(LOG_TAG);

    Report validReport = LicenseArbiter.report(dualLicensedComponent(), null);

    //Log.level(Log.ERROR);
  }

  public static void main(String[] args) {
    test();
  }
  
}
