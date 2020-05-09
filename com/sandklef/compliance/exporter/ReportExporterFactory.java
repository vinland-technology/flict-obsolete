// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.exporter;

import com.sandklef.compliance.utils.TextComponentExporter;

public class ReportExporterFactory {


    public static enum OutputFormat {
        TEXT,
        JSON,
        MARKDOWN;
    }


    private static ReportExporterFactory instance;

    public static ReportExporterFactory getInstance() {
        if (instance==null) {
            instance = new ReportExporterFactory();
        }
        return instance;
    }

    public ReportExporter exporter(OutputFormat format) {
        if (format==OutputFormat.JSON) {
            return new JsonExporter();
        } else if (format==OutputFormat.MARKDOWN) {
            return new MDExporter();
        }
        // default to TEXT (for now)
        return new TextComponentExporter();
    }

}
