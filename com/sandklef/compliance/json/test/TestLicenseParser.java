// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.json.test;

import java.io.IOException;
import java.util.Map;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.utils.*;
import com.sandklef.compliance.json.*;

import static com.sandklef.compliance.test.Utils.*;

public class TestLicenseParser {

  public static void test() throws IOException, IllegalLicenseExpression {
    String dirName = "com/sandklef/compliance/json/test/licenses/";

    JsonLicenseParser jp = new JsonLicenseParser();
    Map<String, License> licenses = jp.readLicenseDir(dirName);

    printTestStart("TestLicenseParser");
    assertHelper("Two licenses read", licenses.size()==2);
    assertHelper("One is gpl2", licenses.get("GPL-2.0-only")!=null);
    assertHelper("One is gpl3", licenses.get("GPL-3.0-only")!=null);
//    assertHelper("Gplv2 is copyleft", licenses.get("GPL-2.0-only").isCopyleft());
  //  assertHelper("Gplv3 is copyleft", licenses.get("GPL-3.0-only").isCopyleft());

/*    System.out.println(licenses.get("GPL-2.0-only").obligations() + "\n\n");
    System.out.println(licenses.get("GPL-2.0-only").obligations().get(Obligation.DISCLOSE_SOURCE_NAME));
*/

    /*
    assertHelper("Gplv2 - disclose source",
            licenses.get("GPL-2.0-only").obligations().get(Obligation.DISCLOSE_SOURCE_NAME).state() == ObligationState.TRUE);
    assertHelper("Gplv3 - disclose source",
            licenses.get("GPL-3.0-only").obligations().get(Obligation.DISCLOSE_SOURCE_NAME).state() == ObligationState.TRUE);
*/

  }

  public static void main(String[] args) throws IOException, IllegalLicenseExpression {
    test();
  }
}
