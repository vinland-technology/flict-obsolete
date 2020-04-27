package com.sandklef.compliance.json;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.utils.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonParser {

  public static final String COMPONENT_TAG = "component";
  public static final String DEPENDENCIES_TAG = "dependencies";
  public static final String LICENSE_TAG = "license";
  public static final String NAME_TAG = "name";

  public static final String LOG_TAG = JsonParser.class.getSimpleName();

  private JSONObject base;

  public JsonParser(String fileName) throws IOException {
    this(new JSONObject(new String(Files.readAllBytes(Paths.get(fileName)))));
  }

  public JsonParser(JSONObject jo) {
    base = jo;
  }
  
  public Component readComponent() {
    return readComponent(base);
  }
  
  private Component readComponent(JSONObject jo) {
    String name = readJsonString(jo, NAME_TAG, "");

    String licenseName = readJsonString(jo, LICENSE_TAG, "");
    System.out.println("License: " + licenseName);
    License license = LicenseStore.getInstance().license(licenseName);
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
  

  private String readJsonString(JSONObject json, String key, String defaultValue) {
    try {
      return json.getString(key);
    } catch (JSONException e) {
      return defaultValue;
    }
  }

}


  
