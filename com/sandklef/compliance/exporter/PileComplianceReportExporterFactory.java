// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.exporter;

public class PileComplianceReportExporterFactory {

   private static PileComplianceReportExporterFactory instance;

    public static PileComplianceReportExporterFactory getInstance() {
        if (instance==null) {
            instance = new PileComplianceReportExporterFactory();
        }
        return instance;
    }

    public PileComplianceReportExporter exporter(PileComplianceReportExporter.OutputFormat format, PileComplianceReportExporter.OutputRange range) {
        if (format== PileComplianceReportExporter.OutputFormat.JSON) {
            return new PileComplianceJsonExporter();
        } else if (format== PileComplianceReportExporter.OutputFormat.MARKDOWN) {
            return new PileComplianceMDExporter();
        }
        // default to TEXT (for now)
        return new PileComplianceTextReportExporter();
    }

}
