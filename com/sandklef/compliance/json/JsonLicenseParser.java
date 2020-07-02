// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.json;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sandklef.compliance.domain.IllegalLicenseExpression;
import com.sandklef.compliance.domain.License;
import com.sandklef.compliance.domain.LicenseGroup;
import com.sandklef.compliance.utils.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class JsonLicenseParser {

  public static final String SW_TAG = "software";
  public static final String SW_VERSION_TAG = "version";
  
  public static final String LOG_TAG = JsonLicenseParser.class.getSimpleName();

  public Map<String, License> readLicenseDir(String dirName) throws IOException, IllegalLicenseExpression {
    Map<String, License> licenses = new HashMap<>();
    File file = new File(dirName);
    Log.d(LOG_TAG,"file: " + dirName);
    File[] files = file.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        Log.d(LOG_TAG,"accept(): " + name);
        return name.toLowerCase().endsWith(".json");
      }
    });
    if (files==null) {
      throw new IllegalLicenseExpression("No licenses found in: \"" + dirName + "\"");
    }
    for(File f : files){
      if (f.toString().contains("-group.json")) {
        continue;
      }
      if (f.toString().contains("template.json")) {
        continue;
      }
      Log.d(LOG_TAG,"file: " + f);
      License l = readLicense(f.toString());
      licenses.put(l.spdx(), l);
    }
    return licenses;
  }

  public Map<String, LicenseGroup> readLicenseGroupDir(String dirName) throws IOException {
    Map<String, LicenseGroup> licenseGroups = new HashMap<>();
    File file = new File(dirName);
    Log.d(LOG_TAG,"file: " + dirName);
    File[] files = file.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        Log.d(LOG_TAG,"accept(): " + name);
        return name.toLowerCase().endsWith("-group.json");
      }
    });
    for(File f : files){
      if (f.toString().contains("template.json")) {
        continue;
      }
      Log.d(LOG_TAG,"file: " + f);
      LicenseGroup lg = readLicenseGroup(f.toString());
      licenseGroups.put(lg.name(), lg);
    }
    return licenseGroups;
  }

  public License readLicense(String fileName) throws IOException {
    return readLicenseString(new String(Files.readAllBytes(Paths.get(fileName))));
  }

  public LicenseGroup readLicenseGroup(String fileName) throws IOException {
    return readLicenseGroupString(new String(Files.readAllBytes(Paths.get(fileName))));
  }

  public License readLicenseString(String json) {
    JsonObject jo = new JsonParser().parse(json).getAsJsonObject();
    JsonObject licenseJson = jo.get("license").getAsJsonObject();
    Gson gson = new Gson();
    License license = gson.fromJson(licenseJson, License.class);
    return license;
  }

  public LicenseGroup readLicenseGroupString(String json) {
    JsonObject jo = new JsonParser().parse(json).getAsJsonObject();
    Gson gson = new Gson();
    JsonObject licenseGroupJson = jo.get("group_name").getAsJsonObject();
    LicenseGroup licenseGroup = gson.fromJson(licenseGroupJson, LicenseGroup.class);
    return licenseGroup;
  }


}


  
