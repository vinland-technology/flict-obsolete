// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.utils;

import java.awt.event.ComponentListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.exporter.ReportExporterFactory;
import com.sandklef.compliance.json.JsonLicenseConnectionsParser;

import static com.sandklef.compliance.utils.Log.debug;


public class LicenseArbiter {

    public static String LOG_TAG = LicenseArbiter.class.getSimpleName();

    static {
        JsonLicenseConnectionsParser jcp = new JsonLicenseConnectionsParser();
        try {
            // TODO: the connector file to use should be given as an arg somehow
            Map<String, LicenseConnector> licenseConnectors = jcp.readLicenseConnection("licenses/connections/dwheeler.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void debug(String msg, int indent) {
        System.out.println(indent(indent)+msg);
    }
    public static String indent(int indents) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<indents; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    private static int counter = 200;
    public static int nextId() {
        counter++;
        return counter;
    }

    public static class InterimComponent  {
        Component component;
        License license;
        List<InterimComponent> dependencies;
        int id;

        public InterimComponent(Component component) {
            this.component = component;
            this.id = nextId();
            this.dependencies = new ArrayList<>();
            for (Component d : component.dependencies()) {
                this.dependencies.add(new InterimComponent(d));
            }
        }

        // A typical Skansholm constructor
        public InterimComponent() {
        }

        public InterimComponent clone() {
            InterimComponent ic = new InterimComponent();
            ic.component = this.component;
            ic.id = this.id;
            ic.license = this.license;
            ic.dependencies = new ArrayList<>();
            for (InterimComponent id : this.dependencies()) {
                ic.dependencies.add(id.clone());
            }
            return ic;
        }

        public List<InterimComponent> dependencies() {
            return dependencies;
        }

        public void addDependency(InterimComponent c) {
            dependencies.add(c);
        }

        public String name() {
            return component.name();
        }

        @Override
        public String toString() {
            return "{ \"" + component + "\", \"" + license.spdx() + "\"  " + id + " " + dependencies + "}";
        }
    }


    public static void printComponents(List<InterimComponent> components, int indent) {
        for (InterimComponent c : components) {
            debug(""+c, indent);
        }
    }

    public static InterimComponent findById(int id, InterimComponent component) {
        if (id ==component.id) {
            return component;
        }
        for (InterimComponent d : component.dependencies()) {
            InterimComponent ic = findById(id, d);
            if (ic!=null) {
                return ic;
            }
        }
        return null;
    }


    public static int countNodes(Component c) {
        if (c.dependencies().size()==0) {
            debug(" count: " + c.name(), 0);
            return 1;
        }
        int sum = 0;
        for (Component d : c.dependencies()) {
            sum += countNodes(d);
        }
        return sum;
    }

    public static int countPaths(Component c) {
        if (c.dependencies().size()==0) {
            debug(" count: " + c.name(), 0);
            return c.licenses().size();
        }
        int sum = 1;
        for (Component d : c.dependencies()) {
            sum *= countPaths(d);
        }
        return sum*c.licenses().size();
    }


    public static ListType color(InterimComponent c, LicensePolicy policy) {
        // if black, return now
        if (policy!=null && policy.blackList().contains(c.license.spdx())) {
            return ListType.BLACK_LIST;
        }
        ListType color = ListType.WHITE_LIST;
        for (InterimComponent d : c.dependencies()) {
            if (color(d, policy) == ListType.BLACK_LIST) {
                // if black, return now
                return ListType.BLACK_LIST;
            } else if (color(d, policy) == ListType.GRAY_LIST) {
                // if gray, store - may be black so continue looking
                color = ListType.GRAY_LIST;
            }
        }
        return color;
    }



    public static List<InterimComponent> copies(Component c) {
        int paths = countPaths(c);
        List<InterimComponent> components = new ArrayList<>();
        InterimComponent ic = new InterimComponent(c);
        for (int i=0; i<paths; i++) {
            components.add(ic.clone());
        }
        return components;
    }


    public static void fillComponent(InterimComponent component, List<InterimComponent> components, int indent) {
        debug("fill " + component.name() + "   license: " + component.license, indent);
        for (int i=0 ; i<components.size(); i++) {
            InterimComponent c = components.get(i);
            InterimComponent componentToChange = findById(component.id, c);
            List<License> licenses = componentToChange.component.licenses();
            debug("edit: " + componentToChange.component.name() + " i: " + i + "   => " + licenses.get(i%licenses.size()), indent+2);
            componentToChange.license = licenses.get(i%licenses.size());

            debug("recurse: " + c.id + " == " + component.id, 0);
        }
        for (InterimComponent ic : component.dependencies()) {
            fillComponent(ic, components, indent+2);
        }
    }

    public static boolean compliant(License license, InterimComponent ic, int indent) {
      //  debug("compliant:  " + ic.name() + " license: " + ic.license, indent);

        if ( ! aCanUseB(license, ic.license)) {
            // TODO: thrown exception??
            return false;
        }

        for (InterimComponent d : ic.dependencies()) {
            if (!compliant(ic.license, d, indent+2)) {
                debug(" license violation: " + ic.name() + "(" + ic.license + ") with " + d.name() + " (" + d.license +")", indent+2);
                // TODO: thrown exception??
                return false;
            }
        }

        return true;
    }




    public static boolean aCanUseB(License a, License b) {
        //Log.level(Log.DEBUG);
        Log.d(LOG_TAG, "aCanUseB " + a + " " + b);
        if (b == null) {
            // violation in "lower" components
            return false;
        }
        Log.d(LOG_TAG, "aCanUseB spdx: " + a.spdx() + " " + b.spdx());
        Log.d(LOG_TAG, "aCanUseB conns " + LicenseStore.getInstance().connectors());
        Log.d(LOG_TAG, "aCanUseB a " + LicenseStore.getInstance().connectors().get(a.spdx()));
        Log.d(LOG_TAG, "aCanUseB b " + LicenseStore.getInstance().connectors().get(b.spdx()));
        return aCanUseB(LicenseStore.getInstance().connectors().get(a.spdx()),
                LicenseStore.getInstance().connectors().get((b.spdx())));
    }

    private static boolean aCanUseBImpl(LicenseConnector a, LicenseConnector b, List<LicenseConnector> visited) {
        Log.d(LOG_TAG, "   ---> check lic: " + a + " and " + b + "    { " + visited + " }");

        // Check if we've visited this connector already. If so, false
        Log.d(LOG_TAG, " ***************** ALREADY BEEN IN " + b.license() + " ******** " + visited.contains(b));
        if (visited.contains(b)) {
            // already checked b
            //           Log.d(LOG_TAG, "\n\n ***************** ALREADY BEEN IN " + b.license().spdx() + " ********\n\n\n");
            return false;
        } else if (b == null) {
            // probably a violation in "lower" components
            return false;
        } else {
            // not visited, mark it as visited
            visited.add(b);
        }

        if (a.license().spdx().equals(b.license().spdx())) {
            return true;
        }


        if (a.canUse().contains(b)) {
            return true;
        }
        //      System.out.println(" Try 1 <--- : " + a.license().spdxTag() + " " + a.canUse() + " contains " + b.license());

        // Loop through all b's canBeUsed licenses
        for (LicenseConnector l : b.canBeUsedBy()) {

            if (l.license().spdx().equals(a.license().spdx())) {
                return true;
            }

            if (aCanUseBImpl(a, l, visited)) {
                //  System.out.println(" Try 3 <--- : " + a.license().spdx() + "   CHECK: " + l.license().spdx());
                return true;
            }
        }

        return false;
    }

    private static boolean aCanUseB(LicenseConnector a, LicenseConnector b) {
        return aCanUseBImpl(a, b, new ArrayList<>());
    }

    public static Report reportConcludeAllPaths(Component c, LicensePolicy policy) {
        Report report = new Report(c,policy);

        List<InterimComponent> components = copies(c);
        fillComponent(components.get(0), components, 0);
        printComponents(components, 2);
        for (InterimComponent d : components) {
            boolean compliant = compliant(components.get(0).license, d, 2);
            debug("compliant:  " + d.name() + " => " + compliant, 2);
            ListType color = color(d, policy);
            report.addComponentResult(new Report.ComponentResult(color, d, compliant));
        }

        return report;
    }

    public static Report report(Component c, LicensePolicy policy) {
        Log.d(LOG_TAG, "reportViolations()    c: " + c.name());
        Report report = reportConcludeAllPaths(c, policy);
        return report;
    }

    private static void addConcluded(Report report, License l, Component c) {
        c.concludedLicense(l);
        // ONly add conclusion report if we have concluded from more than 1 licenses

    }

/*
    private static ComplianceAnswer subComponentCompatibleWith(Report report, Component component, License license, LicensePolicy policy, int indent, int dummy) {
        debug("subComponentCompatibleWith", " -->   " + component.name() + " (compat with? " + license.spdx() + ")", indent);
        ComplianceAnswer answer = new ComplianceAnswer();
        boolean componentCompliant = false;
        for (License l : component.licenses()) {
//            System.out.println("sub-Check " + component.name() + "  under license: " + l.spdx());

            debug("subComponentCompatibleWith", " ---   " + component.name() + " " + license.spdx() + " try l: " + l.spdx(), indent);
            // Blacklisted => continue with next
            //   debug("subComponentCompatibleWith", "  --   " + component.name() + "(" + license.spdx() + ")  check: " + l.spdx(), indent);
            if (policy != null && policy.blackList().contains(l)) {
                debug("subComponentCompatibleWith", "  --    policy: " + policy.grayList() + " contains: " + l.spdx() + " => BLACK", indent);
                continue;
            }
            // Can't combine work => continue with next
            if (!aCanUseB(license, l)) {
                debug("subComponentCompatibleWith", "  --   " + component.name() + " (compat with? " + license.spdx() + ")  can not use: " + l.spdx() + "  skipping", indent);
//                System.out.println(" ignore: " + license.spdx() + " " + l.spdx()) ;
                continue;
            }

            //debug("subComponentCompatibleWith", "  --   " + component.name() + "(" + license.spdx() + ")  moving on", indent);

            // So, License l:
            // * not black listed
            // * can be used by license (maybe null) supplied by caller
            boolean licenseCompliant = true;
            // if no deps, simply check this one with license
            if (component.dependencies().size() == 0) {
                // add to this license,
                debug("subComponentCompatibleWith", "  --   " + component.name() + " (compat with? " + license.spdx() + ") " + l.spdx() + "   no deps, so concluded .... BUG", indent);
                debug("subComponentCompatibleWith", "  --    policy: " + policy + " contains: " + l.spdx() + " => " +
                        (policy==null?false:policy.grayList().contains(l)), indent);
                if (policy!=null && policy.grayList().contains(l)) {

                    debug("subComponentCompatibleWith", "  --    policy: " + policy.grayList() + " contains: " + l.spdx() + " => GRAY", indent);
                    answer.answers(l.spdx(),component).add(ComplianceAnswer.okAnswerNoDepsGray());
                } else {
                    debug("subComponentCompatibleWith", "  --    policy: " + policy + " contains: " + l.spdx() + " => WHITE", indent);
                    debug("subComponentCompatibleWith", "  --    WHITE: " + component + " " + answer.answers(l.spdx(),component));
                    answer.answers(l.spdx(),component).add(ComplianceAnswer.okAnswerNoDeps());
                    debug("subComponentCompatibleWith", "  --    WHITE: " + component + " " + answer.answers(l.spdx(),component).size());
                }
            } else {
                // Check all sub components if l can be used with them
                for (Component c : component.dependencies()) {
                    System.out.println("Checking component: " + component.name() + " with " + c.name());
                    debug("subComponentCompatibleWith", "  --   " + component.name() + "  ----- check " + c.name() + " compat with: " + l.spdx(), indent);
                    ComplianceAnswer answerFromDep = subComponentCompatibleWith(report, c, l, policy, indent + 2, 0);
                    debug("subComponentCompatibleWith", "  --   " + component.name() + "  ----- DEBUG: " + answerFromDep, indent);
//                    debug("subComponentCompatibleWith", "  --   " + component.name() + "(" + license.spdx() + ")  DEPS can use: " + l.spdx() + " : " + canUse, indent);
                    if (answerFromDep == null) {
                        // at least one component could not use l
                        // continue with next
                        debug("subComponentCompatibleWith", "  --   " + component.name() + "  ----- check " + c.name() + " NOT compat with: " + l.spdx() + " don't check anymore", indent);
                        clearConcluded(report, c);
                        licenseCompliant = false;
                        break;
                    } else {
                        System.out.println("Checking component: add to (" +  l.spdx() + ", " + component.name() + ") with " + c.name());
                        debug("subComponentCompatibleWith", "  --   " + component.name() + "  ----- check " + c.name() + " IS compat with: " + l.spdx() + " keep checking ", indent);
                        debug("subComponentCompatibleWith", "  --    DEP: " + component + " " + answer.answers(l.spdx(),component).size());
                        answer.answers(l.spdx(), component).add(answerFromDep);
                        debug("subComponentCompatibleWith", "  --    DEP: " + component + " " + answer.answers(l.spdx(),component).size());
                    }
                }
            }



            if (!licenseCompliant) {
                debug("subComponentCompatibleWith", " ----- REMOVE for " + component + "  l: " + l.spdx(), indent);
                answer.licenseMap(l).remove(component);
                continue;
            }

            if (licenseCompliant) {
                componentCompliant = true; // at least one license worked, so entire component is compliant with asked license
            }

            if (aCanUseB(license, l)) {
                debug("subComponentCompatibleWith", " <--   " + component.name() + " (compat with? " + license.spdx() + ")  true", indent);
                addConcluded(report, l, component);
            }
        }

        if (!componentCompliant) {
            answer = ComplianceAnswer.failAnswer;
        }
        debug("subComponentCompatibleWith",
                "  --   DEBUG ANSWER " +
                        component.name() +
                        "  for license " +
                        license.spdx() +
                        " :: " +
                    answer,
                indent);

        debug("subComponentCompatibleWith", " <--   " + component.name() + " (" + license.spdx() + ")  false (last stand) ", indent);
        debug("subComponentCompatibleWith", " <--   VIOLATION " + component.name() + "  and license " + license.spdx(), indent);
        return answer;
    }


    private static void clearConcluded(Report report, Component c) {
        c.invalidateConcludedLicense();
        for (Component d : c.dependencies()) {
            clearConcluded(report, d);
        }
    }


    public static void buildList(Component c) {
        buildListHelper(new ArrayList<Component>(), c, c.licenses().get(0));
    }

    public static void buildListHelper(List<Component> componentMutations, Component c, License topLicense) {
        for (License l : c.licenses()) {
            for (Component d : c.dependencies()) {
                System.out.println("  \"" + c.name() + "\" (" + l.spdx() + ")  " + d.name());
            }
        }
    }

    private static Report reportConcludeLate(Component c, LicensePolicy policy)  {
        Report report = new Report(c, policy);

        // TODO: not needed to do per license!!!
        for (License l : c.licenses()) {
            System.out.println("Check " + c.name() + "  under license: " + l.spdx());
            // clearConcluded(report, c);
            Log.level(Log.DEBUG);
//            Log.d(LOG_TAG, " checkLicense: " + l );
            debug("reportConcludeLate", " calling subComponentCompatibleWith   --   " + c.name() + " ( ----- compat with: " + l.spdx() );
            ComplianceAnswer answer = subComponentCompatibleWith(report, c, l, policy, 2, 0);
            report.addAnswer(answer);
            Log.d(LOG_TAG, " reportConcludeLate: " + l + " ===> " + answer);
            Log.d(LOG_TAG, " reportConcludeLate: " + l + " ===> " + answer);
            if (answer == null) {
                System.out.println("Yes, me worry!");
                System.out.println("answer: null\n");
                clearConcluded(report, c);
            } else {
                System.out.println("Yes, me happy!");
//                System.out.println(" report: " + ReportExporterFactory.getInstance().exporter(ReportExporterFactory.OutputFormat.TEXT).exportReport(report));
//                System.out.println(" conclusions: " + ReportExporterFactory.getInstance().exporter(ReportExporterFactory.OutputFormat.TEXT).exportConclusions(report.conclusions));
                System.out.println("answer:\n");
                System.out.println(answer.toString(0));
                System.out.println("gray paths: " + answer.grayPaths());
            }
        }
        return report;
    }


*/

}
