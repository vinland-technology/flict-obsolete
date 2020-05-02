// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.test;

import java.util.List;
import java.util.ArrayList;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.utils.*;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import static com.sandklef.compliance.domain.License.*;
import static com.sandklef.compliance.test.Utils.*;

public class TestDualLicenses {

  public static void test() {
    printTestStart("TestDualLicenses");
    Report validReport = LicenseArbiter.report(dualLicensedComponent(), null);
    assertHelper("concluded license on dual licensed component",
            validReport.conclusion().licenseConclusions().size()==1);
  }

  public static void main(String[] args) {
    test();
  }
  
}
