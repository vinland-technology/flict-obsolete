package com.sandklef.compliance.json.test;

import java.io.IOException;

import com.sandklef.compliance.*;
import com.sandklef.compliance.json.*;

public class TestJsonParser {

  public static void main(String[] args) throws IOException{
    JsonParser jp = new JsonParser(args[0]);

    Component c = jp.readComponent();

    System.out.println("component: " + c);

    c.checkViolationSafely();
  }
  
}
