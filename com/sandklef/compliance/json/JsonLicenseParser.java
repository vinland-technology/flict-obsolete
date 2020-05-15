// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.json;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sandklef.compliance.domain.License;
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
  
  public Map<String, License> readLicenseDir(String dirName) throws IOException {
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
    for(File f : files){
      if (f.toString().contains("template.json")) {
        continue;
      }
      Log.d(LOG_TAG,"file: " + f);
      License l = readLicense(f.toString());
      licenses.put(l.spdx(), l);
    }
    return licenses;
  }

  public License readLicense(String fileName) throws IOException {
    return readLicenseString(new String(Files.readAllBytes(Paths.get(fileName))));
  }

  public License readLicenseString(String json) {
    JsonObject jo = new JsonParser().parse(json).getAsJsonObject();
    JsonObject licenseJson = jo.get("license").getAsJsonObject();
    Gson gson = new Gson();
    License license = gson.fromJson(licenseJson, License.class);
    return license;
  }

/*
      public License readLicense(String fileName) throws IOException {
    return readLicense(new JSONObject(new String(Files.readAllBytes(Paths.get(fileName)))));
  }

  public License readLicense(JSONObject jo) {
    // Meta information
    String swName = JsonUtils.readJsonString(jo, SW_TAG, "");
    int version = JsonUtils.readJsonInt(jo, SW_VERSION_TAG, 0);

    Log.d(LOG_TAG, "meta: " + swName + "   " + version);

    JSONObject lic = jo.getJSONObject("license");
    String name = lic.getString("name");
    String spdx = lic.getString("spdx");
    JSONObject obl = lic.getJSONObject("obligations");
    Log.d(LOG_TAG, "name: " + name + "   spdx" + spdx);

    return new License(name, spdx);
  }
*/
//    System.out.println("  jsonP: " + spdx + "  " + INCLUDE_INSTALL_INSTRUCTIONS + "  " + toObligationState(obl, INCLUDE_INSTALL_INSTRUCTIONS_TAG));

//
//    return new License(spdx,
//                       new ObligationBuilder().
//                       add(LINKING_COPYLEFTED, toObligationState(obl,LINKING_COPYLEFTED_TAG )).
//                       add(MODIFICATION_ALLOWED, toObligationState(obl, MODIFICATION_ALLOWED_TAG)).
//                       add(MODIFICATION_COPYLEFTED, toObligationState(obl, MODIFICATION_COPYLEFTED_TAG)).
//                       add(SUBLICENSING_ALLOWED, toObligationState(obl, SUBLICENSING_ALLOWED_TAG)).
//                       add(DISTRIBUTION_ALLOWED, toObligationState(obl, DISTRIBUTION_ALLOWED_TAG)).
//                       add(DISTRIBUTION_COPYLEFTED, toObligationState(obl, DISTRIBUTION_COPYLEFTED_TAG)).
//                       add(DISCLOSE_SOURCE, toObligationState(obl, DISCLOSE_SOURCE_TAG)).
//                       add(STATE_CHANGES, toObligationState(obl, STATE_CHANGES_TAG)).
//                       add(INCLUDE_COPYRIGHT, toObligationState(obl, INCLUDE_COPYRIGHT_TAG)).
//                       add(INCLUDE_LICENSE, toObligationState(obl, INCLUDE_LICENSE_TAG)).
//                       add(INCLUDE_INSTALL_INSTRUCTIONS, toObligationState(obl, INCLUDE_INSTALL_INSTRUCTIONS_TAG)).
//                       add(INCLUDE_NOTICE_FILE, toObligationState(obl, INCLUDE_NOTICE_FILE_TAG)).
//                       add(INCLUDE_NOTICE_ABOUT_LICENSE, toObligationState(obl, INCLUDE_NOTICE_ABOUT_LICENSE_TAG)).
//                       build());
//  }
//
//  private ObligationState toObligationState(JSONObject jo, String key) {
//    Log.d(LOG_TAG,"reading value for key: " + key + " from: " + jo);
//    String value = jo.getString(key);
//    if (value==null) {
//      return ObligationState.UNDEFINED;
//    }
//    if (value.equals("true")) {
//      return ObligationState.TRUE;
//    } else if (value.equals("false")) {
//      return ObligationState.FALSE;
//    }
//    return ObligationState.UNDEFINED;
//  }



}


  
