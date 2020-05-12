// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.json;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.utils.*;

import static com.sandklef.compliance.json.JsonUtils.readJsonString;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import java.util.regex.Pattern;

public class JsonComponentParser {

  public static final String COMPONENT_TAG = "component";
  public static final String DEPENDENCIES_TAG = "dependencies";
  public static final String LICENSE_TAG = "license";
  public static final String NAME_TAG = "name";
  public static final String INCLUDE_DEPENDENCIES_TAG = "include_dependencies";

  public static final String LOG_TAG = JsonComponentParser.class.getSimpleName();

  public Component readComponent(String fileName) throws IOException{
    if (fileName.equals("-")) {
      BufferedReader reader =
              new BufferedReader(new InputStreamReader(System.in));
      StringBuilder sb = new StringBuilder();
      try {
        while(true) {
          String line = reader.readLine();
          if (line==null) {
            break;
          }
          sb.append(line);
//          System.out.println(" sb: " + sb.toString());
        }
      } catch (IOException e) {
        System.out.println(e);
        System.exit(1);
      }
      return readComponent(new JSONObject(sb.toString()));
    }
    return readComponent(new JSONObject(new String(Files.readAllBytes(Paths.get(fileName)))));
  }

  public Component readComponent(JSONObject jo) throws IOException {
    //MetaData meta = readMetaData(jo);
    return readComponentHelper(jo.getJSONObject("component"));
  }

  private Component readComponentHelper(JSONObject jo) throws IOException {
    Log.d(LOG_TAG,"readComponent");
    String name = readJsonString(jo, NAME_TAG, "");
    Log.d(LOG_TAG,"readComponent  name: " + name);

    List<Component> dependencies = new ArrayList<>();
    try {
      JSONArray dependenciesJson = jo.getJSONArray(DEPENDENCIES_TAG);
      for (int i = 0; i < dependenciesJson.length(); i++) {
        JSONObject component = dependenciesJson.getJSONObject(i);
        dependencies.add(readComponentHelper(component));
      }
    } catch (JSONException e) {
      Log.d(LOG_TAG,"Uh oh.... " + e);
    }
    try {
      JSONArray includeJson = jo.getJSONArray(INCLUDE_DEPENDENCIES_TAG);

      for (int i = 0; i < includeJson.length(); i++) {
        String componentName = includeJson.getString(i);
        Component component = readComponent("com/sandklef/compliance/json/test/include-components/" + componentName + ".json");
      //    System.out.println("Reading component from: " + "com/sandklef/compliance/json/test/include-components/" + componentName + ".json" +  "   " + component);
          dependencies.add(component);
        }
    } catch (JSONException e) {
      Log.d(LOG_TAG,"Uh oh.... " + e);
    }

    
    String licenseName = readJsonString(jo, LICENSE_TAG, "");

    Log.d(LOG_TAG,"LicenseName: " + licenseName);
    boolean dualLicensed = licenseName.contains("|");
    if (licenseName.contains("|") ||
            licenseName.contains("&")) {

      List<License> licenses = licensesToList(licenseName);
      return new Component(name, licenses, dependencies, dualLicensed);
    } else {
      Log.d(LOG_TAG,name + " LicenseName: " + licenseName + " DOES NOT contains PIPE");
      License license = LicenseStore.getInstance().license(licenseName.trim());
//      System.out.println("License: " + license);
      Log.d(LOG_TAG,name + " will get license: " + license + "   no PIPE");
      return new Component(name, license, dependencies);
    }
  }

  private List<License> licensesToList(String licensefield) {
    List<String> licenseList ;
    List<License> licenses = new ArrayList<>();

    if (licensefield.contains("|")) {
      licenseList = Arrays.asList(Pattern.compile("\\|").split(licensefield));
    } else {
      licenseList = Arrays.asList(Pattern.compile("&").split(licensefield));
    }

    for(String l : licenseList){
      Log.d(LOG_TAG," * \"" + l.trim() + "\"");
      licenses.add(LicenseStore.getInstance().license(l.trim()));
    }
    return licenses;
  }


  public MetaData readMetaDate(String fileName) throws IOException {
    return readMetaData(new JSONObject(new String(Files.readAllBytes(Paths.get(fileName)))));
  }
  public MetaData readMetaData(JSONObject jo) {
    return new MetaData(jo.getString("software"),
            jo.getString("version"));
  }

}


  
