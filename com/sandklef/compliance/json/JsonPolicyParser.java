// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.json;

import com.sandklef.compliance.domain.LicensePolicy;
import com.sandklef.compliance.domain.License;
import com.sandklef.compliance.utils.LicenseStore;
import com.sandklef.compliance.utils.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class JsonPolicyParser {

  public static final String SW_TAG = "software";
  public static final String SW_VERSION_TAG = "version";
  
  public static final String LOG_TAG = JsonPolicyParser.class.getSimpleName();

  public LicensePolicy readLicensePolicy(String fileName) throws IOException{
    return readLicensePolicy(new JSONObject(new String(Files.readAllBytes(Paths.get(fileName)))));
  }

  private LicensePolicy readLicensePolicy(JSONObject jo) {
    Log.d(LOG_TAG,"readPolicy");
    List<License> whiteList = readLicenses(jo.getJSONArray("whitelist"));
    List<License> grayList = readLicenses(jo.getJSONArray("graylist"));
    List<License> blackList = readLicenses(jo.getJSONArray("blacklist"));

    return new LicensePolicy(whiteList, grayList, blackList);
  }

  private List<License> readLicenses(JSONArray jArray) {
    Log.d(LOG_TAG, " readLicenes(): " + jArray);
    List<License> licenses = new ArrayList<>();
    for (int i = 0; i < jArray.length(); i++) {
//      JSONObject licenseJson = jArray.getJSONObject(i);
  //    String licenseName = licenseJson.getString("license");
      Log.d(LOG_TAG, " object: " + jArray.get(i));
      String licenseName = jArray.get(i).toString();
      Log.d(LOG_TAG, " licenseName: " + licenseName);
      License license = LicenseStore.getInstance().license(licenseName.trim());
      licenses.add(license);
    }
    return licenses;
  }



}


  