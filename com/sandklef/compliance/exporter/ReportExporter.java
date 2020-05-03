// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.exporter;

import com.sandklef.compliance.domain.Concern;
import com.sandklef.compliance.domain.Conclusion;
import com.sandklef.compliance.domain.LicenseViolation;
import com.sandklef.compliance.domain.Report;

public interface ReportExporter {
    public String exportReport(Report report);
    public String exportLicenseViolation(LicenseViolation violation);
    public String exportConclusion(Conclusion conclusion);
    public String exportConcern(Concern concern);
}
