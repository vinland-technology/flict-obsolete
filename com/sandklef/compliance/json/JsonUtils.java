// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.json;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {

    public static String readJsonString(JSONObject json, String key, String defaultValue) {
        try {
            return json.getString(key);
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    public static int readJsonInt(JSONObject json, String key, int defaultValue) {
        try {
            return json.getInt(key);
        } catch (JSONException e) {
            return defaultValue;
        }
    }
}
