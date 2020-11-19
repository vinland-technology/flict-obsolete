package com.sandklef.compliance.exporter;

import com.sandklef.compliance.domain.IllegalLicenseExpression;
import com.sandklef.compliance.domain.LicenseExpressionException;
import com.sandklef.compliance.domain.PileComplianceReport;
import com.sandklef.compliance.domain.Report;

public class PileComplianceMDExporter implements PileComplianceReportExporter {
    @Override
    public String exportReport(PileComplianceReport report, OutputRange range) throws IllegalLicenseExpression, LicenseExpressionException {
        return null;
    }
}
