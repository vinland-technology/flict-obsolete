package com.sandklef.compliance.exporter;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.utils.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;

public class MDExporter implements ReportExporter {

    private static final String HEADER_1 = "#";
    private static final String HEADER_2 = "##";
    private static final String HEADER_3 = "###";
    private static final String BOLD_START = "***";
    private static final String BOLD_END = "***";
    private static final String BULLET_ITEM = "*";
    private static final String HEADER_TAG = "#";
    private static final String LICENSE = "Concluded license";
    private static final String LICENSES = "Licenses";

    private static final String NEWLINE = "\n";
    private static final String LOG_TAG = MDExporter.class.getSimpleName();


    @Override
    public String exportReport(Report report) {
        StringBuilder sb = new StringBuilder();
        Component c = report.component();
        Set<String> licenseSet = c.allLicenses();

        sb.append("# Report ");
        sb.append(c.name());
        sb.append("\n");

        sb.append("# Summary");
        sb.append("\n\n");

        sb.append(BOLD_START + "Component: " + BOLD_END);
        sb.append(c.name());
        sb.append("\n\n");

        sb.append(BOLD_START + "Total license combinations: " + BOLD_END);
        sb.append(report.componentResults().size());
        sb.append("\n\n");

        sb.append(BOLD_START + "Compliant: " + BOLD_END);
        int pathCount = report.compliantCount();
        switch (pathCount) {
            case 0:
                sb.append("No");
                break;
            case 1:
                sb.append("Yes, 1 path");
                break;
            default:
                sb.append("Yes, " + pathCount + " paths");
                break;
        }
        sb.append("\n\n");

        sb.append(BOLD_START + "Policy: " + BOLD_END);
        sb.append(report.policy()==null?" none":" " + Session.getInstance().policyFile());
        sb.append("\n\n");

        sb.append(BOLD_START + "Numbers of dependency components: " + BOLD_END);
        sb.append(c.nrDependencies());
        sb.append("\n\n");

        sb.append(BOLD_START + "Numbers of licenses: " + BOLD_END);
        sb.append(licenseSet.size());
        sb.append("\n\n");

        // About check
        aboutReport(sb, report);

        // DETAILED REPORT
        sb.append("# Detailed report");
        sb.append("\n\n");

        sb.append(BOLD_START + "Dependency components: " + BOLD_END);
        sb.append("\n\n");
        for (Component d : c.allDependenciesImpl()) {
            sb.append(" * ");
            sb.append(d);
            sb.append("\n");
        }
        sb.append("\n\n");

        sb.append(BOLD_START + "Liceneses: " + BOLD_END);
        sb.append("\n\n");
        for (String s : licenseSet) {
            sb.append(" * ");
            sb.append(s);
            sb.append("\n");
        }
        sb.append("\n\n");

        Log.d(LOG_TAG, "Compliance lists:");
        Log.d(LOG_TAG, " * compliant:     " + report.compliantCount());
        Log.d(LOG_TAG, " * gray:          " + report.compliantGrayPaths().size());
        Log.d(LOG_TAG, " * denied:        " + report.compliantDeniedPaths().size());
        Log.d(LOG_TAG, " * non compliant: " + report.nonCompliantPaths().size());

        return sb.toString();
    }

    private void aboutReport(StringBuilder sb, Report report) {
        sb.append("# About license compliance check");
        sb.append("\n");
        sb.append(BOLD_START + "Tool: " + BOLD_END);
        sb.append(report.metaData().producer());
        sb.append(", version ");
        sb.append(report.metaData().version());
        sb.append("\n\n");
        sb.append(BOLD_START + "Check duration: " + BOLD_END);
        sb.append(report.metaData().duration());
        sb.append("\n\n");
        sb.append("* " + BOLD_START + "start: " + BOLD_END);
        sb.append(report.metaData().start());
        sb.append("\n\n");
        sb.append("* " + BOLD_START + "stop: " + BOLD_END);
        sb.append(report.metaData().stop());
        sb.append("\n\n");
        sb.append(BOLD_START + "User: " + BOLD_END);
        sb.append(System.getProperty("user.name"));
        sb.append("\n\n");
        sb.append(BOLD_START + "Host: " + BOLD_END);
        try {
            sb.append(InetAddress.getLocalHost().getHostName() + " (" + InetAddress.getLocalHost().getHostAddress() + ")");
        } catch (UnknownHostException e) {
            sb.append("unknown");
        }
        sb.append("\n\n");
        sb.append(BOLD_START + "Current directory: " + BOLD_END);
        sb.append(System.getProperty("user.dir"));
        sb.append("\n\n");
        Session session = Session.getInstance();

        sb.append(BOLD_START + "Component file: " + BOLD_END);
        sb.append(session.componentFile());
        sb.append("\n\n");

        sb.append(BOLD_START + "License dir: " + BOLD_END);
        sb.append(session.lLicenseDir());
        sb.append("\n\n");

        sb.append(BOLD_START + "Connector file: " + BOLD_END);
        sb.append(session.connectorFile());
        sb.append("\n\n");

        sb.append(BOLD_START + "Policy file: " + BOLD_END);
        sb.append(session.policyFile());
        sb.append("\n\n");
    }


}

