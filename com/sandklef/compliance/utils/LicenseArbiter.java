// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.utils;

import java.util.ArrayList;
import java.util.List;

import com.sandklef.compliance.domain.*;
import sun.nio.cs.ext.COMPOUND_TEXT;

public class LicenseArbiter {

    public static String LOG_TAG = LicenseArbiter.class.getSimpleName();

    public static ObligationState state(License license, String obligationName) {
        return license.obligations().get(obligationName).state();
    }

    public static boolean aUsesB(Component c, Report report, License user, License usee) throws LicenseViolationException, NoLicenseException {
        if (usee == null) {
            Log.d(LOG_TAG, "aUsesB(" + user + ", " + usee + ", ...)  ===>  false");
/*            Log.d(LOG_TAG, " violated license for " + c.name() + " 3");

           report.violation().addObligationViolation(
                    new LicenseViolation.ObligationViolation(c));*/
            throw new NoLicenseException("Used licenses unknown ", user, usee);
        }
        Log.d(LOG_TAG, "aUsesB(" + user + ", " + usee + ", ...)");
        Log.d(LOG_TAG, "aUsesB(" + user.spdxTag() + ", " + usee.spdxTag() + ", ...)");

        if (user.spdxTag().equals(usee.spdxTag())) {
            Log.d(LOG_TAG, "Same license: true");
            return true;
        }

        // Below it will be assumed that the licenses differ

        // if not copylefted - ok to use
        if (!usee.isCopyleft()) {
            Log.d(LOG_TAG, user.spdxTag() + " using " + usee.spdxTag() + " : non copyleft so OK, no possible violation");
            return true;
        }

        // If usee is copylefted AND user NOT copylefted  => violation
        if (usee.isCopyleft() && (!user.isCopyleft())) {
            Log.d(LOG_TAG, user.spdxTag() + " using " + usee.spdxTag() + " : possible violation detected");
            throw new LicenseViolationException(user.spdxTag() + " can not sublicense " + usee.spdxTag(), user, usee);
        }

        // TODO: If usee is copylefted AND user copylefted  => check gpl etc


        Log.d(LOG_TAG, user.spdxTag() + " using " + usee.spdxTag() + " : no possible violation detected");
        return true;
    }

    public static boolean canAUseB(Component c, Report report, License user, License usee) {
        try {
            return aUsesB(c, report, user, usee);
        } catch (LicenseViolationException | NoLicenseException e) {
            Log.d(LOG_TAG, "Exception");
            Log.d(LOG_TAG, "    message: " + e.getMessage());
//            Log.d(LOG_TAG, "    user:    (" + e.user.spdxTag() + ")");
            //          Log.d(LOG_TAG, "    usee:    (" + e.usee.spdxTag() + ")");
            return false;
        }
    }


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
            report.conclusion().addLicenseConclusion(new Conclusion.LicenseConclusion(c, l));
        }
    }

    private static void report(Component c, LicensePolicy policy, Report report) {
        Log.d(LOG_TAG, "report() component: " + c.name() + " violation: " + c.name() + "   viols: " + report.violation.obligations().size());

        // Sort the licenses in permissive order
        c.licenses().sort(MostPermissiveLicenseComparator.comparator);

        // second - return true if no deps
        if (c.dependencies().size() == 0) {
            for (License l : c.licenses()) {
                Log.d(LOG_TAG, c.name() + ": has no deps, try concluding license: " + l);
                if (policy != null && policy.blackList().contains(l)) {
                    Log.d(LOG_TAG, "Black colored license found for " + c.name() + ", can't conlcude it: " + l + " sorry");
                } else if (policy != null && policy.grayList().contains(l)) {
                    Log.d(LOG_TAG, "Gray colored license found for " + c.name() + " : " + l);
                    Log.d(LOG_TAG, " concerned license for " + c.name() + " is " + l);
                    report.concern.addLicenseConcern(new Concern.LicenseConcern(c, l, ListType.GRAY_LIST));
                    // TODO: add in some kind of list of gray yet possible licenses instead of marking as concluded?
                    addConcluded(report, l, c);
                } else {
                    Log.d(LOG_TAG, "White colored license found for " + c.name() + ": " + l);
                    addConcluded(report, l, c);
                    return;
                }
            }
            Log.d(LOG_TAG, " License for " + c.name() + ": " + c.concludedLicense() + "  ... is this ok");
            if (c.concludedLicense() == null) {
                report.violation().addObligationViolation(new LicenseViolation.ObligationViolation(c));
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
        // -- for each of the depdendency component's concluded license
        // ------ check if ok
        boolean allCleared = true;

        // List for all licenses that can be used - we will choose which later on
        List<License> myCheckedLicenses = new ArrayList<>();

        for (License l : c.licenses()) {
            allCleared = true;
            if (policy != null && policy.blackList().contains(l)) {
                continue;
            }
            Log.d(LOG_TAG, "    can " + c.name() + " with: " + l.spdxTag() + " in components: " + c.dependencies().size());
            for (Component d : c.dependencies()) {

                Log.d(LOG_TAG, "    can " + c.name() + " (" + l.spdxTag() + ")    use: " + d.name() + "(" + d.concludedLicense() + ")");
                try {
                    aUsesB(d, report, l, d.concludedLicense());
                    // So, the license l can be used when using d
                    Log.d(LOG_TAG, "    can " + c.name() + " (" + l.spdxTag() + ")    use: " + d.name() + "(" + d.concludedLicense() + ") :  OK choice");
                    myCheckedLicenses.add(l);
                    Log.d(LOG_TAG, "    myCheckedLicenses for " + c.name() + "  added " + l.spdxTag() + "  to " + myCheckedLicenses);
                } catch (LicenseViolationException e) {
                    Log.d(LOG_TAG, "    can " + c.name() + " (" + l.spdxTag() + ")    use: " + d.name() + "(" + d.concludedLicense() + ") :  FAIL: " + e.getMessage());
                    allCleared = false;
                } catch (NoLicenseException e) {
                    Log.d(LOG_TAG, "    can " + c.name() + " (" + l.spdxTag() + ")    use: " + d.name() + "(" + d.concludedLicense() + ") :  FAIL: " + e.getMessage());
                    allCleared = false;
                    Log.d(LOG_TAG, " violated license? for " + c.name() + " 2");
                }
            }
            Log.d(LOG_TAG, " DINKEY 2 " + c.name() + " allCleared:        " + allCleared);
            Log.d(LOG_TAG, " DINKEY 3 " + c.name() + " " + allCleared + " possible licenses:        " + myCheckedLicenses);
            myCheckedLicenses.sort(MostPermissiveLicenseComparator.comparator);
            if (allCleared) {
                Log.d(LOG_TAG, " DINKEY 2 " + c.name() + " allCleared:        " + allCleared + " <----- choose: " + l + " for " + c.name() + "  # licenses: " + c.licenses().size());
                addConcluded(report, l, c);

                if (policy != null && policy.grayList().contains(l)) {
                    report.concern().addLicenseConcern(new Concern.LicenseConcern(c, l, ListType.GRAY_LIST));
                }

                break;
                //}
            } else {
                Log.d(LOG_TAG, " DINKEY 3 " + c.name() + " " + allCleared + " possible licenses:        " + myCheckedLicenses.size());
                if (myCheckedLicenses.size() > 0) {
                    Log.d(LOG_TAG, " DINKEY 3 " + c.name() + " " + allCleared + " possible license:        " + myCheckedLicenses.get(0));
                    addConcluded(report, l, c);
                }
            }

        }

        License concludedLicense = c.concludedLicense();
        Log.d(LOG_TAG, " DINKEY component:      " + c.name() + " concluded license: " + concludedLicense);
        if (c.concludedLicense() == null) {
            Log.d(LOG_TAG, " DINKEY component:      " + c.name() + " violated license: " + concludedLicense);
            Log.d(LOG_TAG, " addObligationViolation " + c.name() + " 3");
            report.violation().addObligationViolation(
                    new LicenseViolation.ObligationViolation(c));
        }
    }
}
