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

import java.io.IOException;
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

  public static final String LOG_TAG = JsonComponentParser.class.getSimpleName();
  
  public Component readComponent(String fileName) throws IOException{
    return readComponent(new JSONObject(new String(Files.readAllBytes(Paths.get(fileName)))));
  }
  
  private Component readComponent(JSONObject jo) {
    Log.d(LOG_TAG,"readComponent");
    String name = readJsonString(jo, NAME_TAG, "");
    Log.d(LOG_TAG,"readComponent  name: " + name);

    List<Component> dependencies = new ArrayList<>();
    try {
      JSONArray dependenciesJson = jo.getJSONArray(DEPENDENCIES_TAG);
      
      for (int i = 0; i < dependenciesJson.length(); i++) {
        JSONObject component = dependenciesJson.getJSONObject(i);
        dependencies.add(readComponent(component));
      }
    } catch (JSONException e) {
      Log.d(LOG_TAG,"Uh oh.... " + e);
    }

    
    String licenseName = readJsonString(jo, LICENSE_TAG, "");

    Log.d(LOG_TAG,"LicenseName: " + licenseName);

    String delimiter = "|";

    if (licenseName.contains(delimiter)) {
      List<License> licenses = new ArrayList<>();
      Log.d(LOG_TAG,name + " LicenseName: " + licenseName + " contains PIPE");

      List<String> licenseList = Arrays.asList(Pattern.compile("\\|").split(licenseName));

      //String[] licenseList = licenseName.split(Pattern.quote("|"));
      //      String[] licenseList = licenseName.split(Pattern.quote("|"));
      Log.d(LOG_TAG," list: " + licenseList);
      for(String l : licenseList){
        Log.d(LOG_TAG," * \"" + l.trim() + "\"");
        licenses.add(LicenseStore.getInstance().license(l.trim()));
      }
      Log.d(LOG_TAG,name + " will get licenses: " + licenses);
      return new Component(name, licenses, dependencies);
    } else {
      Log.d(LOG_TAG,name + " LicenseName: " + licenseName + " DOES NOT contains PIPE");
      License license = LicenseStore.getInstance().license(licenseName.trim());
      Log.d(LOG_TAG,name + " will get license: " + license + "   no PIPE");
      return new Component(name, license, dependencies);
    }
  }

}


  