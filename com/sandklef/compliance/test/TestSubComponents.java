package com.sandklef.compliance.test;

import java.util.List;
import java.util.ArrayList;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.utils.*;
import static com.sandklef.compliance.domain.License.*;

public class TestSubComponents {

  private static License lgpl2 = LicenseStore.getInstance().license(LGPL_2_0_SPDX);
  private static License gpl2 = LicenseStore.getInstance().license(GPL_2_0_SPDX);
  private static License apache2 = LicenseStore.getInstance().license(APACHE_2_0_SPDX);

  public static void main(String[] args) {
    // a1 
    Component a1 = new Component("a1", apache2, null);

    // a2
    Component a2 = new Component("a2", gpl2, null);
    a2.concludedLicense(null);
    
    // a    q
    ArrayList<Component> aDeps = new ArrayList<>();
    aDeps.add(a1);
    aDeps.add(a2);
    Component a = new Component("a", apache2, aDeps);

    if (LicenseArbiter.checkSubComponentsSafely(a)) {
      System.out.println("Did not find faulty sub license");
      System.exit(1);
    }
    System.out.println("Found faulty sub license which is correct :)");

    
     a2 = new Component("a2", gpl2, null);
     aDeps = new ArrayList<>();
     aDeps.add(a1);
     aDeps.add(a2);
     a = new Component("a", apache2, aDeps);
     
     if (LicenseArbiter.checkSubComponentsSafely(a)) {
       System.out.println("Did not find faulty sub license :)");
     } else {
       System.out.println("Found faulty sub license :(");
       System.exit(1);
     }       
  }
  
}
