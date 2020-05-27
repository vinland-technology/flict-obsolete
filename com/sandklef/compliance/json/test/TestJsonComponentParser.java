// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.json.test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.utils.*;
import com.sandklef.compliance.json.*;

import static com.sandklef.compliance.test.Utils.*;

public class TestJsonComponentParser {

  private static final String LOG_TAG = TestJsonComponentParser.class.getCanonicalName() ;

  public static void test() throws IOException {
    test_dual();
    test_many();
  }

  public static void test_dual() throws IOException {
    int fileIndex=0;
    boolean compliant = true;

    JsonComponentParser jp = new JsonComponentParser();

    Map<String, License> licenses = new JsonLicenseParser().readLicenseDir("licenses/json");
    LicenseStore.getInstance().addLicenses(licenses);
    List<Component> components = jp.readComponent("com/sandklef/compliance/json/test/simple.json");

    Component c = components.get(0);
    printTestStart("TestJsonComponentParser");
    printSubTestStart("Dual");

    assertHelper("Component name", c.name().equals("Main program"));
    assertHelper("Component has two dependencies", c.dependencies().size()==2);
    assertHelper("Component is licensed under lgpl", c.licenses().get(0).spdx().equals(gpl20.spdx()));
    Component sub0 = c.dependencies().get(0);
    Component sub1 = c.dependencies().get(1);
    assertHelper("Component's sub components name",
            sub0.name().equals("LGPL-lib") || sub1.name().equals("LGPL-lib"));
    assertHelper("Component's sub components name",
            sub0.name().equals("A-lib") || sub1.name().equals("A-lib"));
    //System.out.println( "\"" + sub0.licenses().size() +  " " + sub1.licenses().size() +  " ");
    assertHelper("Component's sub components both have one sub",
            sub0.licenses().size()==1 || sub1.licenses().size()==1);

    if (sub0.name().equals("A-lib")) {
      assertHelper("A-lib has two sub components",
              sub0.dependencies().size()==2);
    } else {
      assertHelper("A-lib has two sub components",
              sub1.dependencies().size()==2);
    }

  }

  public static void test_many() throws IOException {
    int fileIndex=0;
    boolean compliant = true;
    printSubTestStart("Many");

    JsonComponentParser jp = new JsonComponentParser();

    Map<String, License> licenses = new JsonLicenseParser().readLicenseDir("licenses/json");
    LicenseStore.getInstance().addLicenses(licenses);


    Log.d(LOG_TAG, " * " + jp);

    List<Component> components = jp.readComponent("com/sandklef/compliance/json/test/simple-dual.json");

    assertHelper("Components is 1", components.size()==1);

   // Log.level(Log.DEBUG);
    Log.d(LOG_TAG, " components: " + components);

  }

  public static void main(String[] args) throws IOException {
//    test();
    test_many();
  }
  
}
