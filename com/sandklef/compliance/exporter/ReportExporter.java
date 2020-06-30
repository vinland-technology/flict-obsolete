// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.exporter;

import com.sandklef.compliance.domain.*;

import java.util.List;

public interface ReportExporter {
    String exportReport(Report report);
}
