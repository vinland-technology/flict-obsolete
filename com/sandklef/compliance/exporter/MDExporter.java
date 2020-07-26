// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.exporter;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.utils.LicenseArbiter;
import com.sandklef.compliance.utils.LicenseUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

public class MDExporter implements ReportExporter {

    private static final String BOLD_START = "***";
    private static final String BOLD_END = "***";
    private static final String BULLET_ITEM = "*";
    private static final String HEADER_TAG = "#";

    private static final String NEWLINE = "\n";
    private static final String LOG_TAG = MDExporter.class.getSimpleName();

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

        sb.append("# Report ");
        sb.append(c.name());
        sb.append("\n");

        summaryReport(sb);

        // About check
        aboutReport(sb);

        // DETAILED REPORT
        detailedReport(sb);

        return sb.toString();
    }

    private String licenseTypeComment(String license) throws LicenseExpressionException {
        ListType type = LicenseUtils.licenseType(license,report.policy());
        switch (type) {
            case ALLOWED_LIST:
                return "";
            case GRAY_LIST:
                return " (gray listed)";
            case DENIED_LIST:
                return " (denied)";
        }
        return "";
    }

    private void detailedReport(StringBuilder sb)  {
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
            try {
                sb.append(s + licenseTypeComment(s));
            } catch (LicenseExpressionException e) {
                sb.append(s);
            }
            sb.append("\n");
        }
        sb.append("\n\n");

        sb.append("## Compliance information");
        sb.append("\n\n");

        sb.append(BOLD_START + "Compliant license combinations: " + BOLD_END);
        sb.append(report.compliantCount());
        sb.append("\n\n");

        sb.append(BOLD_START + "Compliant gray license combinations: " + BOLD_END);
        sb.append(report.compliantGrayPaths().size());
        sb.append("\n\n");

        sb.append(BOLD_START + "Compliant denied license combinations: " + BOLD_END);
        sb.append(report.compliantDeniedPaths().size());
        sb.append("\n\n");

        sb.append(BOLD_START + "Non compliant license combinations: " + BOLD_END);
        sb.append(report.nonCompliantPaths().size());
        sb.append("\n\n");


        sb.append(BOLD_START + "Compliant allowed license choices per component: " + BOLD_END);
        List<Report.ComponentResult> results = report.complianAllowedtPaths();
        sb.append("\n\n");
        if (results.size()==0) {
            sb.append(" none found");
        } else {
            for (Report.ComponentResult cr : results) {
                sb.append(" * " + cr.component().name() + " (" + beautifyLicense(cr.component().licenses().toString()) + ")");
                for (LicenseArbiter.InterimComponent ic : cr.component().allDependenciesImpl()) {
                    sb.append(", ");
                    sb.append(ic.name() + " (" +
                            beautifyLicense(ic.licenses().toString()) + ")");
                }
                sb.append("\n\n");
            }
        }
        sb.append("\n\n");

        sb.append(BOLD_START + "Compliant gray license choices per component: " + BOLD_END);
        results = report.compliantGrayPaths();
        sb.append("\n\n");
        if (results.size()==0) {
            sb.append(" none found");
        } else {
            for (Report.ComponentResult cr : report.compliantGrayPaths()) {
                sb.append(" * " + cr.component().name() + " (" + beautifyLicense(cr.component().licenses().toString()) + ")");
                for (LicenseArbiter.InterimComponent ic : cr.component().allDependenciesImpl()) {
                    sb.append(", ");
                    sb.append(ic.name() + " (" +
                            beautifyLicense(ic.licenses().toString()) + ")");
                }
            }
        }
        sb.append("\n\n");
    }

    private String beautifyLicense(String license) {
        return license.replace(",", " & ").replace("[", "").replace("]", "");
    }

    private void summaryReport(StringBuilder sb) {

        sb.append("# Summary");
        sb.append("\n\n");

        sb.append(BOLD_START + "Component: " + BOLD_END);
        sb.append(c.name());
        sb.append("\n\n");

        sb.append(BOLD_START + "Total license combinations: " + BOLD_END);
        sb.append(report.componentResults().size());
        sb.append("\n\n");

        sb.append(BOLD_START + "Compliant (allowed licenses only): " + BOLD_END);
        sb.append(pathComment(report.compliantCount()));
        sb.append("\n\n");

        sb.append(BOLD_START + "Compliant (with gray licenses): " + BOLD_END);
        sb.append(pathComment(report.compliantGrayPaths().size()));
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

    private void aboutReport(StringBuilder sb) {
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
        sb.append("\n\n");
        try {
            Enumeration ifaceEnum = NetworkInterface.getNetworkInterfaces();
            while(ifaceEnum.hasMoreElements())
            {
                NetworkInterface iface = (NetworkInterface) ifaceEnum.nextElement();
                Enumeration addressEnum = iface.getInetAddresses();
                while (addressEnum.hasMoreElements())
                {
                    sb.append(" * ");
                    InetAddress i = (InetAddress) addressEnum.nextElement();
                    sb.append(i);
                    sb.append("\n\n");
                }
            }
        } catch (SocketException e) {
            sb.append("* unknown");
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

