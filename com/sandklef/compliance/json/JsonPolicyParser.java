// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.json;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sandklef.compliance.domain.LicenseExpressionException;
import com.sandklef.compliance.domain.LicensePolicy;
import com.sandklef.compliance.domain.License;
import com.sandklef.compliance.utils.LicenseStore;
import com.sandklef.compliance.utils.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class JsonPolicyParser {

  public static final String SW_TAG = "software";
  public static final String SW_VERSION_TAG = "version";
  
  public static final String LOG_TAG = JsonPolicyParser.class.getSimpleName();

  public LicensePolicy readLicensePolicy(String fileName) throws IOException, LicenseExpressionException {
    Log.d(LOG_TAG, " reading from file: " + fileName);
    Log.d(LOG_TAG, "content: " + new String(Files.readAllBytes(Paths.get(fileName))));
    return readLicensePolicyString(new String(Files.readAllBytes(Paths.get(fileName))));
  }
  private LicensePolicy readLicensePolicyString(String str) throws LicenseExpressionException {
    JsonObject jo = new JsonParser().parse(str).getAsJsonObject();
    Log.d(LOG_TAG, "MetaData: " + JsonMetaInfoParser.readMetaData(jo.getAsJsonObject(JsonTags.META_TAG)));

    JsonObject polocyJson = jo.get("policy").getAsJsonObject();
    Gson gson = new Gson();
    LicensePolicyIntermediate policyGson = gson.fromJson(polocyJson, LicensePolicyIntermediate.class);
    return policyGson.export();
  }

  private static class LicensePolicyIntermediate {
    private String name;
    private List<String> allowlist;
    private List<String> avoidlist;
    private List<String> deniedlist;

    private List<License> convert(List<String> stringLicenses) throws LicenseExpressionException {
      List<License> licenses = new ArrayList<>();
      if (stringLicenses != null) {
        for (String s : stringLicenses) {
          licenses.add(LicenseStore.getInstance().license(s));
        }
      }
      return licenses;
    }

    public LicensePolicy export() throws LicenseExpressionException {
      return new LicensePolicy(name,
              convert(allowlist),
              convert(avoidlist),
              convert(deniedlist));
    }
  }

}


  