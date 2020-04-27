package com.sandklef.compliance.test;

import java.util.List;
import java.util.ArrayList;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.utils.*;

public class TestLicense {

  private static License lgpl2 = LicenseStore.LGPL_2_1_ONLY;
  private static License gpl2 = LicenseStore.GPL_2_0_ONLY;
  private static License apache2 = LicenseStore.APACHE_2_0;

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
