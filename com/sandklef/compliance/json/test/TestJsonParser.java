package com.sandklef.compliance.json.test;

import java.io.IOException;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.utils.*;
import com.sandklef.compliance.json.*;

public class TestJsonParser {

  public static void main(String[] args) throws IOException{
    int fileIndex=0;
    boolean compliant = true;

    if (args.length==0) {
      System.out.println("Missing argument");
    }

    if (args[0].equals("--violation")) {
      compliant = false;
      fileIndex=1;
    }

    
    JsonParser jp = new JsonParser(args[fileIndex]);
    Component c = jp.readComponent();
    System.out.println("component: " + c);

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
