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

    public static class ComplianceAnswer {

        // License.spdx
        private Map<String, Map<Component, List<ComplianceAnswer>>> answers;
        private ListType color;

        @Override
        public String toString() {
            return "ComplianceAnswer{" +
                    "answers=" + answers +
                    '}';
        }

        public ListType color() {
            return color;
        }

        public void color(ListType color) {
            this.color = color;
        }

        public String toString(int indent) {
            StringBuilder sb = new StringBuilder();
            if (answers.entrySet().size() > 0 ) {
                for (Map.Entry<String, Map<Component, List<ComplianceAnswer>>> entry : answers.entrySet()) {

                    //              sb.append(indents(indent));
//                sb.append("[" + answers.entrySet().size() + "]\n");
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (((Map<Component, List<ComplianceAnswer>>) value).entrySet().size() > 0) {
                        sb.append(indents(indent));
                        sb.append("  | ");
                        //sb.append(key + " ( keyset: \"" + ((Map<Component, List<ComplianceAnswer>>) value).keySet().size() + "\")");
                        sb.append(key);
                        if (this.color()!=ListType.WHITE_LIST) {
                            sb.append(" (");
                            sb.append(this.color());
                            sb.append(")");
                       }
                        sb.append("\n");
                        for (Map.Entry<Component, List<ComplianceAnswer>> entry2 : ((Map<Component, List<ComplianceAnswer>>) value).entrySet()) {
                            sb.append(indents(indent));
                            sb.append("  +-- ");
                            sb.append(entry2.getKey().name());
                        /*    sb.append(" (licenses: ");
                            sb.append(entry2.getKey().licenses().size());
                            sb.append(" deps: ");
                            sb.append(entry2.getKey().dependencies().size());
                            sb.append("   answers:");
                            sb.append(entry2.getValue() == null ? 0 : entry2.getValue().size());
                          */
                            sb.append("\n");
                            if (entry2.getValue() != null) {
//                                sb.append(" TEST: " + entry2.getValue() + "\n");
                                for (ComplianceAnswer ca : entry2.getValue()) {
                                    if (ca != null) {
                                        //sb.append(indents(indent));
//                                        sb.append("    print sub " + ca.answers.size() + "\n");
                                        sb.append(ca.toString(indent + 2));
                                    } else {
                                        sb.append("====================================== 1 null\n");
                                    }
                                }
                            } else {
                                sb.append("====================================== 2 null\n");
                                //else { sb.append("====================================== null\n"); }
                            }
                        }
                    }
                }
            } else {
                sb.append(indents(indent));
                sb.append("  | OK");
                if (this.color()!=ListType.WHITE_LIST) {
                    sb.append(" (");
                    sb.append(this.color());
                    sb.append(")");
                }
                sb.append("\n");

            }
            return sb.toString();
        }

        public int paths(ListType color) {
            int count = 0;
            if (answers.entrySet().size() > 0 ) {
                for (Map.Entry<String, Map<Component, List<ComplianceAnswer>>> entry : answers.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (((Map<Component, List<ComplianceAnswer>>) value).entrySet().size() > 0) {
                        if (this.color()!=ListType.WHITE_LIST) {
                        }
                        for (Map.Entry<Component, List<ComplianceAnswer>> entry2 : ((Map<Component, List<ComplianceAnswer>>) value).entrySet()) {
                            if (entry2.getValue() != null) {
                                for (ComplianceAnswer ca : entry2.getValue()) {
                                    if (ca != null) {
                                        count += ca.paths(color);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                count++;
                if (this.color()!=ListType.WHITE_LIST) {
                }
            }
            return count;
        }

        public int grayPaths() {
            return paths(ListType.GRAY_LIST);
        }

        public static ComplianceAnswer okAnswerNoDeps() {
            return new ComplianceAnswer();
        }

        public static ComplianceAnswer okAnswerNoDepsGray() {
            return new ComplianceAnswer(ListType.GRAY_LIST);
        }

        public static ComplianceAnswer failAnswer; // initialized to null, keep it that way

        public ComplianceAnswer() {
            answers = new HashMap<>();
            color = ListType.WHITE_LIST;
        }

        public ComplianceAnswer(ListType color) {
            answers = new HashMap<>();
            this.color = color;
        }

        public Map<Component, List<ComplianceAnswer>> licenseMap(License license) {
            return licenseMap(license.spdx());
        }

        public Map<Component, List<ComplianceAnswer>> licenseMap(String spdx) {
            if (answers.get(spdx) == null) {
                answers.put(spdx, new HashMap<>());
            }
            return answers.get(spdx);
        }

        public List<ComplianceAnswer> answers(String spdx, Component component) {
            if (licenseMap(spdx).get(component) == null) {
                licenseMap(spdx).put(component, new ArrayList<>());
            }
            return licenseMap(spdx).get(component);
        }

    }

    /*
    public static ObligationState state(License license, String obligationName) {
        return license.obligations().get(obligationName).state();
    }
*/

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
        debug("aCanUse", "   new arraylist: NOT ALREADY" + "  " + a + "   " + b);
        return aCanUseBImpl(a, b, new ArrayList<>());
    }

    public static Report report(Component c, LicensePolicy policy) {
        Log.d(LOG_TAG, "reportViolations()    c: " + c.name());
        Report report = new Report(c);
        try {
            Log.level(Log.DEBUG);
            ComplianceAnswer answer = reportConcludeLate(c, policy, report, 0);
        } catch (ConclusionImpossibleException e) {
//            e.printStackTrace();
            // TOOO: errror log
        } catch (LicenseViolationException e) {
            //          e.printStackTrace();
            // TOOO: errror log
        }
        return report;
    }

    private static void addConcluded(Report report, License l, Component c) {
        c.concludedLicense(l);
        // ONly add conclusion report if we have concluded from more than 1 licenses
        if (c.licenses().size() > 1) {
            debug("addConcluded", "     adding conclusion of " + l);
            report.addLicenseConclusion(new LicenseConclusion(c, l));
        }
    }

    private static void handleDualLicensesNoDeps(Component c, LicensePolicy policy, Report report) throws ConclusionImpossibleException {
        debug("handleDualLicensesNoDeps", "      ----> ");

        // Sort the licenses in permissive order
        c.licenses().sort(MostPermissiveLicenseComparator.comparator);

        // loop through licenses for the component (no deps)
        for (License l : c.licenses()) {
            if (policy == null) {
                debug("handleDualLicensesNoDeps", "      ----  no policy: " + l.spdx());
                addConcluded(report, l, c);
            } else {
                if (policy.blackList().contains(l)) {
                    debug("handleDualLicensesNoDeps", "      ----  blacklisted: " + l.spdx());
                } else {
                    if (policy.grayList().contains(l)) {
                        report.addLicenseConcern(new PolicyConcern(c, l, ListType.GRAY_LIST));
                        debug("handleDualLicensesNoDeps", "      ----  graylisted: " + l.spdx());
                    } else {
                        debug("handleDualLicensesNoDeps", "      ----  not listed: " + l.spdx());
                    }
                    addConcluded(report, l, c);
                }
            }
            if (c.concludedLicense() != null) {
                debug("handleDualLicensesNoDeps", "      <---- concluded: " + c.concludedLicense().spdx() + " do nada");
                return;
            }
        }


        debug("handleDualLicensesNoDeps", "      <---- throwing exception since no license could be concluded ");
        report.addLicenseObligationViolation(new LicenseObligationViolation(c));
        throw new ConclusionImpossibleException("Could not conclude license for " + c.name(), c);
    }

    private static void debug(String method, String msg) {
        Log.d(method + "." + method, msg);
    }

    private static String indents(int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("  ");
        }
        return sb.toString();
    }

    private static void debug(String method, String msg, int indent) {
        Log.d(method + method, indents(indent) + msg);
    }


    private static boolean checkLicenseOnDependencies(License l, List<Component> components) {
        boolean licenseUsable = true;
        debug("checkLicenseOnDependencies", "    ----> " + l.spdx());
        for (Component d : components) {
            if (!aCanUseB(l, d.concludedLicense())) {
                licenseUsable = false;
            }
        }
        debug("checkLicenseOnDependencies", "    <---- " + l.spdx() + "   " + licenseUsable);
        return licenseUsable;
    }


    private static boolean subComponentCompatibleWith(Report report, Component component, License license, LicensePolicy policy, int indent) {
        debug("subComponentCompatibleWith", " -->   " + component.name() + " (compat with? " + license.spdx() + ")", indent);
        for (License l : component.licenses()) {
            debug("subComponentCompatibleWith", " ---   " + component.name() + "                                   l: " + l.spdx(), indent);
            // Blacklisted => continue with next
            //   debug("subComponentCompatibleWith", "  --   " + component.name() + "(" + license.spdx() + ")  check: " + l.spdx(), indent);
            if (policy != null && policy.blackList().contains(l.spdx())) {
                debug("subComponentCompatibleWith", "  --   " + component.name() + " (compat with? " + license.spdx() + ")  BLACKLISTED", indent);
                continue;
            }
            // Can't combine work => continue with next
            if (!aCanUseB(license, l)) {
                debug("subComponentCompatibleWith", "  --   " + component.name() + " (compat with? " + license.spdx() + ")  can not use: " + l.spdx(), indent);
                continue;
            }

            //debug("subComponentCompatibleWith", "  --   " + component.name() + "(" + license.spdx() + ")  moving on", indent);

            // So, l (License) :
            // * not black listed
            // * can be used by license (maybe null)

            boolean licenseCompliant = true;
            // if no deps, simply check this one with license
            if (component.dependencies().size() == 0) {
                debug("subComponentCompatibleWith", "  --   " + component.name() + " (compat with? " + license.spdx() + ")  no deps so concluded .... BUG", indent);
                addConcluded(report, l, component);
                return true;
            } else {
                // Check all sub components if l can be used with them
                for (Component c : component.dependencies()) {
                    debug("subComponentCompatibleWith", "  --   " + component.name() + "  ----- check " + c.name() + " compat with: " + l.spdx(), indent);
                    boolean canUse = subComponentCompatibleWith(report, c, l, policy, indent + 2);
//                    debug("subComponentCompatibleWith", "  --   " + component.name() + "(" + license.spdx() + ")  DEPS can use: " + l.spdx() + " : " + canUse, indent);
                    if (!canUse) {
                        // at least one component could not use l
                        // continue with next
                        debug("subComponentCompatibleWith", "  --   " + component.name() + "  ----- check " + c.name() + " NOT compat with: " + l.spdx() + " don't check anymore", indent);
                        clearConcluded(report, c);
                        licenseCompliant = false;
                        break;
                    }
                }
            }

            if (!licenseCompliant) {
                continue;
            }

            if (aCanUseB(license, l)) {
                debug("subComponentCompatibleWith", " <--   " + component.name() + " (compat with? " + license.spdx() + ")  true", indent);
                addConcluded(report, l, component);
                return true;
            }
        }

        debug("subComponentCompatibleWith", " <--   " + component.name() + " (" + license.spdx() + ")  false (last stand) ", indent);
        debug("subComponentCompatibleWith", " <--   VIOLATION " + component.name() + "  and license " + license.spdx(), indent);
        // TODO: violation?
        return false;
    }


    private static ComplianceAnswer subComponentCompatibleWith(Report report, Component component, License license, LicensePolicy policy, int indent, int dummy) {
        debug("subComponentCompatibleWith", " -->   " + component.name() + " (compat with? " + license.spdx() + ")", indent);
        ComplianceAnswer answer = new ComplianceAnswer();
        boolean componentCompliant = false;
        for (License l : component.licenses()) {
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
                continue;
            }

            //debug("subComponentCompatibleWith", "  --   " + component.name() + "(" + license.spdx() + ")  moving on", indent);

            // So, l (License) :
            // * not black listed
            // * can be used by license (maybe null)

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
                    answer.answers(l.spdx(),component).add(ComplianceAnswer.okAnswerNoDeps());
                }
            } else {
                // Check all sub components if l can be used with them
                for (Component c : component.dependencies()) {
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
                        debug("subComponentCompatibleWith", "  --   " + component.name() + "  ----- check " + c.name() + " IS compat with: " + l.spdx() + " keep checking ", indent);
                        answer.answers(l.spdx(), component).add(answerFromDep);
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

    private static void addToList(Map<String, List<Component>> map, String key, Component component) {
        List<Component> components = map.get(key);
        if (components == null) {
            map.put(key, new ArrayList<Component>());
            components = map.get(key);
        }
        components.add(component);
    }

    private static void clearConcluded(Report report, Component c) {
        c.invalidateConcludedLicense();
        for (Component d : c.dependencies()) {
            clearConcluded(report, d);
        }
    }



    public static void reportConcludeLate(Component c, LicensePolicy policy, Report report) throws ConclusionImpossibleException, LicenseViolationException {
        for (License l : c.licenses()) {
            // clearConcluded(report, c);
//            Log.d(LOG_TAG, " checkLicense: " + l );
//            debug("reportConcludeLate", " calling subComponentCompatibleWith   --   " + c.name() + " ( ----- compat with: " + l.spdx() );

            boolean licenseOk = subComponentCompatibleWith(report, c, l, policy, 2);
            Log.d(LOG_TAG, " reportConcludeLate: " + l + " ===> " + licenseOk);
            if (!licenseOk) {
                clearConcluded(report, c);
            } else {
                System.out.println("Yes, me happy!");
//                System.out.println(" report: " + ReportExporterFactory.getInstance().exporter(ReportExporterFactory.OutputFormat.TEXT).exportReport(report));
                System.out.println(" conclusions: " + ReportExporterFactory.getInstance().exporter(ReportExporterFactory.OutputFormat.TEXT).exportConclusions(report.conclusions));
                break;
            }
        }
    }



    public static ComplianceAnswer reportConcludeLate(Component c, LicensePolicy policy, Report report, int dummy) throws ConclusionImpossibleException, LicenseViolationException {
        for (License l : c.licenses()) {
            // clearConcluded(report, c);
//            Log.d(LOG_TAG, " checkLicense: " + l );
            debug("reportConcludeLate", " calling subComponentCompatibleWith   --   " + c.name() + " ( ----- compat with: " + l.spdx() );
            ComplianceAnswer answer = subComponentCompatibleWith(report, c, l, policy, 2, 0);
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
                System.out.println("answer:\n" + c.name());
                System.out.println(answer.toString(2));
                System.out.println("gray paths: " + answer.grayPaths());
            }
        }
        return null;
    }


    private static void reportConcludeFirst(Component c, LicensePolicy policy, Report report) throws ConclusionImpossibleException, LicenseViolationException {
        debug("report", "--->   c: " + c);

        // are we the last component or do we have dependency components?
        if (c.dependencies().size() == 0) {
            // we're the last component (i e has no deps)
            handleDualLicensesNoDeps(c, policy, report);
            // we should now have a license (otherwise exception passed to caller)
            debug("report", "<---   c: " + c + "  license: " + c.concludedLicense().spdx());
            return;
        } else {
            // we have dependency components
            // conclude license for all dependency components
            for (Component d : c.dependencies()) {
                //  report(d, policy, report);
            }
        }

        // We have dependency components (otherwise already returned)
        // and these should now have a concluded license (otherwise exception should have been thrown)
        // Check all licenses for this component if we can chose any to fit all dep components
        for (License l : c.licenses()) {

            // policy blacklists license, skip it
            if (policy != null && policy.blackList().contains(l)) {
                debug("report", "    --- blacklisted, skip: " + l);
                continue;
            }

            // check all dependency components if they ALL can use this license (l)
            if (checkLicenseOnDependencies(l, c.dependencies())) {
                // we can use license l
                // if more then one license in the component, report conclusion
                if (c.licenses().size() > 1) {
                    debug("report", "  --- license concluded " + l + "  for " + c + "  among dual licenses: " + c.licenses());
                    report.addLicenseConclusion(new LicenseConclusion(c, l));
                }
                debug("report", "  --- license concluded " + l + "  for " + c + " among one license: " + c.licenses());
                c.concludedLicense(l);
                break;
            }
        }

        // we have checked all licenses of this component, against all deps
        if (c.concludedLicense() == null) {
            report.addLicenseObligationViolation(new LicenseObligationViolation(c));
            debug("report", "<---   c: " + c + "  no concluded license for " + c.name() + " throw LicenseViolationException");
            throw new LicenseViolationException("Violation found for component " + c.name(), c);
        }

        // This component (with deps ... otherwise returned earlier) now has a concluded
        // license with no violation
        debug("report", "<---   c: " + c + "  license: " + c.concludedLicense().spdx() + "    from (" + c.licenses() + ")");
    }


    public static String multipeLicensesInformation(Component c) {
        if (c.singleLicensed()) {
            return "single";
        } else if (c.dualLicensed()) {
            return "dual";
        } else if (c.manyLicensed()) {
            return "many";
        }
        return "unknown";
    }

}
