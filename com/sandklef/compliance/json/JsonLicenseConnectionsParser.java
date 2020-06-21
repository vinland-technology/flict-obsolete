package com.sandklef.compliance.json;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.sandklef.compliance.domain.License;
import com.sandklef.compliance.domain.LicenseConnector;
import com.sandklef.compliance.domain.LicenseGroup;
import com.sandklef.compliance.domain.LicensePolicy;
import com.sandklef.compliance.utils.LicenseStore;
import com.sandklef.compliance.utils.Log;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonLicenseConnectionsParser {

    public static final String LOG_TAG = JsonLicenseConnectionsParser.class.getSimpleName();

    public static final String SW_TAG = "software";
    public static final String SW_VERSION_TAG = "version";

    public static Map<String, LicenseConnector>  readLicenseConnection(String fileName) throws IOException {
        //Log.level(Log.DEBUG);
        return readLicenseConnectionString(new String(Files.readAllBytes(Paths.get(fileName))));
    }


    public static Map<String, LicenseConnector> readLicenseConnectionString(String json) {
        Map<String, LicenseConnector> connectorMap = new HashMap<>();

        JsonObject jo = new JsonParser().parse(json).getAsJsonObject();
        //        Log.level(Log.DEBUG);
        Log.d(LOG_TAG, " \n\n\n *** read jo:   " + jo + " ****\n\n");
        Log.d(LOG_TAG, " \n\n\n *** read meta: " + jo.get("meta").getAsJsonObject() + " ****\n\n");
        Log.d(LOG_TAG, " \n\n\n *** read conn: " + jo.get("connections").getAsJsonArray() + " ****\n\n");
        JsonArray connectorsJson = jo.get("connections").getAsJsonArray();
        Gson gson = new Gson();
        List<LicenseConnectorIntermediate> connectors = gson.fromJson(connectorsJson, new TypeToken<List<LicenseConnectorIntermediate>>() {}.getType());

        Log.d(LOG_TAG, " \n\n\n *** CONNECTORS: " + connectors + " ****\n\n");

        // connections - collect spdx and create connectors
        for (LicenseConnectorIntermediate lci : connectors) {
            Log.d(LOG_TAG, " lci:: " + lci);
            if (lci.spdx!=null) {
                Log.d(LOG_TAG, " lc: license  " + lci.spdx + " >>>> " + lci.used_by + " \n");
                LicenseConnector lc = new LicenseConnector(new License(lci.spdx));
                connectorMap.put(lci.spdx, lc);
            } else if (lci.group_name!=null) {
                Log.d(LOG_TAG, " lci: licensegroup            >>>> " + lci.group_name + " \n");
                LicenseConnector lc = new LicenseConnector(new LicenseGroup(lci.group_name));
                connectorMap.put(lci.group_name, lc);
            }
        }

        // connections - collect name and add connections
        Log.d(LOG_TAG, " lcs: " + connectors);
        for (LicenseConnectorIntermediate lci : connectors) {
            Log.d(LOG_TAG, " lc: " + lci);
            for (String used_by : lci.used_by) {
                if (lci.spdx!=null) {
                    LicenseConnector lc = connectorMap.get(lci.spdx);
                    LicenseConnector lcToAdd = connectorMap.get(used_by);
                    Log.d(LOG_TAG, "    add to: " + lc + "  " + lcToAdd + " \n   map: " + connectorMap);
                    lc.canBeUsedBy(lcToAdd);
                } else {
                    LicenseConnector lc = connectorMap.get(lci.group_name);
                    LicenseConnector lcToAdd = connectorMap.get(used_by);
                    Log.d(LOG_TAG, "    add to: " + lc + "  " + lcToAdd + "   group_name: " + lci.group_name +  "\n");
                    lc.canBeUsedBy(lcToAdd);
                }
            }
        }
        Log.d(LOG_TAG, " return map: " + connectorMap);
        return connectorMap;
    }

    private static class LicenseConnectorIntermediate {
        private String spdx;
        private String group_name;
        private List<String> used_by;

        @Override
        public String toString() {
            return "LicenseConnectorIntermediate{" +
                    "spdx='" + spdx + '\'' +
                    "group_name='" + group_name + '\'' +
                    ", used_by=" + used_by +
                    '}';
        }
    }
}
