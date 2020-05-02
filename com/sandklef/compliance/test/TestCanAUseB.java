// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.test;

import java.util.List;
import java.util.ArrayList;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.utils.*;
import static com.sandklef.compliance.domain.License.*;

public class TestCanAUseB {

  private static License lgpl2 = LicenseStore.getInstance().license(LGPL_2_0_SPDX);
  private static License gpl2 = LicenseStore.getInstance().license(GPL_2_0_SPDX);
  private static License apache2 = LicenseStore.getInstance().license(APACHE_2_0_SPDX);

  private static boolean testCanAUseB(License user, License usee) {
    boolean ret = LicenseArbiter.canAUseB(null, null, user,usee);
    System.out.println(user.spdxTag() + " using " + usee.spdxTag() + " :::  " + ret);
    return ret;
  }

  public static void main(String[] args) {
    assert testCanAUseB(gpl2, apache2) ;
    assert !testCanAUseB(apache2, gpl2);
  }
  
}
