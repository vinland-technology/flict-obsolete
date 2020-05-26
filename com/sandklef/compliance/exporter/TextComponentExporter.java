package com.sandklef.compliance.exporter;

import com.sandklef.compliance.domain.*;

public class TextComponentExporter implements ReportExporter {


    @Override
    public String exportReport(Report report) {
        return "Report from analysing component: \n";
    }

}
