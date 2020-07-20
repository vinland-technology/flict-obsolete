// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sandklef.compliance.domain.MetaData;

public class JsonMetaInfoParser {

    public static final String SW_TAG = "software";
    public static final String SW_VERSION_TAG = "version";

    public static String emptyIfNull(JsonElement je) {
        if (je==null) {
            return "";
        }
        return je.getAsString();
    }

    public static MetaData readMetaData(JsonObject jo) {
        if (jo==null) {
            return new MetaData("", "");
        }
        MetaData md = new MetaData(emptyIfNull(jo.get(SW_TAG)),
                emptyIfNull(jo.get(SW_VERSION_TAG)));
        return md;
    }

}
