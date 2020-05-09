package com.sandklef.compliance.utils;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.exporter.ReportExporter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class TextComponentExporter implements ReportExporter {

    /*

            "concern report: " + report.concern());

            );
*/

    @Override
    public String exportReport(Report report) {
        return "Report from analysing component: \"" + report.component().name() + "\"\n" +
                exportLicenseViolations(report.violations()) + "\n" +
                exportPolicyViolations(report.policyViolations()) + "\n" +
                exportConcerns(report.concerns()) + "\n" +
                exportConclusions(report.conclusions()) + "\n";
    }

    @Override
    public String exportLicenseViolations(List<LicenseObligationViolation> violations) {
        if (violations.size()==0) {
            return "Violations:  none" ;
        } else {
            return "Violations:  " + violations;
        }
    }


    @Override
    public String exportConclusions(List<LicenseConclusion> conclusions) {
        if (conclusions.size()==0) {
            return "Conclusions:  none" ;
        } else {
            return "Conclusions:  " + conclusions;
        }
    }

    @Override
    public String exportConcerns(List<PolicyConcern> concerns) {
        if (concerns.size()==0) {
            return "Concerns:  none";
        } else {
            return "Concerns: " + concerns;
        }
    }

    @Override
    public String exportPolicyViolations(List<PolicyViolation> violations) {
        if (violations.size()==0) {
            return "Policy violations:  none";
        } else {
            return "Policy violations:  " + violations;
        }
    }

    @Override
    public String exportComponent(Component c) {
        return null;
    }
}
