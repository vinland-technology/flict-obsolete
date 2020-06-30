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
import com.sandklef.compliance.utils.*;
import org.apache.commons.cli.*;

import java.io.*;
import java.util.*;

import com.sandklef.compliance.utils.LicenseStore;

public class LicenseChecker {

    private enum execMode {
        PRINT_LICENSES,
        PRINT_CONNECTIONS,
        PRINT_COMPONENT,
        PRINT_COMPONENT_LICENSES,
        PRINT_EXPRESSION,
        CHECK_VIOLATION
    }

    private static final String CONNECTOR_FILE_CLI = "connector-file";
    private static final String COMPONENT_FILE_CLI = "component-file";
    private static final String POLICY_FILE_CLI = "policy-file";

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
            session.lLicenseDir((String)values.get("licenseDir"));
            session.connectorFile((String)values.get(CONNECTOR_FILE_CLI));
            session.policyFile((String)values.get(POLICY_FILE_CLI));
            session.componentFile((String)values.get(COMPONENT_FILE_CLI));


            // Read all licenses
            Log.d(LOG_TAG, "license dir: " + values.get("licenseDir"));
            Log.d(LOG_TAG, "Connector file: " + session.connectorFile());
            LicenseStore.getInstance().addLicenses(new JsonLicenseParser().readLicenseDir(session.lLicenseDir()));
            LicenseStore.getInstance().addLicenseGroups(new JsonLicenseParser().readLicenseGroupDir(session.lLicenseDir()));
            LicenseStore.getInstance().connector(new JsonLicenseConnectionsParser().readLicenseConnection(session.connectorFile()));
            Log.d(LOG_TAG, "licenses read: " + LicenseStore.getInstance().licenses().size());

            LicenseUtils.verifyLicenses();

            // If policy file provided - read it
            if (session.policyFile() != null) {
                JsonPolicyParser jp = new JsonPolicyParser();
                values.put("policy", jp.readLicensePolicy(session.policyFile()));
                System.out.println("   policy file: " + session.policyFile());
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
                case PRINT_CONNECTIONS:
                    LicenseUtils.connectionsPrintDot(writer);
                    break;
                case PRINT_COMPONENT:
                    try {
                        componentPrint(writer, values, options);
                    } catch (LicenseExpressionException e) {
                        e.printStackTrace();
                    }
                    break;
                case CHECK_VIOLATION:
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
                    expr = lep.fixLicenseExpression(expr);
                    System.out.println("\nLicense expression with parenthesises:\n--------------------------------\n" + expr);
                    LicenseExpression licenseExpression = lep.parse(expr);
                    System.out.println("\nLicenseExpression:\n--------------------------------\n" + licenseExpression);
                    List<List<License>> licenes = licenseExpression.licenseList();
                    System.out.println("\nList of License Lists:\n--------------------------------\n" + LicenseExpression.licenseListToString(licenes));
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + em);
            }

        } catch (LicenseExpressionException | IllegalLicenseExpression | LicenseConnector.LicenseConnectorException e) {
            System.out.println("Uh oh, something wicked this way comes: " + e);
        }


    }


    public static void help(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("license-checker.sh", options);
    }

    private static Options setupOptions() {
        final Options options = new Options();
        options.addOption(new Option("d", "debug", false, "Turn on debug."));
        options.addOption(new Option("dc", "debug-class", true, "Turn on debug for class only."));
        options.addOption(new Option("o", "output", true, "Output to file."));
        options.addOption(new Option("e", "expression", true, "Parse and print a license expression (for debug)"));
        options.addOption(new Option("cg", "connection-graph", false, "Output dot format over license connections."));
        options.addOption(new Option("cf", CONNECTOR_FILE_CLI, true, "File with license connectors."));
        options.addOption(new Option("v", "violation", false, "Check for violations."));
        options.addOption(new Option("l", "license-dir", true, "Directory with license files."));
        options.addOption(new Option("p", POLICY_FILE_CLI, true, "Path to policy file."));
        options.addOption(new Option("pl", "print-licenses", false, "Output list of licenses as found in the files in the provided license directory"));
        options.addOption(new Option("c", COMPONENT_FILE_CLI, true, "Component file to check"));
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
        values.put(CONNECTOR_FILE_CLI, "licenses/connections/dwheeler.json");
        values.put("output", null);
        values.put("licenseDir", "licenses/json");
        values.put(POLICY_FILE_CLI, null);
        values.put("policy", null);
        values.put("expression", null);
        values.put("mode", execMode.PRINT_LICENSES);
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
                values.put("mode", execMode.PRINT_CONNECTIONS);
            }
            if (line.hasOption("debug-component-licenses")) {
                values.put("mode", execMode.PRINT_COMPONENT_LICENSES);
            }
            if (line.hasOption(CONNECTOR_FILE_CLI)) {
                Log.d(LOG_TAG, "Connector file: " + line.getOptionValue(CONNECTOR_FILE_CLI));
                values.put(CONNECTOR_FILE_CLI, line.getOptionValue(CONNECTOR_FILE_CLI));
            }
            if (line.hasOption("violation")) {
                Log.d(LOG_TAG, " Checking violations");
                values.put("mode", execMode.CHECK_VIOLATION);
            }
            if (line.hasOption(COMPONENT_FILE_CLI)) {
                values.put(COMPONENT_FILE_CLI, line.getOptionValue(COMPONENT_FILE_CLI));
                Log.d(LOG_TAG, " Component file: " + values.get(COMPONENT_FILE_CLI));
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
            if (line.hasOption("license-dir")) {
                values.put("licenseDir", line.getOptionValue("license-dir"));
                Log.d(LOG_TAG, " License dir: " + values.get("licenseDir"));
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
        } catch (LicenseConnector.LicenseConnectorException e) {
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
        } catch (LicenseConnector.LicenseConnectorException e) {
            e.printStackTrace();
        }

    }

}
