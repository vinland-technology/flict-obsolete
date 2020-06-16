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

        public Component component() {
            return component;
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

/*
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

*/

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
        int paths = c.paths();
        List<InterimComponent> components = new ArrayList<>();
        InterimComponent ic = new InterimComponent(c);
        for (int i=0; i<paths; i++) {
            components.add(ic.clone());
        }
        return components;
    }


    public static void fillComponent(InterimComponent component, List<InterimComponent> components, int indent) throws LicenseExpressionException, IllegalLicenseExpression {
        debug("fill " + component.name() + "   license: " + component.license, indent);

        // For each component in the list of (same) component
        for (int i=0 ; i<components.size(); i++) {
            // Fetch the corresponding interim
            InterimComponent c = components.get(i);
            // Using id, find component to change
            InterimComponent componentToChange = findById(component.id, c);

            // TODO:  FIX THIS - IT MUST BE THERE
            LicenseExpression le = componentToChange.component.licenseExpression();
    //        List<InterimComponent> componentsToAdd = componentsFromLicenseExpression(le);

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

    public static Report reportConcludeAllPaths(Component c, LicensePolicy policy) throws LicenseExpressionException, IllegalLicenseExpression {
        Report report = new Report(c,policy);

        // Create a list of Components (out of c) with all combinations of licenses
        List<InterimComponent> components = copies(c);
        // Fill the licenses of the Component
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

    public static Report report(Component c, LicensePolicy policy) throws LicenseExpressionException, IllegalLicenseExpression {
        Log.d(LOG_TAG, "reportViolations()    c: " + c.name());
        Report report = reportConcludeAllPaths(c, policy);
        return report;
    }

    private static void addConcluded(Report report, License l, Component c) {
//        c.concludedLicense(l);
        // ONly add conclusion report if we have concluded from more than 1 licenses

    }


}
