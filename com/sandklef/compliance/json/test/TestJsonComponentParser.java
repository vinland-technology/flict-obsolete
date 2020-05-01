// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.json.test;

import java.io.IOException;
import java.util.Map;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.utils.*;
import com.sandklef.compliance.json.*;

public class TestJsonComponentParser {

  public static void main(String[] args) throws IOException{
    int fileIndex=0;
    boolean compliant = true;

    if (args.length==0) {
      System.out.println("Missing argument");
    }

    if (args[fileIndex].equals("--violation")) {
      compliant = false;
      fileIndex++;
    }

    if (args[fileIndex].equals("--verbose")) {
      Log.level(Log.VERBOSE);
      fileIndex++;
    }

    
    
    JsonComponentParser jp = new JsonComponentParser();

    Map<String, License> licenses = new JsonLicenseParser().readLicenseDir("licenses/json");
    LicenseStore.getInstance().addLicenses(licenses);
    System.out.println("component file: " + args[fileIndex]);
    Component c = jp.readComponent(args[fileIndex]);

    
    System.out.println(" --- licenses ...------");

    System.out.println("component read: " + c);

    if (compliant) {
      LicenseArbiter.checkViolationSafely(c);
      try {
        LicenseArbiter.checkViolation(c);
      } catch (LicenseViolationException e) {
        System.out.println("Violation detected ... this was unexpected");
        System.exit(1);
      }
    } else {
      LicenseArbiter.checkViolationSafely(c);
      try {
        LicenseArbiter.checkViolation(c);
        System.out.println("Violation detected ... this was unexpected");
        System.exit(1);
      } catch (LicenseViolationException e) {
        System.out.println("Violation detected ... this was expected");
      }
    }
  }
  
}