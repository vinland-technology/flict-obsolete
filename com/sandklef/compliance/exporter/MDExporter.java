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

    private String header(int level) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<level; i++) {
            sb.append(HEADER_TAG);
        }
        sb.append(" ");
        return sb.toString();
    }

    private String exportComponent(StringBuilder sb, Component c, int level) {
        level++;
        sb.append(header(level));
        sb.append(c.name());
        sb.append(NEWLINE);
        sb.append(NEWLINE);

//        sb.append(header(level+1));
        // CONCLUDED LICENSE
        sb.append(LICENSE);
        sb.append(": ");
        License concludedLicense = c.concludedLicense();
        if (concludedLicense!=null) {
            sb.append(concludedLicense.spdx());
        } else {
            sb.append("not conluded");
        }
        sb.append(NEWLINE);
        sb.append(NEWLINE);

        // LICENSES
        sb.append(LICENSES);
        sb.append(": ");
        sb.append(c.licenses());
        sb.append(NEWLINE);
        sb.append(NEWLINE);

        if (c.dependencies().size()>0) {
            sb.append("Dependencies: ");
            sb.append(NEWLINE);
            sb.append(NEWLINE);
            for (Component d : c.dependencies()) {
//                exportComponent(sb, d, level+1 );
                sb.append(BULLET_ITEM);
                sb.append(" ");
                sb.append(d);
                sb.append(NEWLINE);
                sb.append(NEWLINE);
            }
            sb.append(NEWLINE);
        }
        return sb.toString();
    }

    @Override
    public String exportReport(Report report) {
        StringBuilder sb = new StringBuilder();
        // title
        sb.append(HEADER_1);
        sb.append("License and policy report for ");
        sb.append(report.component().name());
        sb.append(NEWLINE);
        sb.append(NEWLINE);

        // CONCLUDED LICENSE
        sb.append(LICENSE);
        sb.append(": ");
        License concludedLicense = report.component().concludedLicense();
        if (concludedLicense!=null) {
            sb.append(concludedLicense.spdx());
        } else {
            sb.append("not conluded");
        }
        sb.append(NEWLINE);
        sb.append(NEWLINE);

        // LICENSES
        sb.append(LICENSES);
        sb.append(":");
        sb.append(report.component().licenses());
        sb.append(NEWLINE);
        sb.append(NEWLINE);

        // Component
        sb.append(HEADER_1);
        sb.append("Dependencies");
        sb.append(NEWLINE);
        for (Component d : report.component().dependencies()) {
            exportComponent(sb, d, 1);
        }

        return sb.toString();
    }

    @Override
    public String exportLicenseViolations(List<LicenseObligationViolation> violations) {
        return null;
    }

    @Override
    public String exportConclusions(List<LicenseConclusion> conclusions) {
        return null;
    }

    @Override
    public String exportConcerns(List<PolicyConcern> concerns) {
        return null;
    }

    @Override
    public String exportPolicyViolations(List<PolicyViolation> violations) {
        return null;
    }

    @Override
    public String exportComponent(Component c) {
        return null;
    }
}
