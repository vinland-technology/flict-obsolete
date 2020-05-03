package com.sandklef.compliance.utils;

import com.sandklef.compliance.domain.Concern;
import com.sandklef.compliance.domain.Conclusion;
import com.sandklef.compliance.domain.LicenseViolation;
import com.sandklef.compliance.domain.Report;
import com.sandklef.compliance.exporter.ReportExporter;

public class TextComponentExporter implements ReportExporter {

    /*

            "concern report: " + report.concern());

            );
*/

    @Override
    public String exportReport(Report report) {
        return "Report from analysing component: \"" + report.conclusion.component().name() + "\"\n" +
                exportLicenseViolation(report.violation()) +
                exportConcern(report.concern()) +
                exportConclusion(report.conclusion());
    }

    @Override
    public String exportLicenseViolation(LicenseViolation violation) {
        return "violation report:  " + violation;
    }

    @Override
    public String exportConclusion(Conclusion conclusion) {
        return "conclusion report: " + conclusion;
    }

    @Override
    public String exportConcern(Concern concern) {
        return "concern report: " + concern;
    }
}
