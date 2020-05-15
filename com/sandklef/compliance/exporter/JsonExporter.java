// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.exporter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.utils.Version;
import com.sandklef.compliance.utils.LicenseArbiter;


import java.util.List;


public class JsonExporter implements ReportExporter {

    public static final String SOFTWARE_TAG = "software";
    public static final String VERSION_TAG = "version";
    public static final String META_TAG = "meta";
    public static final String CONCERNS_TAG = "concerns";
    public static final String CONCLUSION_TAG = "conclusions";
    public static final String VIOLATION_TAG = "violations";
    public static final String POLICY_VIOLATION_TAG = "policy-violations";
    public static final String REPORT_TAG = "report";

    private static final String LOG_TAG = JsonExporter.class.getSimpleName() ;
    
    @Override
    public String exportReport(Report report) {
/*        JSONObject top = new JSONObject();
        JSONObject meta = new JSONObject();
        meta.put(SOFTWARE_TAG, Version.POLICY_CHECKER_NAME);
        meta.put(VERSION_TAG, Version.POLICY_CHECKER_VERSION);
        top.put(META_TAG, meta);
        JSONObject data = new JSONObject();
        data.put(CONCERNS_TAG, exportConcernsJson(report.concerns()));
        data.put(CONCLUSION_TAG, exportConclusionsJson(report.conclusions()));
        data.put(VIOLATION_TAG, exportLicenseViolationsJson(report.violations()));
        data.put(POLICY_VIOLATION_TAG, exportPolicyViolationsJson(report.policyViolations()));
        top.put(REPORT_TAG, data);
        return top.toString();

 */
        return new Gson().toJson(report);
    }
/*
    public JSONObject exportLicenseViolationImpl(LicenseObligationViolation violation) {
        JSONObject obligationTop = new JSONObject();
        Component component = violation.user();
        obligationTop.put("component", component.name());

        // obligations
        JSONArray obligations = new JSONArray();

        for (LicenseObligationViolation.ObligationViolation o : violation.obligations()) {
            JSONObject obl = new JSONObject();
            obl.put("user_component", o.user.name() );
//            Log.d(LOG_TAG, "concluded_license: " + o.user.concludedLicense());
            obl.put("concluded_license",o.user.concludedLicense()==null?"null":o.user.concludedLicense());
            */

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
  /*          obligations.put(obl);
        }
        obligationTop.put("violated_obligations", obligations);
        return  obligationTop;
    }
*/
    @Override
    public String exportLicenseViolations(List<LicenseObligationViolation> violations) {
        return new Gson().toJson(violations);
//        return exportLicenseViolationsJson(violations);
    }

    public String exportLicenseViolationsJson(List<LicenseObligationViolation> violations) {
/*        JSONArray violationsArray = new JSONArray();
        for (LicenseObligationViolation lov : violations) {
            JSONObject lovJson = new JSONObject();
            lovJson.put("component", lov.user().name());
            lovJson.put("licenses_type", LicenseArbiter.multipeLicensesInformation(lov.user()));
//            lovJson.put("obligation", lov.());
            violationsArray.put(lovJson);
        }
        return violationsArray;

 */
        return new Gson().toJson(violations);
    }
/*
    private JSONObject exportConclusionImpl(LicenseConclusion conclusion) {
        JSONObject conclusionTop = new JSONObject();
        Component component = conclusion.component();
        conclusionTop.put("component", component.name());
        JSONArray conclusions = new JSONArray();
        for (LicenseConclusion lc : conclusion.licenseConclusions()) {
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
        conclusionTop.put(CONCLUSION_TAG, conclusions);
        return conclusionTop;
    }
*/
    @Override
    public String exportConclusions(List<LicenseConclusion> conclusions) {
        return new Gson().toJson(conclusions);
//        return exportConclusionsJson(conclusions).toString();
    }

/*
    public String exportConclusionsJson(List<LicenseConclusion> conclusions) {
        JSONArray conclusionArray = new JSONArray();
        for (LicenseConclusion lc : conclusions) {
            JSONObject lcJson = new JSONObject();
            lcJson.put("component", lc.component().name());
            lcJson.put("license", lc.license().spdx());
            lcJson.put("licenses_type", LicenseArbiter.multipeLicensesInformation(lc.component()));
            JSONArray licArr = new JSONArray();
            for (License lic : lc.component().licenses()) {
                licArr.put(lic.spdx());
            }
            lcJson.put("licenses", licArr);
            conclusionArray.put(lcJson);
        }
        System.out.println("   concl array: " + conclusionArray.toString());
        return conclusionArray;
    }
*/

    /*
    public JSONArray exportConcernsJson(List<PolicyConcern> concerns) {
        JSONArray concernsArrary = new JSONArray();
        for (PolicyConcern licConcern: concerns) {
            JSONObject concJson = new JSONObject();
            concJson.put("component", licConcern.component().name());
            concJson.put("license", licConcern.license().spdx());
            concJson.put("licenses_type", LicenseArbiter.multipeLicensesInformation(licConcern.component()));
            concernsArrary.put(concJson);
        }
        return concernsArrary;
    }
*/
    @Override
    public String exportConcerns(List<PolicyConcern> concerns) {
        return new Gson().toJson(concerns);
//        return exportConcernsJson(concerns).toString();
    }
/*
    public JSONArray exportPolicyViolationsJson(List<PolicyViolation> violations) {
        JSONArray violationArrary = new JSONArray();
        for (PolicyViolation pv: violations) {
            JSONObject concJson = new JSONObject();
            concJson.put("component", pv.component().name());
            concJson.put("license", pv.license().spdx());
            concJson.put("licenses_type", LicenseArbiter.multipeLicensesInformation(pv.component()));
            violationArrary.put(concJson);
        }
        return violationArrary;
    }
*/
    @Override
    public String exportPolicyViolations(List<PolicyViolation> violations) {
        return new Gson().toJson(violations);
//        return exportPolicyViolationsJson(violations).toString();
    }
    /*    @Override
        public String exportConcern(List<LicenseConcern> concerns) {
            JSONObject top = new JSONObject();
            top.put(SOFTWARE_TAG, Version.POLICY_CHECKER_NAME);
            top.put(VERSION_TAG, Version.POLICY_CHECKER_VERSION);
            top.put("concern", exportConcernImpl(concern));
            return top.toString();
        }
    */
    @Override
    public String exportComponent(Component c) {
        return new Gson().toJson(c);
    }
}
