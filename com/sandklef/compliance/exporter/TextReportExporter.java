package com.sandklef.compliance.exporter;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.utils.LicenseArbiter;
import com.sandklef.compliance.utils.LicenseUtils;
import com.sandklef.compliance.utils.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

public class TextReportExporter implements ReportExporter {

  private Report report;
    private Component c;
    private Set<String> licenseSet;

    private void init(Report report) {
        this.report = report;
        if (c==null) {
            c = report.component();
        }
        if (licenseSet==null) {
            licenseSet = c.allLicenses();
        }
    }

    private static String formatColumn = "%-40s %s";

    private String formatColumn(String column, String content) {
        return String.format(formatColumn, column, content);
    }
    private String formatColumn(String column, int content) {
        return String.format(formatColumn, column, String.valueOf(content));
    }

    @Override
    public String exportReport(Report report) {
        init(report);
        StringBuilder sb = new StringBuilder();

        sb.append(formatColumn("Component:", c.name()));
        sb.append("\n");

        summaryReport(sb);

        // DETAILED REPORT
//        detailedReport(sb);

        return sb.toString();
    }

    private String licenseTypeComment(String license) throws LicenseExpressionException {
        ListType color = LicenseUtils.licenseColor(license,report.policy());
        switch (color) {
            case ALLOWED_LIST:
                return "";
            case GRAY_LIST:
                return " (gray listed)";
            case DENIED_LIST:
                return " (denied)";
        }
        return "";
    }

    private String beautifyLicense(String license) {
        return license.replace(",", " & ").replace("[", "").replace("]", "");
    }

    private String compliantResult(Report report) {
        if (report.compliantCount() == 0) {
            return "No";
        }
        if (report.policy()!=null) {
            if (report.complianAllowedtPaths().size() == report.compliantPaths().size()) {
                return "Yes";
            } else if (report.compliantGrayPaths().size() == report.compliantPaths().size()) {
                return "Yes, with only gray licenses";
            } else {
                return "Yes, with some gray licenses";
            }
        }
        return "Yes";
    }

    private void summaryReport(StringBuilder sb) {

        sb.append(formatColumn("Compliant: ", compliantResult(report)));
        sb.append("\n");

        sb.append(formatColumn("Total license combinations: ", report.componentResults().size()));
        sb.append("\n");

        sb.append(formatColumn(" * compliant: ", report.compliantCount()));
        sb.append("\n");

        sb.append(formatColumn(" * non compliant: ", report.nonCompliantPaths().size()));
        sb.append("\n");

        if (report.policy()!=null) {
            sb.append(formatColumn("Policy based combinations: ", report.componentResults().size()));
            sb.append("\n");

            sb.append(formatColumn(" * all: ", report.compliantCount()));
            sb.append("\n");

            sb.append(formatColumn(" * gray: ", report.compliantGrayPaths().size()));
            sb.append("\n");

            sb.append(formatColumn(" * denied: ", report.compliantDeniedPaths().size()));
            sb.append("\n");

            sb.append(formatColumn("Policy: ", Session.getInstance().policyFile()));
            sb.append("\n");
        } else {
            sb.append(formatColumn("Policy: ", "none"));
            sb.append("\n");
        }

        sb.append(formatColumn("Numbers of dependency components: ", c.nrDependencies()));
        sb.append("\n");
        for (Component d : c.allDependenciesImpl()) {
            sb.append(" * ");
            sb.append(d);
            sb.append("\n");
        }

        sb.append(formatColumn("Numbers of licenses: ", licenseSet.size()));
        sb.append("\n");
        for (String s : licenseSet) {
            sb.append(" * ");
            try {
                sb.append(s + licenseTypeComment(s));
            } catch (LicenseExpressionException e) {
                sb.append(s);
            }
            sb.append("\n");
        }


        // TODO: format properly
        sb.append("allowed:" + report.complianAllowedtPaths() + "\n\n\n");
        sb.append("gray:   " + report.compliantGrayPaths() + "\n\n\n");
        sb.append("denied:   " + report.nonCompliantPaths() + "\n\n\n");

    }

    private String pathComment(int pathCount) {
        switch (pathCount) {
            case 0:
                return "No";
            case 1:
                return "Yes, 1 path";
            default:
                return "Yes, " + pathCount + " paths";
        }
    }



  

  
}
