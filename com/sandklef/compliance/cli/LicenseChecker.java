// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.cli;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.exporter.ReportExporterFactory;
import com.sandklef.compliance.json.*;
import com.sandklef.compliance.utils.*;
import org.apache.commons.cli.*;

import java.io.*;
import java.util.*;

import com.sandklef.compliance.utils.LicenseStore;

public class LicenseChecker {

    private enum execMode {
        PRINT_LICENSES,
        PRINT_COMPATIBILITIES,
        PRINT_COMPONENT,
        PRINT_COMPONENT_LICENSES,
        PRINT_EXPRESSION,
        CHECK_COMPATIBILITY
    }

    private static final String CHECK_COMPATIBILITY_CLI = "check-compatibility";
    private static final String COMPATIBILITIES_FILE_CLI = "compatibility-file";
    private static final String COMPONENT_FILE_CLI = "component-file";
    private static final String POLICY_FILE_CLI = "policy-file";
    private static final String LICENSE_DIR = "license-dir";
    private static final String LATER_FILE_CLI = "later-file";
    private static final String VERSION_CLI = "version";

    private static final String LOG_TAG = LicenseChecker.class.getSimpleName();
    private static PrintStream writer;

    public static void main(String[] args) throws IOException {

        try {
            // Create and setup Options
            Options options = setupOptions();

            // Create and setup default values
            Map<String, Object> values = defaultValues();

            // Parse arguments
            parseArguments(args, options, values);

            Session session = Session.getInstance();
            session.lLicenseDir((String)values.get(LICENSE_DIR));
            session.connectorFile((String)values.get(COMPATIBILITIES_FILE_CLI));
            session.policyFile((String)values.get(POLICY_FILE_CLI));
            session.componentFile((String)values.get(COMPONENT_FILE_CLI));
            session.laterFile((String)values.get(LATER_FILE_CLI));


            // Read all licenses
            Log.d(LOG_TAG, "license dir: " + values.get("licenseDir"));
            Log.d(LOG_TAG, "Connector file: " + session.connectorFile());
            LicenseStore.getInstance().addLicenses(new JsonLicenseParser().readLicenseDir(session.lLicenseDir()));
            LicenseStore.getInstance().addLicenseGroups(new JsonLicenseParser().readLicenseGroupDir(session.lLicenseDir()));
            LicenseStore.getInstance().connector(new JsonLicenseCompatibilityParser().readLicenseConnection(session.connectorFile()));
            LicenseStore.getInstance().laterLicenses((new JsonLaterDefinitionParser()).readLaterDefinition(session.laterFile()));
            Log.d(LOG_TAG, "licenses read: " + LicenseStore.getInstance().licenses().size());

            LicenseUtils.verifyLicenses();

            // If policy file provided - read it
            if (session.policyFile() != null) {
                JsonPolicyParser jp = new JsonPolicyParser();
                values.put("policy", jp.readLicensePolicy(session.policyFile()));
                //                System.out.println("   policy file: " + session.policyFile());
            }

            String outFileName = (String) values.get("output");
            if (outFileName != null) {
                try {
                    writer = new PrintStream(
                            new FileOutputStream(outFileName, true));
                    //TODO: handle properly
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                writer = System.out;
            }

            execMode em = (execMode) values.get("mode");
            //        System.out.println(("  mode: " + values.get("mode")));
            // Take action
            switch (em) {
                case PRINT_LICENSES:
                    licensePrint(writer);
                    break;
                case PRINT_COMPATIBILITIES:
                    LicenseUtils.connectionsPrintDot(writer);
                    break;
                case PRINT_COMPONENT:
                    try {
                        componentPrint(writer, values, options);
                    } catch (LicenseExpressionException e) {
                        e.printStackTrace();
                    }
                    break;
                case CHECK_COMPATIBILITY:
                    try {
                        reportPrint(writer, values, options);
                    } catch (LicenseExpressionException | IllegalLicenseExpression e) {
                        e.printStackTrace();
                    }
                    break;
                case PRINT_COMPONENT_LICENSES:
                    printComponentWithLicenses(values, options);
                    break;
                case PRINT_EXPRESSION:
                    String expr = (String) values.get("expression");
                    System.out.println("\nLicense expression:\n--------------------------------\n" + expr);
                    LicenseExpressionParser lep = new LicenseExpressionParser();
                    String laterExpr = lep.fixOrLaterExpression(expr);
                    System.out.println("\nLicense expression with later:\n--------------------------------\n" + laterExpr);
                    String fixedExpr = lep.fixLicenseExpression(laterExpr);
                    System.out.println("\nLicense expression with parenthesises:\n--------------------------------\n" + fixedExpr);
                    LicenseExpression licenseExpression = lep.parse(expr);
                    System.out.println("\nLicenseExpression:\n--------------------------------\n" + licenseExpression);
                    List<List<License>> licenes = licenseExpression.licenseList();
                    System.out.println("\nList of License Lists:\n--------------------------------\n" + LicenseExpression.licenseListToString(licenes));
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + em);
            }

        } catch (LicenseExpressionException | IllegalLicenseExpression | LicenseCompatibility.LicenseConnectorException e) {
            System.out.println("Uh oh, something wicked this way comes: " + e);
        }


    }


    public static void help(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("flict", options);
    }

    private static Options setupOptions() {
        final Options options = new Options();
        options.addOption(new Option("d", "debug", false, "Turn on debug."));
        options.addOption(new Option("dc", "debug-class", true, "Turn on debug for class only."));
        options.addOption(new Option("o", "output", true, "Output to file."));
        options.addOption(new Option("e", "expression", true, "Parse and print a license expression (for debug)"));
        options.addOption(new Option("cg", "connection-graph", false, "Output dot format over license connections."));
        options.addOption(new Option("cf", COMPATIBILITIES_FILE_CLI, true, "File with license connectors."));
        options.addOption(new Option("cc", CHECK_COMPATIBILITY_CLI, false, "Check for compatibility."));
        options.addOption(new Option("ld", LICENSE_DIR, true, "Directory with license files."));
        options.addOption(new Option("p", POLICY_FILE_CLI, true, "Path to policy file."));
        options.addOption(new Option("pl", "print-licenses", false, "Output list of licenses as found in the files in the provided license directory"));
        options.addOption(new Option("c", COMPONENT_FILE_CLI, true, "Component file to check"));
        options.addOption(new Option("lf", LATER_FILE_CLI, true, "Later license file"));
        options.addOption(new Option("v", VERSION_CLI, false, "Prints version and more"));
        options.addOption(new Option("pc", "print-component", false, "Print component"));
        options.addOption(new Option("h", "help", false, "Print help text"));
        options.addOption(new Option("j", "json", false, "Output result in JSON format"));
        options.addOption(new Option("md", "markdown", false, "Output result in Markdown format"));
        options.addOption(new Option("dcl", "debug-component-licenses", false, "Output component with deps and license combinations"));
        return options;
    }


    private static Map<String, Object> defaultValues() {
        // Prepare map wth default values
        Map<String, Object> values = new HashMap<>();
        values.put(COMPONENT_FILE_CLI, null);
        values.put(COMPATIBILITIES_FILE_CLI, "share/licenses/connections/license-checker.json");
        values.put(LATER_FILE_CLI, "share/licenses/later/later-definitions.json");
        values.put("output", null);
        values.put(LICENSE_DIR, "licenses/json");
        values.put(POLICY_FILE_CLI, null);
        values.put("policy", null);
        values.put("expression", null);
        values.put("mode", execMode.CHECK_COMPATIBILITY);
        return values;
    }

    private static CommandLineParser parseArguments(String[] args, Options options, Map<String, Object> values) {
        CommandLineParser parser = new DefaultParser();

        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);

            if (line.hasOption("debug")) {
                Log.level(Log.VERBOSE);
            }
            if (line.hasOption("output")) {
                values.put("output", line.getOptionValue("output"));
            }
            if (line.hasOption("debug-class")) {
                Log.level(Log.VERBOSE);
                Log.filterTag(line.getOptionValue("debug-class"));
            }
            if (line.hasOption("connection-graph")) {
                values.put("mode", execMode.PRINT_COMPATIBILITIES);
            }
            if (line.hasOption("debug-component-licenses")) {
                values.put("mode", execMode.PRINT_COMPONENT_LICENSES);
            }
            if (line.hasOption(COMPATIBILITIES_FILE_CLI)) {
                Log.d(LOG_TAG, "Connector file: " + line.getOptionValue(COMPATIBILITIES_FILE_CLI));
                values.put(COMPATIBILITIES_FILE_CLI, line.getOptionValue(COMPATIBILITIES_FILE_CLI));
            }
            if (line.hasOption(CHECK_COMPATIBILITY_CLI)) {
                Log.d(LOG_TAG, " Checking compatibility");
                values.put("mode", execMode.CHECK_COMPATIBILITY);
            }
            if (line.hasOption(COMPONENT_FILE_CLI)) {
                values.put(COMPONENT_FILE_CLI, line.getOptionValue(COMPONENT_FILE_CLI));
                Log.d(LOG_TAG, " Component file: " + values.get(COMPONENT_FILE_CLI));
            }
            if (line.hasOption(LATER_FILE_CLI)) {
                values.put(LATER_FILE_CLI, line.getOptionValue(LATER_FILE_CLI));
                Log.d(LOG_TAG, " Later definitions file: " + values.get(LATER_FILE_CLI));
            }
            if (line.hasOption("expression")) {
                values.put("expression", line.getOptionValue("expression"));
                values.put("mode", execMode.PRINT_EXPRESSION);
                Log.d(LOG_TAG, "License expression: " + values.get("expression"));
            }
            if (line.hasOption("licenses")) {
                values.put("mode", execMode.PRINT_LICENSES);
                Log.d(LOG_TAG, " licenses mode choosend");
            }
            if (line.hasOption(VERSION_CLI)) {
                version();
                System.exit(0);
            }
            if (line.hasOption(LICENSE_DIR)) {
                values.put(LICENSE_DIR, line.getOptionValue(LICENSE_DIR));
                Log.d(LOG_TAG, " License dir: " + values.get(LICENSE_DIR));
            }
            if (line.hasOption(POLICY_FILE_CLI)) {
                values.put(POLICY_FILE_CLI, line.getOptionValue(POLICY_FILE_CLI));
                Log.d(LOG_TAG, " Policy file: " + values.get(POLICY_FILE_CLI));
            }
            if (line.hasOption("print-license")) {
                values.put("mode", execMode.PRINT_LICENSES);
            }
            if (line.hasOption("print-component")) {
                values.put("mode", execMode.PRINT_COMPONENT);
            }
            if (line.hasOption("help")) {
                help(options);
                System.exit(0);
            }
            if (line.hasOption("json")) {
                values.put("format", ReportExporterFactory.OutputFormat.JSON);
            }
            if (line.hasOption("markdown")) {
                values.put("format", ReportExporterFactory.OutputFormat.MARKDOWN);
            }
            if (line.hasOption("pdf")) {
            }

        } catch (ParseException exp) {
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
            System.exit(1);
        }

        return parser;
    }

    private static void version() {
        System.out.println(Version.LICENSE_CHECKER_NAME + " (" + Version.LICENSE_CHECKER_VERSION + ")");
        System.out.println("Copyright (C) " + Version.LICENSE_CHECKER_COPYRIGHT);
        System.out.println("License " + Version.LICENSE_CHECKER_LICENSE);
        System.out.println("Authors " + Version.LICENSE_CHECKER_AUTHORS);
    }


    private static void licensePrint(PrintStream writer) {
        Log.d(LOG_TAG, "printing licenses...");
        writer.println(LicenseStore.getInstance().licenseString());
    }

    private static void componentPrint(PrintStream writer, Map<String, Object> values, Options options) throws IOException, LicenseExpressionException, IllegalLicenseExpression {
        Log.d(LOG_TAG, "printing component...");
        Log.d(LOG_TAG, "component file: " + values.get(COMPONENT_FILE_CLI));
        JsonComponentParser jp = new JsonComponentParser();

        Component c = jp.readComponent((String) values.get(COMPONENT_FILE_CLI));
        writer.println(c.toStringLong());
    }

    private static void printComponentWithLicenses(Map<String, Object> values, Options options) throws IOException, LicenseExpressionException, IllegalLicenseExpression {
        if (values.get(COMPONENT_FILE_CLI) == null) {
            System.err.println("\n*** Error: missing component file! ***\n\n");
            help(options);
            System.err.println("\n\n.... cowardly bailing out.\n");
            System.exit(1);
        }

        // Read component
        Log.d(LOG_TAG, "component file: " + values.get(COMPONENT_FILE_CLI));
        JsonComponentParser jp = new JsonComponentParser();
        // TODO: remove get(0)
        Component c = jp.readComponent((String) values.get(COMPONENT_FILE_CLI));
        Log.d(LOG_TAG, "Component read: " + c.name());
        Log.d(LOG_TAG, " * deps: " + c.dependencies().size());

        System.out.println(c.toStringWithLicenses());

        try {
            String str = LicenseArbiter.componentsWithLicenses(c, null);
            System.out.println(str);
        } catch (LicenseCompatibility.LicenseConnectorException e) {
            e.printStackTrace();
        }

    }

    private static void reportPrint(PrintStream writer, Map<String, Object> values, Options options) throws IOException, LicenseExpressionException, IllegalLicenseExpression {
        if (values.get(COMPONENT_FILE_CLI) == null) {
            System.err.println("\n*** Error: missing component file! ***\n\n");
            help(options);
            System.err.println("\n\n.... cowardly bailing out.\n");
            System.exit(1);
        }

        //   System.out.println("json: " + values.get("format"));

        // Read component
        Log.d(LOG_TAG, "component file: " + values.get(COMPONENT_FILE_CLI));
        JsonComponentParser jp = new JsonComponentParser();
        // TODO: remove get(0)
        Component c = jp.readComponent((String) values.get(COMPONENT_FILE_CLI));
        Log.d(LOG_TAG, "Component read: " + c.name());
        Log.d(LOG_TAG, " * deps: " + c.dependencies().size());

        Report report = null;
        try {
            report = LicenseArbiter.report(c, (LicensePolicy) values.get("policy"));
            writer.print(ReportExporterFactory.getInstance().exporter((ReportExporterFactory.OutputFormat) values.get("format")).exportReport(report) + "\n");
            if (report.compliantCount()==0) {
                System.exit(2);
            }
            if (report.compliantCount()==report.compliantGrayPaths().size()) {
                System.exit(1);
            }
            System.exit(0);
        } catch (LicenseCompatibility.LicenseConnectorException e) {
            e.printStackTrace();
            System.exit(4);
        }

    }

}
