package com.sandklef.compliance.test;

import java.util.List;
import java.util.ArrayList;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.utils.*;

public class TestComponents {

  private static License lgpl2 = LicenseStore.LGPL_2_1_ONLY;
  private static License gpl2 = LicenseStore.GPL_2_0_ONLY;
  private static License apache2 = LicenseStore.APACHE_2_0;

  public static void main(String[] args) {
    /*
             top
              |
           +--+---------------+
           |                  |
           a                  b
           |                  |
        ---+----       +------+--------+         
        |      |       |               |
        a1    a2       b1             b2
                       |
                       +-----+
                       |     |
                       b11   b12

     */
    // a1 
    Component a1 = new Component("a1", apache2, null);

    // a2 
    Component a2 = new Component("a2", lgpl2, null);

    // a    q
    ArrayList<Component> aDeps = new ArrayList<>();
    aDeps.add(a1);
    aDeps.add(a2);
    Component a = new Component("a", apache2, aDeps);

    // b11
    Component b11 = new Component("b11", apache2, null);

    // b12
    Component b12 = new Component("b12", apache2, null);

    // b1
    ArrayList<Component> b1Deps = new ArrayList<>();
    b1Deps.add(b11);
    b1Deps.add(b12);
    Component b1 = new Component("b1", apache2, b1Deps);

    // b2
    Component b2 = new Component("b2", gpl2, null);

    // b
    ArrayList<Component> bDeps = new ArrayList<>();
    bDeps.add(b1);
    bDeps.add(b2);
    Component b = new Component("b", lgpl2, bDeps);


    // top
    ArrayList<Component> deps = new ArrayList<>();
    deps.add(a);
    deps.add(b);
    Component top = new Component("Top", gpl2, deps);


    System.out.println("Short listing: " + top);
    System.out.println("Long listing:  " + top.toStringLong());
    System.out.println();
    System.out.println();
    System.out.println();
    Log.level(Log.DEBUG);
    boolean ret = LicenseArbiter.checkViolationSafely(top);
    System.out.println("Violation? : " + ret);
  }
}
