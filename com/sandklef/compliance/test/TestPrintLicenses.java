// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.test;

import java.util.List;
import java.util.ArrayList;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.utils.*;
import static com.sandklef.compliance.domain.License.*;

public class TestPrintLicenses {

  private static License lgpl2;
  private static License gpl2;
  private static License apache2;

  static {
    try {
      lgpl2 = LicenseStore.getInstance().license(LGPL_2_0_SPDX);
      gpl2 = LicenseStore.getInstance().license(GPL_2_0_SPDX);
      apache2 = LicenseStore.getInstance().license(APACHE_2_0_SPDX);
    } catch (LicenseExpressionException e) {
      e.printStackTrace();
    }
  }


  public static void main(String[] args) {

    System.out.println("LGPLv2:  " + lgpl2);
    System.out.println("GPLv2:  " + gpl2);
    System.out.println("Apache: " + apache2);
    System.out.println();
    System.out.println();
  
  }
  
}
