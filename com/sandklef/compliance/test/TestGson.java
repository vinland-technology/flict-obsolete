// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.test;

import com.google.gson.*;
import com.sandklef.compliance.domain.License;
import com.sandklef.compliance.domain.LicenseExpressionException;
import com.sandklef.compliance.domain.LicensePolicy;
import com.sandklef.compliance.json.JsonLicenseParser;
import com.sandklef.compliance.utils.LicenseStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestGson {

    private static class LicensePolicyIntermediate {
        private String name;
        private List<String> whitelist;
        private List<String> graylist;
        private List<String> blacklist;

        private List<License> convert(List<String> stringLicenses) throws LicenseExpressionException {
            List<License> licenses = new ArrayList<>();
            for (String s : stringLicenses) {
                licenses.add(LicenseStore.getInstance().license(s));
//                licenses.add(new License("dummy", s));
            }
            return licenses;
        }

        public LicensePolicy export() throws LicenseExpressionException {
             return new LicensePolicy(name,
                     convert(whitelist),
                     convert(graylist),
                     convert(blacklist));
        }
    }

    public static void main(String[] args) throws IOException, LicenseExpressionException {

        String license = "{ \"meta\": { \"software\":\"License Policy Checker\", \"version\":\"0.1\" }, \"license\": { \"name\":\"GNU General Public License v2.0 or later\", \"spdx\":\"GPL-2.0-or-later\"} }";
        String license2 = "{ \"name\":\"GNU General Public License v2.0 or later\", \"spdx\":\"GPL-2.0-or-later\" }";
        String license3 = "{" +
                "    \"meta\": {" +
                "        \"software\":\"License Policy Checker\"," +
                "        \"version\":\"0.1\"" +
                "    }," +
                "    \"license\": {" +
                "        \"name\":\"GNU General Public License v2.0 or later\"," +
                "        \"spdx\":\"GPL-2.0-or-later aasdasd\", " +
                "        \"obligations\": {" +
                "            \"linking_copylefted\":\"true\"," +
                "            \"modification_allowed\":\"true\"," +
                "            \"modification_copylefted\":\"true\"," +
                "            \"sublicensing_allowed\":\"false\"," +
                "            \"distribution_allowed\":\"true\"," +
                "            \"distribution_copylefted\":\"true\"," +
                "            \"disclose_source\":\"true\"," +
                "            \"include_original\":\"true\"," +
                "            \"state_changes\":\"true\"," +
                "            \"include_copyright\":\"true\"," +
                "            \"include_license\":\"true\"," +
                "            \"include_install_instructions\":\"false\"," +
                "            \"include_notice_file\":\"false\"," +
                "            \"include_notice_about_license\":\"false\"" +
                "        }" +
                "    }" +
                "}";

        String policy = "{" +
                "  \"meta\": {" +
                "    \"software\": \"License Policy Checker\"," +
                "    \"version\": \"0.1\"" +
                "  }," +
                "  \"policy\": {" +
                "    \"name\": \"Test\"," +
                "    \"whitelist\": [" +
                "      \"Apache-2.0\"," +
                "      \"LGPL-2.1-only\"" +
                "    ]," +
                "    \"graylist\": [" +
                "      \"GPL-2.0-only\"" +
                "    ]," +
                "    \"blacklist\": [" +
                "      \"GPL-3.0-only\"," +
                "      \"GPL-3.0-or-later\"" +
                "    ]" +
                "  }" +
                "}";

        Gson gson = new Gson();

        System.out.println(license3);
        JsonObject root = new JsonParser().parse(license3).getAsJsonObject();
        JsonObject licenseJson = root.get("license").getAsJsonObject();
        System.out.println(licenseJson);
//        System.exit(1);
        License l2 = gson.fromJson(licenseJson, License.class);
        System.out.println("  l2: " + l2);


// policy
        LicenseStore.getInstance().addLicenses(new JsonLicenseParser().readLicenseDir("licenses/json/"));

        System.out.println(policy);
        JsonObject policyRoot = new JsonParser().parse(policy).getAsJsonObject();
        JsonObject polocyJson = policyRoot.get("policy").getAsJsonObject();
        System.out.println(polocyJson);
        LicensePolicyIntermediate policyGson = gson.fromJson(polocyJson, LicensePolicyIntermediate.class);
        System.out.println(policyGson);

        System.out.println(policyGson.export());

    }
}

