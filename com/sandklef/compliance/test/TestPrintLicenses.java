package com.sandklef.compliance.test;

import java.util.List;
import java.util.ArrayList;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.utils.*;

public class TestPrintLicenses {

  private static License lgpl2 = LicenseStore.LGPL_2_1_ONLY;
  private static License gpl2 = LicenseStore.GPL_2_0_ONLY;
  private static License apache2 = LicenseStore.APACHE_2_0;

  public static void main(String[] args) {

    System.out.println("LGPLv2:  " + lgpl2);
    System.out.println("GPLv2:  " + gpl2);
    System.out.println("Apache: " + apache2);
    System.out.println();
    System.out.println();
  
  }
  
}
