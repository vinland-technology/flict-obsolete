package com.sandklef.compliance.arbiter;

import com.sandklef.compliance.domain.IllegalLicenseExpression;
import com.sandklef.compliance.domain.License;
import com.sandklef.compliance.domain.LicenseCompatibility;
import com.sandklef.compliance.domain.LicenseMatrix;
import com.sandklef.compliance.utils.LicenseStore;
import com.sandklef.compliance.utils.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MatrixLicenseArbiter implements LicenseArbiter {

    private static final String LOG_TAG = MatrixLicenseArbiter.class.getSimpleName();

    public boolean aCanUseB(License a, List<License> bLicenses) throws IllegalLicenseExpression, LicenseCompatibility.LicenseConnectorException {
        //Log.level(Log.DEBUG);
        Log.d(LOG_TAG, "aCanUseB " + a + " " + bLicenses);
        if (a == null || bLicenses == null) {
            throw new IllegalLicenseExpression("Illegal (null) licenses found");
        }

        for (License l : bLicenses) {
            if (!aCanUseB(a, l)) {
                return false;
            }
        }
        return true;
    }


    public boolean aCanUseB(License a, License b) throws IllegalLicenseExpression, LicenseCompatibility.LicenseConnectorException {
        //Log.level(Log.DEBUG);
        Log.d(LOG_TAG, "aCanUseB " + a + " " + b);
        if (a == null || b == null) {
            throw new IllegalLicenseExpression("Illegal (null) license found");
        }



        LicenseMatrix matrix = LicenseStore.getInstance().licenseMatrix();

//        matrix.verifyMatrix();

        int aIndex = matrix.indexOfLicense(a);
        int bIndex = matrix.indexOfLicense(b);
       // System.out.println("   " + LicenseStore.getInstance().licenseMatrix());
       // System.out.println(" a: [\"" + a + "\" / " + aIndex  + "]");
        System.out.println("aCanUseB a: [\"" + a + "\" / " + aIndex  + "]");
        System.out.println("aCanUseB a: [\"" + b + "\" / " + bIndex  + "]");
        return (matrix.valueAt(aIndex,bIndex)==LicenseMatrix.LICENSE_MATRIX_TRUE);
    }

    @Override
    public String name() {
        return "Matrix based license arbiter";
    }

    private boolean directMatch(LicenseCompatibility a, LicenseCompatibility b) throws LicenseCompatibility.LicenseConnectorException {
        // a contains a license
        if (a.hasLicense()) {
            // b contains a license
            // - both contains licenses, check if same license
            if (b.hasLicense()) {
                return a.license().spdx().equals(b.license().spdx());
            }
        }
        return false;
    }

    private boolean aCanUseBImpl(LicenseCompatibility a, LicenseCompatibility b, List<LicenseCompatibility> visited) throws LicenseCompatibility.LicenseConnectorException {
        Log.d(LOG_TAG, " aCanUseBImp  ---> check lic: " + a + " and " + b + "    { " + visited + " }");

        // If a and b are the same object, then they're (for sure) compliant
        if (a==b) {
            Log.d(LOG_TAG, " aCanUseBImp <--- same licensecompat, true");
            return true;
        }

        // Check if we've visited this connector already. If so, false
        //     Log.d(LOG_TAG, " ***************** ALREADY BEEN IN " + b + " ******** " + visited.contains(b));
        if (visited.contains(b)) {
            // already checked b
            Log.d(LOG_TAG, " aCanUseBImp <--- already been there, false");
            return false;
        } else if (b == null) {
            Log.d(LOG_TAG, " aCanUseBImp <--- b null, false");
            // probably a violation in "lower" components
            return false;
        } else {
            Log.d(LOG_TAG, " aCanUseBImp  --- add to visited: " + b);
            // not visited, mark it as visited
            visited.add(b);
        }

        if (directMatch(a, b)) {
            Log.d(LOG_TAG, " aCanUseBImp <--- direct match, true");
            return true;
        }

        if (a.canUse().contains(b)) {
            Log.d(LOG_TAG, " aCanUseBImp <--- contains, true");
            return true;
        }
        //      System.out.println(" Try 1 <--- : " + a.license().spdxTag() + " " + a.canUse() + " contains " + b.license());

        // Loop through all b's canBeUsed licenses
        for (LicenseCompatibility l : b.canBeUsedBy()) {

            if (directMatch(a, b)) {
                return true;
            }

            if (aCanUseBImpl(a, l, visited)) {
                //  System.out.println(" Try 3 <--- : " + a.license().spdx() + "   CHECK: " + l.license().spdx());
                return true;
            }
        }

        Log.d(LOG_TAG, "aCanUseBImpl: <--- end of method ... false: " + a + "  " + b);
        return false;
    }

    private boolean aCanUseB(LicenseCompatibility a, LicenseCompatibility b) throws LicenseCompatibility.LicenseConnectorException {
        return aCanUseBImpl(a, b, new ArrayList<>());
    }

}
