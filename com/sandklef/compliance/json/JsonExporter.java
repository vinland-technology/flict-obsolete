// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.json;

import com.sandklef.compliance.cli.LicenseChecker;
import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.exporter.ReportExporter;
import com.sandklef.compliance.utils.Log;
import com.sandklef.compliance.utils.Version;
import org.json.JSONArray;
import org.json.JSONObject;
import sun.nio.cs.ext.COMPOUND_TEXT;


public class JsonExporter implements ReportExporter {

    private static final String LOG_TAG = JsonExporter.class.getSimpleName() ;

    @Override
    public String exportReport(Report report) {
        JSONObject top = new JSONObject();
        top.put("software", Version.POLICY_CHECKER_NAME);
        top.put("version", Version.POLICY_CHECKER_VERSION);
        JSONObject data = new JSONObject();
        data.put("concerns", exportConcernImpl(report.concern()));
        data.put("conclusion", exportConclusionImpl(report.conclusion()));
        data.put("obligation", exportLicenseViolationImpl(report.violation()));
        top.put("report", data);
        return top.toString();
    }

    public JSONObject exportLicenseViolationImpl(LicenseViolation violation) {
        JSONObject obligationTop = new JSONObject();
        Component component = violation.component();
        obligationTop.put("component", component.name());

        // obligations
        JSONArray obligations = new JSONArray();

        for (LicenseViolation.ObligationViolation o : violation.obligations()) {
            JSONObject obl = new JSONObject();
            obl.put("user_component", o.user.name() );
//            Log.d(LOG_TAG, "concluded_license: " + o.user.concludedLicense());
            obl.put("concluded_license",o.user.concludedLicense()==null?"null":o.user.concludedLicense());
/*            JSONArray deps = new JSONArray();
            for (Component c : o.user.dependencies()) {
                JSONObject cJson = new JSONObject();
                cJson.put("name", c.name());
                JSONArray lic = new JSONArray();
                for (License l : c.licenses()) {
                    lic.put(l.spdxTag());
                }
                cJson.put("licenses", lic);
                deps.put(cJson);
            }

            obl.put("dependencies", deps);
 */
            obligations.put(obl);
        }
        obligationTop.put("violated_obligations", obligations);
        return  obligationTop;
    }

    @Override
    public String exportLicenseViolation(LicenseViolation violation) {
        JSONObject top = new JSONObject();
        top.put("software", Version.POLICY_CHECKER_NAME);
        top.put("version", Version.POLICY_CHECKER_VERSION);

        top.put("obligation", exportLicenseViolationImpl(violation));
        return top.toString();
    }

    private JSONObject exportConclusionImpl(Conclusion conclusion) {
        JSONObject conclusionTop = new JSONObject();
        Component component = conclusion.component();
        conclusionTop.put("component", component.name());
        JSONArray conclusions = new JSONArray();
        for (Conclusion.LicenseConclusion lc : conclusion.licenseConclusions()) {
            JSONObject lcJson = new JSONObject();
            lcJson.put("component", lc.component().name() );
            lcJson.put("license", lc.license().spdxTag());
            JSONArray licArr = new JSONArray();
            for (License lic : lc.component().licenses()) {
                licArr.put(lic.spdxTag());
            }
            lcJson.put("licenses", licArr);
//            Log.d(LOG_TAG, "  ------------------------------------- licenses: " + lc.component().licenses());
            conclusions.put(lcJson);

        }
        conclusionTop.put("conclusions", conclusions);
        return conclusionTop;
    }

    @Override
    public String exportConclusion(Conclusion conclusion) {
        JSONObject top = new JSONObject();
        top.put("software", Version.POLICY_CHECKER_NAME);
        top.put("version", Version.POLICY_CHECKER_VERSION);
        top.put("conclusion", exportConclusionImpl(conclusion));
        return top.toString();
    }

    public JSONObject exportConcernImpl(Concern concern) {
        JSONObject concernTop = new JSONObject();
        concernTop.put("component", concern.component().name());
        JSONArray concerns = new JSONArray();
        for (Concern.LicenseConcern licConcern: concern.licenseConcerns()) {
            JSONObject concJson = new JSONObject();
            concJson.put("component", licConcern.component().name());
            concJson.put("license", licConcern.license().spdxTag());
            concJson.put("license_type", licConcern.listType());
            concerns.put(concJson);
        }
        concernTop.put("concerns", concerns);
        return concernTop;
    }

    @Override
    public String exportConcern(Concern concern) {
        JSONObject top = new JSONObject();
        top.put("software", Version.POLICY_CHECKER_NAME);
        top.put("version", Version.POLICY_CHECKER_VERSION);
        top.put("concern", exportConcernImpl(concern));
        return top.toString();
    }
}