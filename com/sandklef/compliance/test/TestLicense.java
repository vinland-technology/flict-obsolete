// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.test;

import java.util.List;
import java.util.ArrayList;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.utils.*;
import static com.sandklef.compliance.domain.License.*;

public class TestLicense {

  private static License lgpl2 = LicenseStore.getInstance().license(LGPL_2_0_SPDX);
  private static License gpl2 = LicenseStore.getInstance().license(GPL_2_0_SPDX);
  private static License apache2 = LicenseStore.getInstance().license(APACHE_2_0_SPDX);

  private static void testAUsesB(License user, License usee) {
    System.out.print(user.spdxTag() + " can use " + usee.spdxTag() + " :::  " );
    try {
      LicenseArbiter.aUsesB(user,usee);
      System.out.println(" ok");
    } catch (LicenseViolationException e) {
      System.out.println(" violation");
    }
  }

  public static void main(String[] args) {
      testAUsesB(apache2, apache2);
      testAUsesB(apache2, gpl2);
      testAUsesB(apache2, lgpl2);
      
      testAUsesB(gpl2, apache2);
      testAUsesB(gpl2, gpl2);
      testAUsesB(gpl2, lgpl2);
      
      testAUsesB(lgpl2, apache2);
      testAUsesB(lgpl2, gpl2);
      testAUsesB(lgpl2, lgpl2);
    }
  
}
