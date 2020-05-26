package com.sandklef.compliance.exporter;

import com.sandklef.compliance.domain.*;

import java.util.List;

public class MDExporter implements ReportExporter {

    private static final String HEADER_1 = "#";
    private static final String HEADER_2 = "##";
    private static final String HEADER_3 = "###";
    private static final String BULLET_ITEM = "*";
    private static final String HEADER_TAG = "#";
    private static final String LICENSE = "Concluded license";
    private static final String LICENSES = "Licenses";

    private static final String NEWLINE = "\n";


    @Override
    public String exportReport(Report report) {
        return "";
    }
}

