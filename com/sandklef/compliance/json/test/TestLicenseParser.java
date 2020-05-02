// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.json.test;

import java.io.IOException;
import java.util.Map;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.utils.*;
import com.sandklef.compliance.json.*;

public class TestLicenseParser {

  public static void main(String[] args) throws IOException{
    int fileIndex=0;
    boolean compliant = true;
    String dirName = "licenses/json";

    if (args.length > 0 && args[fileIndex].equals("--verbose")) {
      Log.level(Log.VERBOSE);
      fileIndex++;
    }

    JsonLicenseParser jp = new JsonLicenseParser();
    Map<String, License> licenses = jp.readLicenseDir(dirName);

    System.out.println("License: " + licenses);
  }
}
