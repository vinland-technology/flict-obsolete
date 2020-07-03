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


    @Override
    public String exportReport(Report report) {
        init(report);
        StringBuilder sb = new StringBuilder();

        sb.append("Report ");
        sb.append(c.name());

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

    private void summaryReport(StringBuilder sb) {

        sb.append("Total license combinations: ");
        sb.append(report.componentResults().size());
        sb.append("\n");

        sb.append("Compliant (allowed licenses only): ");
        sb.append(pathComment(report.compliantCount()));
        sb.append("\n");

        sb.append("Compliant (with gray licenses): ");
        sb.append(pathComment(report.compliantGrayPaths().size()));
        sb.append("\n");

        sb.append("Policy: ");
        sb.append(report.policy()==null?" none":" " + Session.getInstance().policyFile());
        sb.append("\n");

        sb.append("Numbers of dependency components: ");
        sb.append(c.nrDependencies());
        sb.append("\n");
        for (Component d : c.allDependenciesImpl()) {
            sb.append(" * ");
            sb.append(d);
            sb.append("\n");
        }

        sb.append("Numbers of licenses: ");
        sb.append(licenseSet.size());
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

        sb.append("Compliant license combinations: ");
        sb.append(report.compliantCount());
        sb.append("\n");

        sb.append("Compliant gray license combinations: ");
        sb.append(report.compliantGrayPaths().size());
        sb.append("\n");

        sb.append("Compliant denied license combinations: ");
        sb.append(report.compliantDeniedPaths().size());
        sb.append("\n");

        sb.append("Non compliant license combinations: ");
        sb.append(report.nonCompliantPaths().size());
        sb.append("\n");

        System.out.println("results:  " + report.complianAllowedtPaths());
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
