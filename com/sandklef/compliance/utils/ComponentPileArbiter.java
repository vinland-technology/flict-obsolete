package com.sandklef.compliance.utils;

import com.sandklef.compliance.arbiter.LicenseArbiter;
import com.sandklef.compliance.domain.*;

import java.util.*;

import static com.sandklef.compliance.domain.PileComplianceReport.ComponentStatus;

public class ComponentPileArbiter {

    private LicenseArbiter arbiter;
    private Component c;
    private LicensePolicy policy;
    private String laterFile;

    public ComponentPileArbiter(LicenseArbiter arbiter, Component c, LicensePolicy policy, String laterFile) {
        this.arbiter = arbiter;
        this.c = c;
        this.policy = policy;
        this.laterFile = laterFile;
    }

    private class ComponentMatrixHolder {
        boolean[][] componentMatrix;
        List<License> allLicensesList;
    }

    private ComponentMatrixHolder componentMatrix(Map<Component, List<List<License>>> map)
            throws IllegalLicenseExpression, LicenseCompatibility.LicenseConnectorException {
        /*
            We get this:
            ----------------------------------------------------------------
            C1 [L1, L2]   - Component C1 has a license expression "L1 & L2"
            C2 [L3]   - Component C2 has a license expression "L3"
            C3 [L4, L1]   - Component C2 has a license expression "L3"

            Which we turn in to this:

               | L1 | L2 | L3 | L4 | (Unique licenses, so discard one L1)
            ---------------------------
            L1 |    |    |    |    |
            L2 |    |    |    |    |
            L3 |    |    |    |    |
            L4 |    |    |    |    |

            So, we need to check for compliance
            L1 with L1 .. L4
            L2 with L1 .. L4
            L3 with L1 .. L4
            L4 with L1 .. L4
         */

        // Go through all maps
        // and their license lists, and "split" the list if more than one
        Set<License> allLicenses = new HashSet<>();
        for (Map.Entry<Component, List<List<License>>> listEntry : map.entrySet())
           for (List<License> licenseList : listEntry.getValue()) {
            for (License license : licenseList) {
                allLicenses.add(license);
            }
        }

        List<License> allLicensesList = new ArrayList<>(allLicenses);
        int matrixSize = allLicensesList.size();
//        System.out.println("set: " + allLicenses.size() + " " + allLicenses);
        boolean[][] componentMatrix = new boolean [matrixSize][matrixSize];
        //System.out.println("matrix: " + matrixSize + "  => " + Arrays.deepToString(componentMatrix));

        // Prepare resulting object
        ComponentMatrixHolder holder = new ComponentMatrixHolder();
        holder.allLicensesList = allLicensesList;
        holder.componentMatrix = componentMatrix;

        // Check left element with the rest, column per column
        // ... and fill array
        for (int i=0; i<matrixSize; i++) {
            License leftLicense = allLicensesList.get(i);
         //   System.out.println("Left license: " +  leftLicense);
            for (int j = 0; j < matrixSize; j++) {
                if (i==j) {
                    componentMatrix[i][j] = true;
                } else {
                    License licenseToTestAgainst = allLicensesList.get(j);
                    boolean compliant = arbiter.aCanUseB(leftLicense, licenseToTestAgainst);
//                    System.out.println("    [" + leftLicense.spdx() + " ---> " + licenseToTestAgainst + "]: " + compliant);
                    componentMatrix[i][j] = compliant;
                }
            }

        }

     //   System.out.println("matrix: " + Arrays.deepToString(componentMatrix));

        return holder;
    }

    public ComponentStatus checkMapCompliance(Map<Component, List<List<License>>> map,
                                             PileComplianceReport pileComplianceStatus)
            throws IllegalLicenseExpression, LicenseCompatibility.LicenseConnectorException {

        ComponentStatus status = new ComponentStatus();

        ComponentMatrixHolder holder = componentMatrix(map);
        List<License> list = holder.allLicensesList;
        boolean[][] componentMatrix = holder.componentMatrix;
        int matrixSize = componentMatrix[0].length;

        boolean compliant = false;

        List<License> avoided = new ArrayList<>();
        List<License> denied = new ArrayList<>();

        for (int i = 0; i < matrixSize; i++) {
            boolean rowCompliant = true;
            for (int j = 0; j < matrixSize; j++) {
//                System.out.print(" " + componentMatrix[i][j] );
              rowCompliant = rowCompliant && componentMatrix[i][j];
            }
            if (rowCompliant) {
                status.outboundLicenseCoice().add(list.get(i));
            }
//            status.outboundLicenseCoice.add(
            compliant = compliant || rowCompliant;
  //          System.out.println("   ==> GIVES: " + compliant );
        }
    //    System.out.println("   ==> GIVES FINALLY: " + compliant );

        for (License l : list) {
         //   System.out.println(" adding to xxx: " + l +  " avoid: " + policy.avoidList());
          //  System.out.println(" adding to xxx: " + l +  " denied: " + policy.deniedList());
            if (policy.deniedList().contains(l)) {
            //    System.out.println(" adding to denied: " + l);
                denied.add(l);
            } else if (policy.avoidList().contains(l)) {
              //  System.out.println(" adding to avoid: " + l);
                avoided.add(l);
            } else {
               // System.out.println(" adding to allow: " + l);
            }
//            System.out.println(" adding ...");
        }


        status.compliant(compliant);
        //System.out.println("   ==> GIVES FINALLY: " + compliant );
        status.avoidLicenses(avoided);
        status.deniedLicenses(denied);
        status.map(map);

        System.out.println(" avoided: " + status.avoidLicenses().size());
        System.out.println(" denied:  " + status.deniedLicenses().size());

      /*  System.out.print(" STATUS: ");
        for (ComponentStatus s : pileComplianceStatus.compliantComponents()){
            System.out.print(" | " + s.compliant);
        }
        System.out.println("");
*/

        return status;
    }

    private PileComplianceReport pileComplianceStatus()
            throws IllegalLicenseExpression, LicenseExpressionException, LicenseCompatibility.LicenseConnectorException {
        System.out.println("---------------------=========== Component =================------------------------");
        System.out.println(c);
        System.out.println(c.toStringLong());
        System.out.println("---------------------=======================================------------------------");

        PileComplianceReport pileComplianceStatus = new PileComplianceReport(c, policy, laterFile, new MetaData(), arbiter);

        int i=0;
        List<Boolean> compliantList = new ArrayList<>();
        for (Map<Component, List<List<License>>> licMap : c.allLicensesCombinationsList() ) {
            ComponentStatus status = checkMapCompliance(licMap, pileComplianceStatus);
            pileComplianceStatus.addComponentStatus(status);
        }
        return pileComplianceStatus;
    }

    public long combinations(LicenseArbiter arbiter, Component c, LicensePolicy policy) {
        return 0;
    }

    public PileComplianceReport report()
            throws LicenseExpressionException, IllegalLicenseExpression, LicenseCompatibility.LicenseConnectorException {

        PileComplianceReport pileComplianceStatus = pileComplianceStatus();

        return pileComplianceStatus;
    }
}
