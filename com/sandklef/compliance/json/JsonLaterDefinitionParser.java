// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.json;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.utils.LicenseStore;
import com.sandklef.compliance.utils.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonLaterDefinitionParser {

    public static final String SW_TAG = "software";
    public static final String SW_VERSION_TAG = "version";

    public static final String LOG_TAG = JsonLaterDefinitionParser.class.getSimpleName();

    public Map<String, List<License>> readLaterDefinition(String fileName) throws IOException, LicenseExpressionException {
        Log.d(LOG_TAG, " reading from file: " + fileName);
        Log.d(LOG_TAG, "content: " + new String(Files.readAllBytes(Paths.get(fileName))));
        return readLaterDefinitionString(new String(Files.readAllBytes(Paths.get(fileName))));
    }

    private List<License> convert(List<String> stringLicenses) throws LicenseExpressionException {
        List<License> licenses = new ArrayList<>();
        if (stringLicenses != null) {
            for (String s : stringLicenses) {
                licenses.add(LicenseStore.getInstance().license(s));
            }
        }
        return licenses;
    }

    private Map<String, List<License>> readLaterDefinitionString(String str) throws LicenseExpressionException {
        JsonObject jo = new JsonParser().parse(str).getAsJsonObject();
        JsonArray laterJson = jo.get("later-definitions").getAsJsonArray();
        Gson gson = new Gson();
        List<LaterDefinitionIntermediate> laterGson = gson.fromJson(laterJson, new TypeToken<List<LaterDefinitionIntermediate>>() {
        }.getType());

      //  System.out.println("  * " + laterGson);
        Map<String, List<License>> laters = new HashMap<>();
        for (LaterDefinitionIntermediate ld : laterGson) {
            laters.put(ld.spdx, convert(ld.later));
        }
        return laters;
    }

    static class LaterDefinitionIntermediate {
        private String spdx;
        private List<String> later;

        @Override
        public String toString() {
            return "LaterDefinitionIntermediate{" +
                    "spdx='" + spdx + '\'' +
                    ", licenses=" + later +
                    '}';
        }
    }


}


  
