package com.sandklef.compliance.exporter;

import com.sandklef.compliance.domain.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class PileComplianceTextReportExporter implements PileComplianceReportExporter {

    @Override
    public String exportReport(PileComplianceReport report, OutputRange range)
            throws IllegalLicenseExpression, LicenseExpressionException {
        if (range == OutputRange.SHORT) {
            return shortReport(report);
        } else {
            return shortReport(report) + "\n" + longReport(report);
        }
    }

    private String shortReport(PileComplianceReport pileComplianceStatus)
            throws IllegalLicenseExpression, LicenseExpressionException {
        StringBuilder sb = new StringBuilder();

        sb.append("---------------------================= Pile Compliance Status ======================------------------------");

        sb.append("Outbound licenses:" );
        sb.append(" * Allowed: " );
        boolean first = true;
        for (List<License> licenses : pileComplianceStatus.compliantAllowed()) {
            System.out.print("    * ");
            first = true;
            for (License license : licenses) {
                if (first) {
                    first = false;
                } else {
                    System.out.print(" & ");
                }
                System.out.print(license );
            }
            sb.append("");
        }
        sb.append(" * Avoid: " );
        for (List<License> licenses : pileComplianceStatus.compliantAvoid()) {
            System.out.print("    * ");
            first = true;
            for (License license : licenses) {
                if (first) {
                    first = false;
                } else {
                    System.out.print(" & ");
                }
                System.out.print(license);
            }
            sb.append("");
        }

        sb.append("Metadata:");
        sb.append("\n");
        sb.append(" * producer:           " + pileComplianceStatus.metaData().producer() + " (" + pileComplianceStatus.metaData().version() + ")");
        sb.append("\n");
        sb.append(" * duration:           " + pileComplianceStatus.metaData().duration() );
        sb.append("\n");

        sb.append("Component:             " + pileComplianceStatus.component() );
        sb.append("\n");
        sb.append("Policy:                " + pileComplianceStatus.policy().name() );
        sb.append("\n");
        sb.append(" * avoid:              " + pileComplianceStatus.policy().avoidList() );
        sb.append("\n");
        sb.append(" * denied:             " + pileComplianceStatus.policy().deniedList() );
        sb.append("\n");
        sb.append("Later file:            " + pileComplianceStatus.laterFile() );
        sb.append("\n");

        sb.append("Combinations:          " + pileComplianceStatus.combinations() );
        sb.append("\n");
        sb.append(" * Compliant:          " + pileComplianceStatus.compliantComponents().size() );
        sb.append("\n");
        sb.append(" * Compliant / avoid:  " + pileComplianceStatus.compliantAvoidedComponents().size() );
        sb.append("\n");
        sb.append(" * Compliant / denied: " + pileComplianceStatus.compliantDeniedComponents().size() );
        sb.append("\n");
        sb.append(" * Not compliant:      " + pileComplianceStatus.inCompliantComponents().size() );
        sb.append("\n");

        sb.append("---------------------================= Pile Compliance Status ======================------------------------");

        return sb.toString();
    }

    private String longReport(PileComplianceReport pileComplianceStatus) throws IllegalLicenseExpression, LicenseExpressionException {
        StringBuilder sb = new StringBuilder();

        sb.append(longReportHelper("All combinations",
                p -> true,
                pileComplianceStatus));
        sb.append("\n");

        sb.append(longReportHelper(" * compliant",
                p -> p.compliant() && p.avoidLicenses().size() == 0 && p.deniedLicenses().size() == 0,
                pileComplianceStatus));
        sb.append("\n");

        sb.append(longReportHelper(" * avoid compliant",
                p -> p.compliant() && p.avoidLicenses().size() > 0 && p.deniedLicenses().size() == 0,
                pileComplianceStatus));
        sb.append("\n");

        sb.append(longReportHelper(" * denied compliant",
                p -> p.compliant() && p.avoidLicenses().size() == 0 && p.deniedLicenses().size() > 0,
                pileComplianceStatus));
        sb.append("\n");

        sb.append(longReportHelper(" * non compliant",
                p -> !p.compliant(),
                pileComplianceStatus));
        sb.append("\n");

        return sb.toString();
    }

    private String longReportHelper(String title, Predicate<PileComplianceReport.ComponentStatus> p, PileComplianceReport pileComplianceStatus)
            throws IllegalLicenseExpression, LicenseExpressionException {
        StringBuilder sb = new StringBuilder();
        sb.append(title + ":          " +  pileComplianceStatus.predicatedComponents(p).size());
        for (PileComplianceReport.ComponentStatus s : pileComplianceStatus.predicatedComponents(p)) {
            sb.append(" --== [" + s.compliant() + " | " + s.avoidLicenses().size() + " | " + s.deniedLicenses().size() + "] ==--");
            Set<String> components = new HashSet<>();
            for (Map.Entry<Component, List<List<License>>> entry : s.map().entrySet()) {
                Component c = entry.getKey();
                StringBuilder innerSb = new StringBuilder();
                innerSb.append(c + ": ");
                boolean first = true;
                for (License l : c.licenseList().get(0)) {
                    if (first) {
                        first = false;
                    } else {
                        innerSb.append(" AND ");
                    }
                    innerSb.append(l.spdx());
                }
                components.add(innerSb.toString());
            }
            for (String cs : components) {
                sb.append(cs);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
