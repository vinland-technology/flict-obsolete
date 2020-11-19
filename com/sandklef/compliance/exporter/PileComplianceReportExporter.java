// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.exporter;

import com.sandklef.compliance.domain.IllegalLicenseExpression;
import com.sandklef.compliance.domain.LicenseExpressionException;
import com.sandklef.compliance.domain.PileComplianceReport;
import com.sandklef.compliance.domain.Report;

public interface PileComplianceReportExporter {

    public enum OutputFormat {
        SHORT,
        JSON,
        MARKDOWN
    }

    public enum OutputRange {
        SHORT,
        LONG
    };


    String exportReport(PileComplianceReport report, OutputRange range) throws IllegalLicenseExpression, LicenseExpressionException;
}
