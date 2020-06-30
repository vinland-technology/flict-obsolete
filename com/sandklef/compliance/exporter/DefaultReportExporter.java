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

public class TextComponentExporter implements ReportExporter {

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

        sb.append("Report for ");
        sb.append(c.name());
        sb.append("\n===========================================\n");

        summaryReport(sb);

        // About check
        aboutReport(sb);

        // DETAILED REPORT
        detailedReport(sb);

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

    private void detailedReport(StringBuilder sb)  {
        sb.append("Detailed report");
        sb.append("\n--------------------------------------------\n");
        sb.append("\n\n");

        sb.append("Dependency components: ");
        sb.append("\n\n");
        for (Component d : c.allDependenciesImpl()) {
            sb.append(" * ");
            sb.append(d);
            sb.append("\n");
        }
        sb.append("\n\n");

        sb.append("Liceneses: ");
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

        sb.append("Compliance information");
        sb.append("\n\n");

        sb.append("Compliant license combinations: ");
        sb.append(report.compliantCount());
        sb.append("\n\n");

        sb.append("Compliant gray license combinations: ");
        sb.append(report.compliantGrayPaths().size());
        sb.append("\n\n");

        sb.append("Compliant denied license combinations: ");
        sb.append(report.compliantDeniedPaths().size());
        sb.append("\n\n");

        sb.append("Non compliant license combinations: ");
        sb.append(report.nonCompliantPaths().size());
        sb.append("\n\n");


        sb.append("Compliant allowed license choices per component: ");
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
            }
        }
        sb.append("\n\n");

        sb.append("Compliant gray license choices per component: ");
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

        sb.append("Component: ");
        sb.append(c.name());
        sb.append("\n\n");

        sb.append("Total license combinations: ");
        sb.append(report.componentResults().size());
        sb.append("\n\n");

        sb.append("Compliant (allowed licenses only): ");
        sb.append(pathComment(report.compliantCount()));
        sb.append("\n\n");

        sb.append("Compliant (with gray licenses): ");
        sb.append(pathComment(report.compliantGrayPaths().size()));
        sb.append("\n\n");

        sb.append("Policy: ");
        sb.append(report.policy()==null?" none":" " + Session.getInstance().policyFile());
        sb.append("\n\n");

        sb.append("Numbers of dependency components: ");
        sb.append(c.nrDependencies());
        sb.append("\n\n");

        sb.append("Numbers of licenses: ");
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
        sb.append("Tool: ");
        sb.append(report.metaData().producer());
        sb.append(", version ");
        sb.append(report.metaData().version());
        sb.append("\n\n");
        sb.append("Check duration: ");
        sb.append(report.metaData().duration());
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

        sb.append("Current directory: ");
        sb.append(System.getProperty("user.dir"));
        sb.append("\n\n");
        Session session = Session.getInstance();

        sb.append("Component file: ");
        sb.append(session.componentFile());
        sb.append("\n\n");

        sb.append("License dir: ");
        sb.append(session.lLicenseDir());
        sb.append("\n\n");

        sb.append("Connector file: ");
        sb.append(session.connectorFile());
        sb.append("\n\n");

        sb.append("Policy file: ");
        sb.append(session.policyFile());
        sb.append("\n\n");
    }

  

  
}
