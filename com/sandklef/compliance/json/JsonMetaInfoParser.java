package com.sandklef.compliance.json;

import com.google.gson.JsonObject;
import com.sandklef.compliance.domain.MetaData;

public class JsonMetaInfoParser {

    public static final String SW_TAG = "software";
    public static final String SW_VERSION_TAG = "version";

    public static MetaData readMetaData(JsonObject jo) {
        MetaData md = new MetaData(jo.get(SW_TAG).getAsString(), jo.get(SW_VERSION_TAG).getAsString());
        return md;
    }

}
