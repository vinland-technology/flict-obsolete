// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.cli;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.exporter.ReportExporterFactory;
import com.sandklef.compliance.json.JsonComponentParser;
import com.sandklef.compliance.json.JsonLicenseConnectionsParser;
import com.sandklef.compliance.json.JsonLicenseParser;
import com.sandklef.compliance.json.JsonPolicyParser;
import com.sandklef.compliance.utils.LicenseArbiter;
import com.sandklef.compliance.utils.LicenseStore;
import com.sandklef.compliance.utils.LicenseUtils;
import com.sandklef.compliance.utils.Log;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LicenseChecker {

    private enum execMode {
        PRINT_LICENSES,
        PRINT_COMPONENT,
        CHECK_VIOLATION;
    } ;

    private static final String LOG_TAG = LicenseChecker.class.getSimpleName() ;

    public static void main(String[] args) throws IOException {

        // Create and setup Options
        Options options = setupOptions();

        // Create and setup default values
        Map<String, Object> values = defaultValues();

        // Parse arguments
        parseArguments(args, options, values);

        // Read all licenses
        Log.d(LOG_TAG, "license dir: " + values.get("licenseDir"));
        LicenseStore.getInstance().addLicenses(new JsonLicenseParser().readLicenseDir((String) values.get("licenseDir")));
        Log.d(LOG_TAG, "licenses read: " + LicenseStore.getInstance().licenses().size());

        // If policy file provided - read it
        if (values.get("policyFile")!=null) {
            JsonPolicyParser jp = new JsonPolicyParser();
            values.put("policy", jp.readLicensePolicy((String) values.get("policyFile")));
            System.out.println("   policy file: " + values.get("policyFile"));
        }

        // Take action
        if (execMode.PRINT_LICENSES == values.get("mode")) {
            licensePrint();
        } else if (execMode.PRINT_COMPONENT == values.get("mode")) {
            componentPrint(values, options);
        } else if (execMode.CHECK_VIOLATION == values.get("mode")) {
            reportPrint(values, options);
        }
    }


    public static void help(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("license-checker.sh", options );
    }

    private static Options setupOptions() {
        final Options options = new Options();
        options.addOption(new Option("d", "debug", false, "Turn on debug."));
        options.addOption(new Option("dc", "debug-class", true, "Turn on debug for class only."));
        options.addOption(new Option("v", "violation", false, "Check for violations."));
        options.addOption(new Option("l", "license-dir", true, "Directory with license files."));
        options.addOption(new Option("p", "policy-file", true, "Path to policy file."));
        options.addOption(new Option("pl", "print-licenses", false, "Output list of licenses as found in the files in the provided license directory"));
        options.addOption(new Option("c", "component", true, "Component file to check"));
        options.addOption(new Option("pc", "print-component", false, "Print component"));
        options.addOption(new Option("h", "help", false, "Print help text"));
        options.addOption(new Option("j", "json", false, "Output result in JSON format"));
        options.addOption(new Option("md", "markdown", false, "Output result in Markdown format"));
        return options;
    }


    private static Map<String, Object> defaultValues() {
        // Prepare map wth default values
        Map<String, Object> values = new HashMap<>();
        values.put("componentFile", null);
        values.put("licenseDir", "licenses/json");
        values.put("policyFile", null);
        values.put("policy", null);
        values.put("mode", execMode.PRINT_LICENSES);
        return values;
    }

    private static CommandLineParser parseArguments(String[] args, Options options, Map<String, Object> values) {
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
                values.put("mode",execMode.CHECK_VIOLATION);
            }
            if( line.hasOption( "component" ) ) {
                values.put("componentFile", line.getOptionValue("component"));
                Log.d(LOG_TAG, " Component file: " + values.get("componentFile"));
            }
            if( line.hasOption( "licenses" ) ) {
                values.put("mode", execMode.PRINT_LICENSES);
                Log.d(LOG_TAG, " licenses mode choosend");
            }
            if( line.hasOption( "license-dir" ) ) {
                values.put("licenseDir", line.getOptionValue("license-dir"));
                Log.d(LOG_TAG, " License dir: " + values.get("licenseDir"));
            }
            if( line.hasOption( "policy-file" ) ) {
                values.put("policyFile", line.getOptionValue("policy-file"));
                System.out.println("  POLICY: " + line.getOptionValue("policyFile"));
                Log.d(LOG_TAG, " Policy file: " + values.get("policyFile"));
            }
            if( line.hasOption( "print-license" ) ) {
                values.put("mode", execMode.PRINT_LICENSES);
            }
            if( line.hasOption( "print-component" ) ) {
                values.put("mode", execMode.PRINT_COMPONENT);
            }
            if( line.hasOption( "help" ) ) {
                help(options);
                System.exit(0);
            }
            if( line.hasOption( "json" ) ) {
                values.put("format", ReportExporterFactory.OutputFormat.JSON);
            }
            if( line.hasOption( "markdown" ) ) {
                values.put("format", ReportExporterFactory.OutputFormat.MARKDOWN);
            }
            if( line.hasOption( "pdf" ) ) {
                System.out.println("Ignoring pdf argument");
            }

        }
        catch( ParseException exp ) {
            System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
            System.exit(1);
        }

        return parser;
    }

    private static void licensePrint(){
        Log.d(LOG_TAG, "printing licenses...");
        System.out.println(LicenseStore.getInstance().licenseString());
    }

    private static void componentPrint(Map<String, Object> values, Options options) throws IOException {
        Log.d(LOG_TAG, "printing component...");
        Log.d(LOG_TAG, "component file: " + values.get("componentFile"));
        JsonComponentParser jp = new JsonComponentParser();
        Component c = jp.readComponent((String) values.get("componentFile"));
        System.out.println(c.toStringLong());
    }


    private static void reportPrint(Map<String, Object> values, Options options) throws IOException {
        if (values.get("componentFile")==null) {
            System.err.println("\n*** Error: missing component file! ***\n\n");
            help(options);
            System.err.println("\n\n.... cowardly bailing out.\n");
            System.exit(1);
        }

     //   System.out.println("json: " + values.get("format"));

        // Read component
        Log.d(LOG_TAG, "component file: " + values.get("componentFile"));
        JsonComponentParser jp = new JsonComponentParser();
        Component c = jp.readComponent((String) values.get("componentFile"));
        Log.d(LOG_TAG, "Component read: " + c.name());
        Log.d(LOG_TAG, " * deps: " + c.dependencies().size());

        Report report = LicenseArbiter.report(c, (LicensePolicy) values.get("policy"));

        System.out.print(ReportExporterFactory.getInstance().exporter((ReportExporterFactory.OutputFormat) values.get("format")).exportReport(report)+"\n");
    }

}
