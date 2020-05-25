package com.sandklef.compliance.utils;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.exporter.ReportExporter;

import java.util.List;

public class TextComponentExporter implements ReportExporter {

    /*

            "concern report: " + report.concern());

            );
*/

    @Override
    public String exportReport(Report report) {
        return "Report from analysing component: \"" + report.component().name() + "\"\n" +
                exportLicenseViolations(report.violations()) +
                exportPolicyViolations(report.policyViolations()) + "\n" +
                exportConcerns(report.concerns()) + "\n" +
                exportConclusions(report.conclusions()) + "\n";
    }

    @Override
    public String exportLicenseViolations(List<LicenseObligationViolation> violations) {
        if (violations.size()==0) {
            return "Violations:  none\n" ;
        } else {
          StringBuilder sb = new StringBuilder();
          sb.append("Violations: \n");
          for (LicenseObligationViolation lov : violations) {
            sb.append(" * ");
            sb.append(lov.user().name());
            sb.append(" (");
            sb.append(LicenseArbiter.multipeLicensesInformation(lov.user()));
            sb.append(" | ");
            sb.append(lov.user().licenses());
            sb.append(" )\n");
            //            lovJson.put("obligation", lov.());
          }
          return sb.toString() ;
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
          StringBuilder sb = new StringBuilder();
          sb.append("Violations: \n");
          for (PolicyViolation pv : violations) {
            sb.append(" * ");
            sb.append(pv.component().name());
            sb.append(" (");
            sb.append(pv.license());
            sb.append(" )\n");
            //            lovJson.put("obligation", lov.());
          }
          return sb.toString() ;
        }
    }

    @Override
    public String exportComponent(Component c) {
        return null;
    }
}
