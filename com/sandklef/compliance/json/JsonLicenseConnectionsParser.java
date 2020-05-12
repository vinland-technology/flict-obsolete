package com.sandklef.compliance.json;

import com.sandklef.compliance.domain.License;
import com.sandklef.compliance.domain.LicenseConnector;
import com.sandklef.compliance.utils.LicenseStore;
import com.sandklef.compliance.utils.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class JsonLicenseConnectionsParser {

    public static final String LOG_TAG = JsonLicenseConnectionsParser.class.getSimpleName();

    public static final String SW_TAG = "software";
    public static final String SW_VERSION_TAG = "version";

    public static Map<String, LicenseConnector>  readLicenseConnection(String fileName) throws IOException {
        //Log.level(Log.DEBUG);
        return readLicenseConnection(new JSONObject(new String(Files.readAllBytes(Paths.get(fileName)))));
    }


    public static Map<String, LicenseConnector> readLicenseConnection(JSONObject jo) {
        Map<String, LicenseConnector> connectorMap = new HashMap<>();

        // Meta information
        String swName = JsonUtils.readJsonString(jo, SW_TAG, "");
        int version = JsonUtils.readJsonInt(jo, SW_VERSION_TAG, 0);

        Log.d(LOG_TAG, swName + " " + version);
        // connections - collect name and create connections
        JSONArray connectionArrays = jo.getJSONArray("connections");
        Log.d(LOG_TAG, "connections: " + connectionArrays);
        for (int i = 0; i < connectionArrays.length(); i++) {
            // connection
            JSONObject connection = connectionArrays.getJSONObject(i);
            String spdx = connection.getString("spdx");
            Log.d(LOG_TAG, "connectorMap, adding " + spdx);

            LicenseConnector lc = new LicenseConnector(LicenseStore.getInstance().license(spdx));
            connectorMap.put(spdx, lc);
            Log.d(LOG_TAG, "connectorMap, adding " + lc.license().spdxTag());
        }
        Log.d(LOG_TAG, "connectorMap: " + connectorMap);


        // connections - collect name and create connections
        connectionArrays = jo.getJSONArray("connections");
        for (int i = 0; i < connectionArrays.length(); i++) {
            // connection
            JSONObject connection = connectionArrays.getJSONObject(i);
            String spdx = connection.getString("spdx");

            LicenseConnector lc = connectorMap.get(spdx);
            Log.d(LOG_TAG, " * connector: " + lc.license().spdxTag());
            // used_by[]
            JSONArray usedByArray = connection.getJSONArray("used_by");
            for (int j = 0; j < usedByArray.length(); j++) {
                // used_by
                String license = usedByArray.getString(j);
                LicenseConnector subLc =  connectorMap.get(license);
                Log.d(LOG_TAG, "   * connector: " + subLc);
                lc.canBeUsedBy(subLc);
            }
        }

        Log.d(LOG_TAG,"meta: " + swName + "   " + version);

        return connectorMap;
    }

}
