package com.sandklef.compliance.test;

import com.sandklef.compliance.domain.Component;
import com.sandklef.compliance.domain.License;
import com.sandklef.compliance.domain.LicensePolicy;
import com.sandklef.compliance.domain.Report;
import com.sandklef.compliance.utils.LicenseArbiter;
import com.sandklef.compliance.utils.Log;

import java.util.ArrayList;
import java.util.List;

import static com.sandklef.compliance.test.Utils.*;

public class TestConcern {

    private final static String LOG_TAG = TestConcern.class.getSimpleName();

    public static void test() {
        printTestStart("TestConcern");

        printSubTestStart("One concern");

        LicensePolicy policy = new LicensePolicy();
        policy.addGrayLicense(gpl20);
        policy.addBlackLicense(gpl30);

        /*
                   Top
                 (GPLv2)
                    |
             +------+--------+
             |               |
          One-lLeft       One-Right
          (LGPLv21)       (GPLv2) <--- gray list
                             |
                     +-------+--------+
                     |                |
                   Two-Left        Two-Right
                   (LGPLv21)         (GPLv2+)
         */

        Component twoLeft = new Component("Two-Left", lgpl21, null);
        Component twoRight = new Component("Two-Right", gpl20_later, null);

        ArrayList<Component> oneRightDeps = new ArrayList<>();
        oneRightDeps.add(twoLeft);
        oneRightDeps.add(twoRight);

        Component oneLeft = new Component("One-Left", lgpl21, null);
        Component oneRight = new Component("One-Right", gpl20, oneRightDeps);

        ArrayList<Component> topDeps = new ArrayList<>();
        topDeps.add(oneLeft);
        topDeps.add(oneRight);

        Component top = new Component("Top", gpl20, topDeps);

//        Log.level(Log.DEBUG);

       // Log.level(Log.DEBUG);
        Report report = LicenseArbiter.report(top, policy);

        Log.d(LOG_TAG, "TestConcern");
/*        Log.d(LOG_TAG, "   concerns:    " + report.concerns() + "  " + report.concerns().size());
        Log.d(LOG_TAG, "   conclusions: " + report.conclusions() + "  " + report.conclusions().size());
        Log.d(LOG_TAG, "   violations:  " + report.violations() + "  " + report.violations().size());
        assertHelper("Check one concern", report.concerns().size()==0);
  */
    }


    public static void main(String[] args) {
        test();
    }
}
