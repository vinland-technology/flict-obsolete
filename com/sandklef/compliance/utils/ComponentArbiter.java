// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.arbiter.*;


public class ComponentArbiter {

    public static String LOG_TAG = ComponentArbiter.class.getSimpleName();

    public static void debug(String msg, int indent) {
        Log.d(LOG_TAG, indent(indent) + msg);
    }

    public static class InterimComponent {
        Component component;
        List<License> licenses; // one of the Component's List of licenses, so they should be AND:ed
        List<InterimComponent> dependencies;
        int id;

        private static int counter = 200;
        public static int nextId() {
            counter++;
            return counter;
        }


        public List<License> licenses() {
            return licenses;
        }

        public void lLicenses(List<License> licenses) {
            this.licenses = licenses;
        }

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
            ic.licenses = this.licenses;
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

        public List<InterimComponent> allDependenciesImpl() {
            List<InterimComponent> components = new ArrayList<>();
            for (InterimComponent ic : dependencies) {
                components.add(ic);
                components.addAll(ic.allDependenciesImpl());
            }
            return components;
        }

        private String toStringHelper(InterimComponent iComponent, String indent) {
            StringBuilder sb = new StringBuilder();
            sb.append(indent);
            sb.append(iComponent.component().name());
            sb.append("   ");
            sb.append(iComponent.licenses());
            for (InterimComponent d : iComponent.dependencies()) {
                sb.append("\n");
                sb.append(toStringHelper(d, indent + " |  " ));
            }
            return sb.toString();
        }

        @Override
        public String toString() {
//            return "{ \"" + component + "\", \"" + licenses + "\"  " + dependencies + "}";
            return toStringHelper(this, "  ");
        }
    }

    private static String indent(int indents) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indents; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }


    public static void printComponents(List<InterimComponent> components, int indent) {
        for (InterimComponent c : components) {
            debug("" + c, indent);
        }
    }

    public static String componentWithLicenseList(List<InterimComponent> components) {
        StringBuffer sb = new StringBuffer();
        for (InterimComponent c : components) {
            sb.append(c);
            sb.append("\n\n");
        }
        return sb.toString();
    }

    public static InterimComponent findById(int id, InterimComponent component) {
        if (id == component.id) {
            return component;
        }
        for (InterimComponent d : component.dependencies()) {
            InterimComponent ic = findById(id, d);
            if (ic != null) {
                return ic;
            }
        }
        return null;
    }


    public static ListType type(InterimComponent c, LicensePolicy policy) {

        ListType type = ListType.ALLOWED_LIST;

        Log.d(LOG_TAG, " type check: " + c.component().name());

        // If we find a denied license directly, return denied
        // if gray remember, it
        for (License l : c.licenses) {


            // if black, return now
            if (policy != null && policy.deniedList().contains(l)) {
                Log.d(LOG_TAG, " type check:   * denied");
                return ListType.DENIED_LIST;
            } else if (policy != null && policy.avoidList().contains(l)) {
                Log.d(LOG_TAG, " type check:   * gray");
                return ListType.AVOID_LIST;
            }
        }

        // Loop through all components and look for a denied or gray license
        for (InterimComponent d : c.dependencies()) {
            if (type(d, policy) == ListType.DENIED_LIST) {
                // if black, return now
                Log.d(LOG_TAG, " type check:   * deniged");
                return ListType.DENIED_LIST;
            } else if (type(d, policy) == ListType.AVOID_LIST) {
                // if gray, store - may be black so continue looking
                Log.d(LOG_TAG, " type check:   * gray");
                type = ListType.AVOID_LIST;
            }
        }

        return type;
    }


    public static List<InterimComponent> copies(Component c) throws LicenseExpressionException, IllegalLicenseExpression {
        int paths = c.paths();
        Log.d(LOG_TAG, "copies() " + c + "  paths: " + paths);
        Log.d(LOG_TAG, "copies() " + c);
        List<InterimComponent> components = new ArrayList<>();
        InterimComponent ic = new InterimComponent(c);
        for (int i = 0; i < paths; i++) {
            Log.d(LOG_TAG, " * copies() " + c.license() + "   interim: " + ic.component.license());
            components.add(ic.clone());
        }
        Log.d(LOG_TAG, "copies() " + components);
        return components;
    }


    public static void fillComponent(InterimComponent component, List<InterimComponent> components, AtomicInteger splitCount)
            throws LicenseExpressionException, IllegalLicenseExpression {

        // We know how many interim components we have in the list (components.size())
        // Each components license list laid out one by one - and repeated
        /*
                c1 : [ [a,b] , [c,d] ]                   (paths: 2*2*3=12
                    deps:
                        c11 : [ [e,f] , [g,h] ]          (paths: 2)
                        c12 : [ [i,j] , [k,l], [m,n] ]   (paths: 3)

                c1: [a,b]
                        c11 : [ [e,f] ]
                        c12 : [ [i,j] ]
                c1: [a,b]
                        c11 : [ [e,f] ]
                        c12 : [ [k,l] ]
                c1: [a,b]
                        c11 : [ [e,f] ]
                        c12 : [ [m,n] ]
                c1: [a,b]
                        c11 : [ [g,h] ]
                        c12 : [ [i,j] ]
                c1: [a,b]
                        c11 : [ [g,h] ]
                        c12 : [ [k,l] ]
                c1: [a,b]
                        c11 : [ [g,h] ]
                        c12 : [ [m,n] ]

                and the same for c1: [c, d], so

                [a,b] [e, f] [i,j]
                [a,b] [e, f] [k,l]
                [a,b] [e, f] [m,n]
                [a,b] [g, h] [i,j]
                [a,b] [g, h] [k,l]
                [a,b] [g, h] [m,n]
                and the same for c1: [c, d], so

         */

        debug("fill " + component.name() + "  license: " + component.component.license(), splitCount.get());

        int licenseCount = 0;

        int licenseIndex = 0;
        boolean splitIndexIncreased = false;

        // For each component in the list of (same) component
        for (int i = 0; i < components.size(); i++) {
            // Fetch the corresponding interim
            InterimComponent c = components.get(i);

            // Using id, find component to change
            InterimComponent componentToChange = findById(component.id, c);

            Log.d(LOG_TAG, "fillComponent(): " + component);
            Log.d(LOG_TAG, "fillComponent(): component license: " + component.component.license());

            // If 12 components and 2 license expressions; 6 by 6 (12/2)
            // If 12 components and 3 license expressions; 4 by 4 (12/3)
            int nrOfComponents = components.size();
            int thisComponentLicenseCount = componentToChange.component.licenseList().size();
            int timesPerLicense = nrOfComponents / thisComponentLicenseCount;
            // index as spread out evenly on all components
            int leIndex = i / timesPerLicense;
            // index as spread out evenly on all components with previous components in mind
            int realIndex = leIndex;//+ indexSplitOffset  ;

//            int steps = (int)( Math.pow(2, splitCount.get()));
            int steps = splitCount.get();

            int stepLimit = nrOfComponents / (thisComponentLicenseCount * steps);
            if (i % stepLimit == 0) {
                licenseIndex = (licenseIndex + 1 )% thisComponentLicenseCount;
            }


            componentToChange.licenses = componentToChange.component.licenseList().get(licenseIndex);
//            componentToChange.licenses = componentToChange.component.licenseList().get(licenseIndex%thisComponentLicenseCount);
            Log.d(LOG_TAG, "fillComponent()  component: " + component);
            Log.d(LOG_TAG, "fillComponent(): licenses:  " + component.component.license());
            Log.d(LOG_TAG, "fillComponent(): iLicense:   " + componentToChange.licenses);

//            List<InterimComponent> componentsToAdd = componentsFromLicenseExpression(le);

            debug("recurse: " + c.id + " == " + component.id, 0);

            // Keep licenseCount to see if we should split more times next round
            licenseCount = component.component().licenseList().size();
        }

        splitCount.set(splitCount.get() * licenseCount);

        // Do the same for all sub components (recursively)
        for (InterimComponent ic : component.dependencies()) {
            fillComponent(ic, components, splitCount);
        }
    }

    public static boolean compliant(InterimComponent ic, LicenseArbiter arbiter, int indent) throws IllegalLicenseExpression, LicenseCompatibility.LicenseConnectorException {
//        System.out.println("compliant()");
        debug("compliant:  " + ic.name() + " licenses: " + ic.licenses, indent);

        // For each license in ic
        // - for each dep
        // - - check if license can be used with deps license(s)
        for (License icLicense : ic.licenses) {
            for (InterimComponent d : ic.dependencies()) {
                if (!arbiter.aCanUseB(icLicense, d.licenses)) {
                    Log.i(LOG_TAG, " * " + icLicense + " cannot use " + d.licenses);
                    debug("compliant:  " + ic.name() + " licenses: " + ic.licenses + "   incompliant since: " + d.licenses, indent);
                    // TODO: thrown exception??
                    return false;
                }
            }
        }
        // Ok, still seems ok
        debug("compliant:  " + ic.name() + " licenses: " + ic.licenses + "   coninue checking", indent);

        // Check each dep against their deps
        for (InterimComponent d : ic.dependencies()) {
            if (!compliant(d, arbiter, indent + 2)) {
                Log.i(LOG_TAG, " * " + d + " is not compliant");
                debug(" license violation: " + d.name() + "(" + d.licenses + ")", indent + 2);
                // TODO: thrown exception??
                return false;
            }
        }

        //      System.out.println(" * " + ic + " is working");
        return true;
    }



    public static String componentsWithLicenses(Component c, LicensePolicy policy, LicenseArbiter arbiter) throws LicenseExpressionException, IllegalLicenseExpression, LicenseCompatibility.LicenseConnectorException {
        Report report = new Report(c, policy, arbiter);

        Log.d(LOG_TAG, " reportConcludeAllPaths: " + c);
        Log.d(LOG_TAG, " reportConcludeAllPaths: " + c.license());

        // Create a list of Components (out of c) with all combinations of licenses
        List<InterimComponent> components = copies(c);
        // Fill the licenses of the Component
        fillComponent(components.get(0), components, new AtomicInteger(1));

        return componentWithLicenseList(components);
    }

    public static Report reportConcludeAllPaths(LicenseArbiter arbiter, Component c, LicensePolicy policy) throws LicenseExpressionException, IllegalLicenseExpression, LicenseCompatibility.LicenseConnectorException {
        Report report = new Report(c, policy, arbiter);

        Log.d(LOG_TAG, " reportConcludeAllPaths: " + c);
        Log.d(LOG_TAG, " reportConcludeAllPaths: " + c.license());

        // Create a list of Components (out of c) with all combinations of licenses
        List<InterimComponent> components = copies(c);
        // Fill the licenses of the Component
        fillComponent(components.get(0), components, new AtomicInteger(1));

        printComponents(components, 2);

        // for each InterimComponent
        // - check if it (with its licenses) is compliant with dependency components
        for (InterimComponent ic : components) {
            boolean compliant = compliant(ic, arbiter, 2);
            debug("compliant:  " + ic.name() + " => " + compliant, 2);
            ListType type = type(ic, policy);
            report.addComponentResult(new Report.ComponentResult(type, ic, compliant));
        }

        return report;
    }

    public static Report report(LicenseArbiter arbiter, Component c, LicensePolicy policy) throws LicenseExpressionException, IllegalLicenseExpression, LicenseCompatibility.LicenseConnectorException {
        Log.d(LOG_TAG, "reportViolations()    c: " + c.name());
        Report report = reportConcludeAllPaths(arbiter, c, policy);

        //System.out.println("report: " + report);

        return report;
    }

    private static void addConcluded(Report report, License l, Component c) {
//        c.concludedLicense(l);
        // ONly add conclusion report if we have concluded from more than 1 licenses

    }


}
