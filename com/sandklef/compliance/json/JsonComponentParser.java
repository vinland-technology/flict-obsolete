// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.json;

import com.google.gson.*;
import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.utils.*;


import java.io.BufferedReader;
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

    public List<Component> readComponent(String fileName) throws IOException {
        if (fileName.equals("-")) {
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(System.in));
            StringBuilder sb = new StringBuilder();
            try {
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    sb.append(line);
//          System.out.println(" sb: " + sb.toString());
                }
            } catch (IOException e) {
                System.out.println(e);
                System.exit(1);
            }
            return readComponentString(sb.toString());
        }
        return readComponentString(new String(Files.readAllBytes(Paths.get(fileName))));
    }

    public List<Component> readComponentString(String json) {
        JsonObject jo = new JsonParser().parse(json).getAsJsonObject();
        //    Log.level(Log.DEBUG);
        Log.d(LOG_TAG, " n *** read co:   " + jo + " ****\n\n");
//    Log.d(LOG_TAG, " \n\n\n *** read meta: " + jo.get("meta").getAsJsonObject() + " ****\n\n");
//    Log.d(LOG_TAG, "  *** read comp: " + jo.get("component").getAsJsonObject() + " ****\n\n");
        JsonObject componentJson = jo.get("component").getAsJsonObject();

        return readComponentStringHelper(componentJson);

    }

    public List<Component> readComponentStringHelper(JsonObject componentJson) {
        Gson gson = new Gson();
        ComponentIntermediate componentIntermediate = gson.fromJson(componentJson, ComponentIntermediate.class);
        Log.d(LOG_TAG, "  *** read comp: " + componentIntermediate + " ****\n\n");

        List<Component> currents = componentIntermediate.export();

        JsonElement depElem = componentJson.get("dependencies");
        if (depElem == null) {
            return currents;
        }

        JsonArray depArray = depElem.getAsJsonArray();
        if (depArray == null || depArray.size() == 0) {
            return currents;
        }

     //   Log.level(Log.DEBUG);
      // for each component in currents
        for (Component cur : currents) {
            // for each dep in json
            for (JsonElement je : depArray) {
                Log.d(LOG_TAG, "   ===================  SUB READ: " + je.toString());
                List<Component> deps = readComponentStringHelper(je.getAsJsonObject());
                //   for each dep inflated from json
                for (Component dep : deps) {
                  Log.d(LOG_TAG, "   ===================  component loop: " + cur.name() + " add " + dep.name());
                    cur.addDependency(dep);
                }
            }
        }

        return currents;
    }

    private static class LicenseIntermediate {
        private String spdx;
    }

    private static class ComponentIntermediate {
        private String name;
        private String license;
        private List<License> dependencies;

        @Override
        public String toString() {
            return "ComponentIntermediate{" +
                    "name='" + name + '\'' +
                    ", license='" + license + '\'' +
                    ", dependencies=" + dependencies +
                    '}';
        }


        public List<Component> export() {
            if (license.contains("|") ||
                    license.contains("&")) {
                boolean dualLicensed = license.contains("|");
                Log.d(LOG_TAG, name + " license string: " + license.trim() );
                List<License> licenses = licensesToList(license.trim());
                List<Component> components = new ArrayList<>();

                if (dualLicensed) {
                    components.add(new Component(name, licenses, null));
                    return components;
                } else {
                    int i = 0;
                    for (License l : licenses) {
                        Log.d(LOG_TAG, name + " virtual : " + l );
                        components.add(new Component(name + "_virtual_" + i, l, null));
                        i++;
                    }
                    return components;
                }
            } else {
                Log.d(LOG_TAG, name + " LicenseName: " + license + " DOES NOT contains PIPE");
                License licenseReturn = LicenseStore.getInstance().license(license.trim());
//      System.out.println("License: " + license);
                Log.d(LOG_TAG, name + " will get license: " + license + "   no PIPE");
                List<Component> components = new ArrayList<>();
                components.add(new Component(name, licenseReturn, null));
                return components;
            }
        }

    }


/*  private Component readComponentHelper(JSONObject jo) throws IOException {
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
*/

    private static List<License> licensesToList(String licensefield) {
        List<String> licenseList;
        List<License> licenses = new ArrayList<>();

        if (licensefield.contains("|")) {
            licenseList = Arrays.asList(Pattern.compile("\\|").split(licensefield));
        } else {
            licenseList = Arrays.asList(Pattern.compile("&").split(licensefield));
        }

        for (String l : licenseList) {
            if (l.equals("")) { continue; }
            License lic = LicenseStore.getInstance().license(l.trim());
            Log.d(LOG_TAG, " * translate license: \"" + l.trim() + "\" => \"" + lic + "\"");
            Log.d(LOG_TAG, " * translate license: \"" + l.trim() + "\" => \"" + lic.spdx() + "\"");
            licenses.add(lic);
            Log.d(LOG_TAG, " * translate license: \"" + licenses);
        }
        return licenses;
    }

/*
  public MetaData readMetaDate(String fileName) throws IOException {
    return readMetaData(new JSONObject(new String(Files.readAllBytes(Paths.get(fileName)))));
  }
  public MetaData readMetaData(JSONObject jo) {
    return new MetaData(jo.getString("software"),
            jo.getString("version"));
  }
*/

}


  
