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
import com.sandklef.compliance.utils.Version;
import org.apache.commons.cli.*;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.IOException;

public class LicenseChecker {

    private enum execMode {
        PRINT_LICENSES,
        CHECK_VIOLATION;
    } ;

    private static final String LOG_TAG = LicenseChecker.class.getSimpleName() ;


    public static void help(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("license-checker.sh", options );
    }

    private enum OutputFormat {
        TEXT,
        JSON;
    }

    public static void main(String[] args) throws IOException {

        final Options options = new Options();
        options.addOption(new Option("d", "debug", false, "Turn on debug."));
        options.addOption(new Option("dc", "debug-class", true, "Turn on debug for class only."));
        options.addOption(new Option("v", "violation", false, "Check for violations."));
        options.addOption(new Option("l", "license-dir", true, "Directory with license files."));
        options.addOption(new Option("p", "policy", true, "Path to policy file."));
        options.addOption(new Option("p", "print-licenses", false, "Output list of licenses as found in the files in the provided license directory"));
        options.addOption(new Option("c", "component", true, "Component file to check"));
        options.addOption(new Option("h", "help", false, "Print help text"));
        options.addOption(new Option("j", "json", false, "Output result in JSON format"));


        execMode mode = execMode.PRINT_LICENSES;

        String componentFile = null;
        String licenseDir = "licenses/json";
        String policyFile = null;
        LicensePolicy policy = null;
        OutputFormat format = OutputFormat.TEXT;

        CommandLineParser parser = new DefaultParser();

        try {
            // parse the command line arguments
            CommandLine line = parser.parse( options, args );

            if( line.hasOption( "debug" ) ) {
                System.out.println(" DEBUG found");
                Log.level(Log.VERBOSE);
            }
            if( line.hasOption( "debug-class" ) ) {
                System.out.println(" DEBUG cli found, setting filter to: " + LOG_TAG);
                Log.level(Log.VERBOSE);
                Log.filterTag(line.getOptionValue("debug-class"));
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
            if( line.hasOption( "help" ) ) {
                help(options);
                System.exit(0);
            }
            if( line.hasOption( "json" ) ) {
                format = OutputFormat.JSON;
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
            if (componentFile==null) {
                System.err.println("\n*** Error: missing component file! ***\n\n");
                help(options);
                System.err.println("\n\n.... cowardly bailing out.\n");
                System.exit(1);
            }

            // Read component
            Log.d(LOG_TAG, "component file: " + componentFile);
            JsonComponentParser jp = new JsonComponentParser();
            Component c = jp.readComponent(componentFile);
            Log.d(LOG_TAG, "Component read: " + c.name());
            Log.d(LOG_TAG, " * deps: " + c.dependencies().size());

            Report report = LicenseArbiter.report(c, policy);
            if (format==OutputFormat.TEXT) {
                System.out.println("Report from analysing component: \"" + c.name() + "\"\n");
                System.out.println("violation report:  " + report.violation());
                System.out.println("conclusion report: " + report.conclusion());
                System.out.println("concern report: " + report.concern());
            } else {
                ReportExporter exporter = new JsonExporter();
             //   System.out.println("violation report:  \n\n" + exporter.exportLicenseViolation(report.violation()));
              //  System.out.println("conclusion report:  \n\n" + exporter.exportConclusion(report.conclusion()));
               // System.out.println("conclusion report:  \n\n" + exporter.exportConcern(report.concern()));
                System.out.println(exporter.exportReport(report));
            }

        }
    }

}