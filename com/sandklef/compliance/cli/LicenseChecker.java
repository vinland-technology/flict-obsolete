// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.cli;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.exporter.ReportExporter;
import com.sandklef.compliance.json.JsonComponentParser;
import com.sandklef.compliance.json.JsonExporter;
import com.sandklef.compliance.json.JsonLicenseParser;
import com.sandklef.compliance.json.JsonPolicyParser;
import com.sandklef.compliance.utils.LicenseArbiter;
import com.sandklef.compliance.utils.LicenseStore;
import com.sandklef.compliance.utils.Log;
import org.apache.commons.cli.*;

import java.io.IOException;

public class LicenseChecker {

    private enum execMode {
        PRINT_LICENSES,
        CHECK_VIOLATION;
    } ;

    private static final String LOG_TAG = LicenseChecker.class.getSimpleName() ;

    public static void main(String[] args) throws IOException {

        final Options options = new Options();
        options.addOption(new Option("d", "debug", false, "Turn on debug."));
        options.addOption(new Option("dc", "debug-cli", false, "Turn on debug for cli only."));
        options.addOption(new Option("v", "violation", false, "Check for violations."));
        options.addOption(new Option("l", "license-dir", true, "Directory with license files."));
        options.addOption(new Option("pl", "policy-file", true, "Path to policy file."));
        options.addOption(new Option("p", "print-licenses", false, "Output list of licenses"));
        options.addOption(new Option("c", "component", true, "Component file to check"));

        execMode mode = execMode.PRINT_LICENSES;

        String componentFile = null;
        String licenseDir = "licenses/json";
        String policyFile = null;
        LicensePolicy policy = null;

        CommandLineParser parser = new DefaultParser();

        try {
            // parse the command line arguments
            CommandLine line = parser.parse( options, args );

            if( line.hasOption( "debug" ) ) {
                System.out.println(" DEBUG found");
                Log.level(Log.VERBOSE);
            }
            if( line.hasOption( "debug-cli" ) ) {
                System.out.println(" DEBUG cli found, setting filter to: " + LOG_TAG);
                Log.level(Log.VERBOSE);
                Log.filterTag(LOG_TAG);
            }
            if( line.hasOption( "violation" ) ) {
                Log.d(LOG_TAG, " Checking violations");
                mode = execMode.CHECK_VIOLATION;
            }
            if( line.hasOption( "component" ) ) {
                componentFile = line.getOptionValue("component");
                Log.d(LOG_TAG, " Component file: " + componentFile);
            }
            if( line.hasOption( "licenses" ) ) {
                mode = execMode.PRINT_LICENSES;
                Log.d(LOG_TAG, " licenses mode choosend");
            }
            if( line.hasOption( "license-dir" ) ) {
                licenseDir = line.getOptionValue("license-dir");
                Log.d(LOG_TAG, " License dir: " + licenseDir);
            }
            if( line.hasOption( "policy-file" ) ) {
                policyFile = line.getOptionValue("policy-file");
                Log.d(LOG_TAG, " Policy file: " + policyFile);
            }
        }
        catch( ParseException exp ) {
            System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
            System.exit(1);
        }



        // Read all licenses
        Log.d(LOG_TAG, "license dir: " + licenseDir);
        LicenseStore.getInstance().addLicenses(new JsonLicenseParser().readLicenseDir(licenseDir));

        if (policyFile!=null) {
            JsonPolicyParser jp = new JsonPolicyParser();
            policy = jp.readLicensePolicy(policyFile);
    //        System.out.println("policy: " + policy );

           // System.exit(0);
        }


        Log.d(LOG_TAG, "licenses read: " + LicenseStore.getInstance().licenses().size());


        if (execMode.PRINT_LICENSES == mode) {
            Log.d(LOG_TAG, "printing licenses...");
            System.out.println(LicenseStore.getInstance().licenseString());
        } else if (execMode.CHECK_VIOLATION == mode) {
            // Read component
            Log.d(LOG_TAG, "component file: " + componentFile);
            JsonComponentParser jp = new JsonComponentParser();
            Component c = jp.readComponent(componentFile);
            Log.d(LOG_TAG, "Component read: " + c.name());
            Log.d(LOG_TAG, " * deps: " + c.dependencies().size());


            Report report = LicenseArbiter.report(c, policy);
            System.out.println("Report from analysing component: \"" + c.name() + "\"\n");
            System.out.println("violation report:  " + report.violation());
            System.out.println("conclusion report: " + report.conclusion());
            System.out.println("concern report: " + report.concern());

            System.out.println("--- JSON ----");
            ReportExporter exporter = new JsonExporter();
            System.out.println("violation report:  \n\n" + exporter.exportLicenseViolation(report.violation()));
            System.out.println("conclusion report:  \n\n" + exporter.exportConclusion(report.conclusion()));
            System.out.println("conclusion report:  \n\n" + exporter.exportConcern(report.concern()));
            System.out.println("report:  \n\n" + exporter.exportReport(report));


        }
    }

}