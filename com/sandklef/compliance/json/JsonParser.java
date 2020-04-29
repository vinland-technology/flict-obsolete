package com.sandklef.compliance.json;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.utils.*;
import static com.sandklef.compliance.domain.Obligation.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;
import java.io.IOException;
import java.nio.file.Files;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class JsonParser {

  public static final String COMPONENT_TAG = "component";
  public static final String DEPENDENCIES_TAG = "dependencies";
  public static final String LICENSE_TAG = "license";
  public static final String NAME_TAG = "name";

  public static final String SW_TAG = "software";
  public static final String SW_VERSION_TAG = "version";
  
  public static final String LOG_TAG = JsonParser.class.getSimpleName();
  
  public Component readComponent(String fileName) throws IOException{
    return readComponent(new JSONObject(new String(Files.readAllBytes(Paths.get(fileName)))));
  }
  
  private Component readComponent(JSONObject jo) {
    System.out.println("readComponent");
    String name = readJsonString(jo, NAME_TAG, "");

    String licenseName = readJsonString(jo, LICENSE_TAG, "");
    License license = LicenseStore.getInstance().license(licenseName);
    System.out.println("LicenseName: " + licenseName);
    System.out.println("License: " + license);

    List<Component> dependencies = new ArrayList<>();
    try {
      JSONArray dependenciesJson = jo.getJSONArray(DEPENDENCIES_TAG);
      
      for (int i = 0; i < dependenciesJson.length(); i++) {
        JSONObject component = dependenciesJson.getJSONObject(i);
        dependencies.add(readComponent(component));
      }
    } catch (JSONException e) {
      System.out.println("Uh oh.... " + e);
    }
    return new Component(name, license, dependencies);
  }
  

  public Map<String, License> readLicenseDir(String dirName) throws IOException {
    Map<String, License> licenses = new HashMap<>();
    File file = new File(dirName);
    System.out.println("file: " + dirName);
    File[] files = file.listFiles(new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
          System.out.println("accept(): " + name);
          return name.toLowerCase().endsWith(".json");
        }
      });
    for(File f : files){
      if (f.toString().contains("template.json")) {
        continue;
      }
      System.out.println("file: " + f);
      License l = readLicense(f.toString());
      licenses.put(l.spdxTag(), l);
    }
    return licenses;
  }
  
  public License readLicense(String fileName) throws IOException {
    return readLicense(new JSONObject(new String(Files.readAllBytes(Paths.get(fileName)))));
  }

  public License readLicense(JSONObject jo) {
    // Meta information
    String swName = readJsonString(jo, SW_TAG, "");
    int version = readJsonInt(jo, SW_VERSION_TAG, 0);

    System.out.println("meta: " + swName + "   " + version);

    JSONObject lic = jo.getJSONObject("license");
    String name = lic.getString("name");
    String spdx = lic.getString("spdx");
    JSONObject obl = lic.getJSONObject("obligations");
    System.out.println("name: " + name + "   spdx" + spdx);
    return new License(spdx,
                       new ObligationBuilder().
                       add(LINKING_COPYLEFTED, toObligationState(obl,LINKING_COPYLEFTED_TAG )).
                       add(MODIFICATION_ALLOWED, toObligationState(obl, MODIFICATION_ALLOWED_TAG)).
                       add(MODIFICATION_COPYLEFTED, toObligationState(obl, MODIFICATION_COPYLEFTED_TAG)).
                       add(SUBLICENSING_ALLOWED, toObligationState(obl, SUBLICENSING_ALLOWED_TAG)).
                       add(DISTRIBUTION_ALLOWED, toObligationState(obl, DISTRIBUTION_ALLOWED_TAG)).
                       add(DISTRIBUTION_COPYLEFTED, toObligationState(obl, DISTRIBUTION_COPYLEFTED_TAG)).
                       add(DISCLOSE_SOURCE, toObligationState(obl, DISCLOSE_SOURCE_TAG)).
                       add(STATE_CHANGES, toObligationState(obl, STATE_CHANGES_TAG)).
                       add(INCLUDE_COPYRIGHT, toObligationState(obl, INCLUDE_COPYRIGHT_TAG)).
                       add(INCLUDE_LICENSE, toObligationState(obl, INCLUDE_LICENSE_TAG)).
                       add(INCLUDE_INSTALL_INSTRUCTIONS, toObligationState(obl, INCLUDE_INSTALL_INSTRUCTIONS_TAG)).
                       add(INCLUDE_NOTICE_FILE, toObligationState(obl, INCLUDE_NOTICE_FILE_TAG)).
                       add(INCLUDE_NOTICE_ABOUT_LICENSE, toObligationState(obl, INCLUDE_NOTICE_ABOUT_LICENSE_TAG)).
                       build());
  }

  private ObligationState toObligationState(JSONObject jo, String key) {
    System.out.println("reading value for key: " + key + " from: " + jo);
    String value = jo.getString(key);
    if (value==null) {
      return ObligationState.UNDEFINED;
    }
    if (value.equals("true")) {
      return ObligationState.TRUE;
    } else if (value.equals("false")) {
      return ObligationState.FALSE;
    }
    return ObligationState.UNDEFINED;
  }

  private String readJsonString(JSONObject json, String key, String defaultValue) {
    try {
      return json.getString(key);
    } catch (JSONException e) {
      return defaultValue;
    }
  }

  private int readJsonInt(JSONObject json, String key, int defaultValue) {
    try {
      return json.getInt(key);
    } catch (JSONException e) {
      return defaultValue;
    }
  }

}


  
