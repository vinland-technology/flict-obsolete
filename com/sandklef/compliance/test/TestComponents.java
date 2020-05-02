// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.test;
import com.sandklef.compliance.domain.*;
import static com.sandklef.compliance.test.Utils.*;

public class TestComponents {

  public static void test() {
    Component valid = validComponent();
    Component invalid = invalidComponent();
    printTestStart("TestComponents");
    printSubTestStart("Valid component");
    assertHelper("Verify dependency size of top", valid.dependencies().size()==2, "OK");
    assertHelper("Verify total number of dependencies", countDependencies(valid)==8, "OK");
    assertHelper("Verify top level name", valid.name().equals("Top"), "OK");
    assertHelper("Verify top level license", valid.licenses().get(0).spdxTag().equals("GPL-2.0-only"), "OK");
    printSubTestStart("Invalid component");
    assertHelper("Verify dependency size of top", invalid.dependencies().size()==2, "OK");
    assertHelper("Verify total number of dependencies", countDependencies(invalid)==8, "OK");
    assertHelper("Verify top level name", invalid.name().equals("InvalidTop"), "OK");
    assertHelper("Verify top level license", invalid.licenses().get(0).spdxTag().equals("Apache-2.0"), "OK");
  }



  public static void main(String args[]) {
    test();
  }

}
