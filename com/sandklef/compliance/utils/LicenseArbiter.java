// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sandklef.compliance.domain.*;
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

    /*
    public static ObligationState state(License license, String obligationName) {
        return license.obligations().get(obligationName).state();
    }
*/

    public static boolean aCanUseB(License a, License b) {
        //Log.level(Log.DEBUG);
        Log.d(LOG_TAG, "aCanUseB " + a + " " +b);
        if (b==null) {
            // violation in "lower" components
            return false;
        }
        Log.d(LOG_TAG, "aCanUseB " + a.spdx() + " " +b.spdx());
        Log.d(LOG_TAG, "aCanUseB " + LicenseStore.getInstance().connectors());
        Log.d(LOG_TAG, "aCanUseB c " + LicenseStore.getInstance().connectors().get(a.spdx()));
        Log.d(LOG_TAG, "aCanUseB c " + LicenseStore.getInstance().connectors().get(b.spdx()));
        return aCanUseB(LicenseStore.getInstance().connectors().get(a.spdx()),
                LicenseStore.getInstance().connectors().get((b.spdx())));
    }

    private static boolean aCanUseBImpl(LicenseConnector a, LicenseConnector b, List<LicenseConnector> visited) {
        Log.d(LOG_TAG, "   ---> check lic: " + a + " and " + b + "    { " + visited + " }");

        // Check if we've visited this connector already. If so, false
        if (visited.contains(b)) {
            // already checked b
            Log.d(LOG_TAG, "\n\n ***************** ALREADY BEEN IN " + b.license().spdx() + " ********\n\n\n");
            return false;
        } else if (b==null) {
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

/*
    public static boolean aUsesB(Component c, Report report, License user, License usee) throws LicenseViolationException, NoLicenseException {
        ///System.out.println("****: " + c.name() + " " + usee);


        if (usee == null) {
            Log.d(LOG_TAG, "aUsesB(" + user + ", " + usee + ", ...)  ===>  false");

            throw new NoLicenseException("Used licenses unknown ", user, usee);
        }
        Log.d(LOG_TAG, "aUsesB(" + user + ", " + usee + ", ...) ");
        Log.d(LOG_TAG, "aUsesB(" + user.spdxTag() + ", " + usee.spdxTag() + ", ...)");

        if (user.spdxTag().equals(usee.spdxTag())) {
          Log.d(LOG_TAG, "Same license: true");
          return true;
        }
        Log.d(LOG_TAG, "Licenses differ");

        // Below it will be assumed that the licenses differ

      //  System.out.println(" **************************** : " + c.name() + " " + usee.spdxTag() + "  => " + user.obligations().get(Obligation.SUBLICENSING_ALLOWED_NAME).state() + "????");
        // If we can sublicense (e.g A(MIT) using B(GPLv2) turns A into GPLv2)
        if (user.obligations().get(Obligation.SUBLICENSING_ALLOWED_NAME).state() ==
                ObligationState.TRUE) {
          //   System.out.println("Liverpool: " + c.name() + " " + usee.spdxTag() + "  => " + user.obligations().get(Obligation.SUBLICENSING_ALLOWED_NAME).state() + " DOING IT");
          Log.d(LOG_TAG, "SUBLICENSING ");

          //          return true;
        }

        // if not copylefted - ok to use
        if (!usee.isCopyleft()) {
            Log.d(LOG_TAG, user.spdxTag() + " using " + usee.spdxTag() + " : non copyleft so OK, no possible violation");
            return true;
        }

        // If usee is copylefted AND user NOT copylefted  => violation
        if (usee.isCopyleft() && (!user.isCopyleft())) {
       //     System.out.println(" UH UH UH UH: " + c.name() + "  " + user + " using " + usee);
            Log.d(LOG_TAG, user.spdxTag() + " using " + usee.spdxTag() + " : possible violation detected");
            throw new LicenseViolationException(user.spdxTag() + " can not link/use " + usee.spdxTag(), user, usee);
        }

        // TODO: If usee is copylefted AND user copylefted  => check gpl etc
        

        Log.d(LOG_TAG, user.spdxTag() + " using " + usee.spdxTag() + " : no possible violation detected");
        return true;
    }

    public static boolean canAUseB(Component c, Report report, License user, License usee) {
        try {
         //   System.out.println("*** 1" + c + " " + user + " " + usee);
            return aUsesB(c, report, user, usee);
        } catch (LicenseViolationException | NoLicenseException e) {
            Log.d(LOG_TAG, "Exception");
            Log.d(LOG_TAG, "    message: " + e.getMessage());
//            Log.d(LOG_TAG, "    user:    (" + e.user.spdxTag() + ")");
            //          Log.d(LOG_TAG, "    usee:    (" + e.usee.spdxTag() + ")");
            return false;
        }
    }
*/

    public static Report report(Component c, LicensePolicy policy) {
        Log.d(LOG_TAG, "reportViolations()    c: " + c.name());
        Report report = new Report(c);
        report(c, policy, report);
        return report;
    }

    private static void addConcluded(Report report, License l, Component c) {
        c.concludedLicense(l);
        // ONly add conclusion report if we have concluded from more than 1 licenses
        if (c.licenses().size() > 1) {
            Log.d(LOG_TAG, " concluded license for " + c.name() + " is " + l + "  # licenses: " + c.licenses().size());
            report.addLicenseConclusion(new LicenseConclusion(c, l));
        }
    }

    private static void handleDualLicensesNoDeps(Component c, LicensePolicy policy, Report report) {
        // Sort the licenses in permissive order
        c.licenses().sort(MostPermissiveLicenseComparator.comparator);
        for (License l : c.licenses()) {
            Log.d(LOG_TAG, c.name() + ": has no deps, try concluding license: " + l);
            if (policy != null && policy.blackList().contains(l)) {
                Log.d(LOG_TAG, "Black colored license found for " + c.name() + ", can't conclude it: " + l + " sorry");
            } else if (policy != null && policy.grayList().contains(l)) {
                Log.d(LOG_TAG, "Gray colored license found for " + c.name() + " : " + l);
                Log.d(LOG_TAG, " concerned license for " + c.name() + " is " + l);
                report.addLicenseConcern(new PolicyConcern(c, l, ListType.GRAY_LIST));
                // TODO: add in some kind of list of gray yet possible licenses instead of marking as concluded?
                addConcluded(report, l, c);
                return;
            } else {
                Log.d(LOG_TAG, "White colored license found for " + c.name() + ": " + l);
                addConcluded(report, l, c);
                return ;
            }
        }
    }

    private static void handleManyLicensesNoDeps(Component c, LicensePolicy policy, Report report) {
        // Sort the licenses in least permissive order
        // The first one is hopefully something we can conclude
        c.licenses().sort(LeastPermissiveLicenseComparator.comparator);
        List<License> blackListed = new ArrayList<>();
        //        System.out.println("handleManyLicensesNoDeps   black " + policy + "    licenses: " + c.licenses());
        for (License l : c.licenses()) {
          //            System.out.println("handleManyLicensesNoDeps   black " + policy + "    license: " + l);
          //System.out.println("handleManyLicensesNoDeps " + l + " " +c.dualLicensed() + "  " + policy.blackList() + "--------------------------------");
            Log.d(LOG_TAG, c.name() + ": has no deps, try concluding license: " + l);
            if (policy != null && policy.blackList().contains(l)) {
              //    System.out.println("handleManyLicensesNoDeps " + l + " " +c.dualLicensed() + " BLACK FOUND +++++++++++++++++");
                Log.d(LOG_TAG, "Black colored license found for " + c.name() + ", can't exclude it: " + l + " sorry");
                blackListed.add(l);
                //                System.out.println("handleManyLicensesNoDeps " + l + " " +c.dualLicensed() + " BLACK FOUND +++++++++++++++++ " + blackListed.size());
            } else if (policy != null && policy.grayList().contains(l)) {
                Log.d(LOG_TAG, "Gray colored license found for " + c.name() + " : " + l);
                Log.d(LOG_TAG, " concerned license for " + c.name() + " is " + l);
                report.addLicenseConcern(new PolicyConcern(c, l, ListType.GRAY_LIST));
                break;
            } else {
                Log.d(LOG_TAG, "White colored license found for " + c.name() + ": " + l);
                break ;
            }
        }

        //        System.out.println("handleManyLicensesNoDeps any BLACK FOUND +++++++++++++++++ " + blackListed.size());
        if (blackListed.size()>0) {
          //  System.out.println("handleManyLicensesNoDeps   black " + policy + "    licenses: " + c.licenses() + "REPORING VIOLATION ++++++++++++++++++++++++++++");
            for (License l : blackListed) {
              //    System.out.println(" ADDING POLICY VIOLATION FOR " + c + "   AND LICENSE: " + l.spdxTag());
              report.addPolicyViolation(new PolicyViolation(c, l));
            }
        }
    }

    private static void report(Component c, LicensePolicy policy, Report report) {
        Log.d(LOG_TAG, "report() component: " + c.name() + " violation: " + c.name() + "   viols: " + report.violations.size());

        // second - return true if no deps
        if (c.dependencies().size() == 0) {
          //            System.out.println("Hurrrweopwqe   --------===|||||||||||||||||||||||||||||||||||||<  " + c.name());
            if (c.dualLicensed()) {
              //    System.out.println("Hurrrweopwqe   --------===|||||||||||||||||||||||||||||||||||||<  " + c.name() + " dual");
                handleDualLicensesNoDeps(c, policy, report);
            } else if (c.manyLicensed()) {
              //System.out.println("Hurrrweopwqe   --------===|||||||||||||||||||||||||||||||||||||<  " + c.name() + " many");
                handleManyLicensesNoDeps(c,policy, report);
                //System.out.println("Hurrrweopwqe   --------===| " + c.concludedLicense());
            } else if (c.singleLicensed()){
              //System.out.println("Hurrrweopwqe   --------===|||||||||||||||||||||||||||||||||||||<  " + c.name() + " single");
                License l = c.licenses().get(0);
                if (policy != null && policy.blackList().contains(l)) {
                  //    System.out.println("single license and no deps BLACK FOUND +++++++++++++++++");
                    report.addPolicyViolation(new PolicyViolation(c, l));
                } else if (policy != null && policy.grayList().contains(l)) {
                  //System.out.println("single license and no deps GRAY FOUND +++++++++++++++++");
                    report.addLicenseConcern(new PolicyConcern(c, l, ListType.GRAY_LIST));
                    addConcluded(report, l, c);
                } else {
                    Log.d(LOG_TAG, "White colored license found for " + c.name() + ": " + l);
                    c.concludedLicense(l);
                }
            }
            Log.d(LOG_TAG, " License for " + c.name() + ": " + c.concludedLicense() + "  ... is this ok");
            if (c.concludedLicense() == null) {
                report.addLicenseObligationViolation(new LicenseObligationViolation(c));
            }
            return;
        } else {
            Log.d(LOG_TAG, " * checking dependencies for : " + c + "   nr: " + c.dependencies().size());
            for (Component d : c.dependencies()) {
                Log.d(LOG_TAG, " * checking component: " + d);
                report(d, policy, report);
            }
        }

        // For each of the current component's licenses
        // -- for each of the dependency component's concluded license
        // ------ check if ok
     //   boolean allCleared = true;

        // List for all licenses that can be used - we will choose which one later on
        List<License> myCheckedLicenses = new ArrayList<>();
        List<Component> violatedComponents = new ArrayList<>();
        for (License l : c.licenses()) {

            boolean violationFound = false;
//            allCleared = false;
            if (policy != null && policy.blackList().contains(l)) {
                continue;
            }
            Log.d(LOG_TAG, "    can " + c.name() + " with: " + l.spdx() + " in components: " + c.dependencies().size());
            for (Component d : c.dependencies()) {
                Log.d(LOG_TAG, c.name() + " myCheckedLicenses: " + myCheckedLicenses + " HESA");

                Log.d(LOG_TAG, "    can " + c.name() + " (" + l.spdx() + ")    use: " + d.name() + "(" + d.concludedLicense() + ")");
        //        try {
               //     System.out.println("*** 1" + c + " " + l + " " + d + "   concluded: " + d.concludedLicense());
                //aUsesB(d, report, l, d.concludedLicense());
                  if (aCanUseB(l, d.concludedLicense())) {
                      // So, the license l can be used when using d
                      Log.d(LOG_TAG, "    can " + c.name() + " (" + l.spdx() + ")    use: " + d.name() + "(" + d.concludedLicense() + ") :  OK choice  HESA");
                      myCheckedLicenses.add(l);
                      violatedComponents.remove(d);
                      Log.d(LOG_TAG, "    myCheckedLicenses for " + c.name() + "  added " + l.spdx() + "  to " + myCheckedLicenses);
                  } else {
                      violationFound = true;
                      violatedComponents.add(d);
                      Log.d(LOG_TAG, "    can " + c.name() + " (" + l.spdx() + ")    use: " + d.name() + "(" + d.concludedLicense() + ")?  :  FAIL:   HESA");

                  }
          /*      } catch (LicenseViolationException e) {
                    violationFound = true;
                    violatedComponents.add(d);
                    Log.d(LOG_TAG, "    can " + c.name() + " (" + l.spdx() + ")    use: " + d.name() + "(" + d.concludedLicense() + ")?  :  FAIL: " + e.getMessage() + "  HESA");
                 //   allCleared = false;
                } catch (NoLicenseException e) {
                    Log.d(LOG_TAG, "    can " + c.name() + " (" + l.spdx() + ")    use: " + d.name() + "(" + d.concludedLicense() + ")?? :  FAIL: " + e.getMessage());
                  //  allCleared = false;
                    Log.d(LOG_TAG, " violated license? for " + c.name() + " 2");
                    violatedComponents.add(d);
                    violationFound = true;
                }*/
                Log.d(LOG_TAG, c.name() + " myCheckedLicenses: " + myCheckedLicenses + " HESA");
            }
//            Log.d(LOG_TAG, " DINKEY 2 " + c.name() + " allCleared:        " + allCleared);
            Log.d(LOG_TAG, " DINKEY 3 " + c.name() + "  possible licenses:        " + myCheckedLicenses + " HESA");
            if (violationFound) {
                Log.d(LOG_TAG, " DINKEY 3 violation found, removing: " + l + " from list");
                myCheckedLicenses.remove(l);
            }

            myCheckedLicenses.sort(MostPermissiveLicenseComparator.comparator);
            if (myCheckedLicenses.size()>0) {
//                Log.d(LOG_TAG, " DINKEY 2 " + c.name() + " allCleared:        " + allCleared + " <----- choose: " + l + " for " + c.name() + "  # licenses: " + c.licenses().size());
                Log.d(LOG_TAG, " DINKEY 2 " + c.name() + "l: " + l + " for " + c.name() + "    # licenses: " + myCheckedLicenses);
                addConcluded(report, l, c);

                if (policy != null && policy.grayList().contains(l)) {
                    report.addLicenseConcern(new PolicyConcern(c, l, ListType.GRAY_LIST));
                }

                break;
                //}
            } else {
//                Log.d(LOG_TAG, " DINKEY 3 " + c.name() + " " + allCleared + " possible licenses:        " + myCheckedLicenses.size() + " HESA " + myCheckedLicenses.size() + " " + myCheckedLicenses);
                Log.d(LOG_TAG, " DINKEY 3 " + c.name() + "  possible licenses:  " + myCheckedLicenses.size() + " HESA " + myCheckedLicenses.size() + " " + myCheckedLicenses);
              //  if (myCheckedLicenses.size() > 0) {
//                    Log.d(LOG_TAG, " DINKEY 3 " + c.name() + " " + allCleared + " possible license:        " + myCheckedLicenses.get(0));
                //    Log.d(LOG_TAG, " DINKEY 3 " + c.name() + " possible license:        " + myCheckedLicenses.get(0));
                  //  addConcluded(report, l, c);
                //}
            }

        }
        Log.d(LOG_TAG, c.name() + " myCheckedLicenses:  " + myCheckedLicenses + " HESA END");
        Log.d(LOG_TAG, c.name() + " violatedComponents: " + violatedComponents + " HESA END");

        License concludedLicense = c.concludedLicense();
        Log.d(LOG_TAG, " DINKEY component:      " + c.name() + " concluded license: " + concludedLicense);
        if (c.concludedLicense() == null) {
            Log.d(LOG_TAG, " DINKEY component:      " + c.name() + " violated license: " + concludedLicense);
            Log.d(LOG_TAG, " addObligationViolation " + c.name() + " 3");
            report.addLicenseObligationViolation(new LicenseObligationViolation(c));
        }
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
