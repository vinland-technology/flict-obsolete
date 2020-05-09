// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.exporter;

import com.sandklef.compliance.domain.*;

import java.util.List;

public interface ReportExporter {
    public String exportReport(Report report);
    public String exportLicenseViolations(List<LicenseObligationViolation> violations);
    public String exportConclusions(List<LicenseConclusion> conclusions);
    public String exportConcerns(List<PolicyConcern> concerns);
    public String exportPolicyViolations(List<PolicyViolation> violations);
    public String exportComponent(Component c);
}
