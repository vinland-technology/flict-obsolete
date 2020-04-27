package com.sandklef.compliance.test;

import java.util.List;
import java.util.ArrayList;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.utils.*;

public class TestCanAUseB {

  private static License lgpl2 = LicenseStore.LGPL_2_1_ONLY;
  private static License gpl2 = LicenseStore.GPL_2_0_ONLY;
  private static License apache2 = LicenseStore.APACHE_2_0;

  private static boolean testCanAUseB(License user, License usee) {
    boolean ret = LicenseArbiter.canAUseB(user,usee);
    System.out.println(user.spdxTag() + " using " + usee.spdxTag() + " :::  " + ret);
    return ret;
  }

  public static void main(String[] args) {
    assert testCanAUseB(gpl2, apache2) ;
    assert !testCanAUseB(apache2, gpl2);
  }
  
}
