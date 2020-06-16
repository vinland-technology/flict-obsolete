package com.sandklef.compliance.domain;

import com.sandklef.compliance.utils.Log;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.sandklef.compliance.domain.LicenseExpression.Operator.*;

public class LicenseExpression {
    private static final String LOG_TAG = LicenseExpression.class.getSimpleName();


    /*

    LicenseExpression :=
        License
        ( License )
        LicenseExpression op LicenseExpression
        ( LicenseExpression op LicenseExpression )
     */

    public enum Operator {
        AND,
        OR
    }

    ;

    public static Operator stringToOperator(String op) {
        if (op.equals("&")) {
            return AND;
        } else if (op.equals("|")) {
            return OR;
        }
        return null;
    }

    public static String operatorToString(Operator op) {
        switch (op) {
            case AND:
                return "&";
            case OR:
                return "|";
        }
        return null;
    }

    private License license;
    private Operator op;
    private List<LicenseExpression> licenses;

    public LicenseExpression() {
        licenses = new ArrayList<>();
    }

    public LicenseExpression(License license) {
        Log.d(LOG_TAG, "Creating License from: " + license);
        this.license = license;
    }

    public void validateState() throws IllegalLicenseExpression {
        if (license != null && op != null) {
            throw new IllegalLicenseExpression("Operator and license both set, illegal state", this);
        }
    }

    public void op(Operator op) throws IllegalLicenseExpression {
        Log.d(LOG_TAG, " Set op: " + op);
        this.op = op;
        validateState();
    }

    public Operator op() {
        return this.op;
    }

    public void licenses(List<LicenseExpression> licenses) throws IllegalLicenseExpression {
        this.licenses = licenses;
        validateState();
    }

    public List<LicenseExpression> licenses() {
        return licenses;
    }

    public void addLicense(LicenseExpression expr) {
        Log.d(LOG_TAG, " Add license: " + expr);
        licenses.add(expr);
    }

    public void addLicense(License license) {
        Log.d(LOG_TAG, " Add license: " + license.spdx());
        licenses.add(new LicenseExpression(license));
    }

    // How many paths does this expression cause/generate
    public int paths() {
//        Log.d(LOG_TAG, "--> paths()");
        // If single license: 1
        if (license != null) {
            return 1;
        }

        int pathCount = (op == AND ? 1 : 0);
/*
        Log.d(LOG_TAG, "--  paths() lic: " + license);
        Log.d(LOG_TAG, "--  paths() op:  " + op);
        Log.d(LOG_TAG, "--  paths() pc:  " + pathCount);
        Log.d(LOG_TAG, "--  paths() lics:" + licenses);
 */

        // If license expression(s)
        for (LicenseExpression le : licenses) {
            if (op == AND) {
                pathCount *= le.paths();
            } else if (op == OR) {
                pathCount += le.paths();
            }
        }
        //       Log.d(LOG_TAG, "<-- paths() " + pathCount);
        return pathCount;
    }

    public List<List<License>> licenseList2() {
        Log.d(LOG_TAG, "licenseList()");
/*        List<List<License>> licenseListStart = new ArrayList<>();
        licenseListStart.add(new ArrayList<License>());
 */
        // List<List<License>> licenseList = licenseList2(new ArrayList<>());
        return null; //licenseList;
    }


    private List<List<License>> copyList(List<List<License>> licenseList, int times) {
        List<List<License>> newLicenseList = new ArrayList<>();

        int origSize = licenseList.size();

        Log.d(LOG_TAG, "  COPY " + times + "  " + origSize + " " + licenseList);
        if (origSize > 0) {
            for (List<License> list : licenseList) {
                // Put the new list copy in the list to return
                // - and do it times times
                for (int i = 0; i < times; i++) {

                    // Copy each list in licenseList (parameter)
                    // - create new List as a copy of list
                    List<License> newTmpList = new ArrayList<>();
                    for (License l : list) {
                        newTmpList.add(l.clone());
                    }

                    newLicenseList.add(newTmpList);
                }

                Log.d(LOG_TAG, "    newLicenseList: " + newLicenseList);
            }
        } else {
            for (int i = 0; i < times; i++) {
                Log.d(LOG_TAG, "  COPY i: " + i + "    times: " + times);
                newLicenseList.add(new ArrayList<>());
            }
        }

        Log.d(LOG_TAG, "  COPY return : " + newLicenseList);
        return newLicenseList;
    }

    public List<List<License>> licenseList() throws IllegalLicenseExpression {

        List<List<License>> licenseList = new ArrayList<>();

        // base case
        // - no compund expression, but simply a license
        Log.d(LOG_TAG, " -------------> licenselist()");
        if (license != null) {
            Log.d(LOG_TAG, "license found: " + license);
            // Create list from license, and add to licenseList
            licenseList.add(new ArrayList(Arrays.asList(license)));
        } else {

            // Transform all LicenseExpressions in to on List List
            for (LicenseExpression le : licenses()) {


                if (licenseList.size() == 0) {
                    for (List<License> licenses : le.licenseList()) {
                        licenseList.add(licenses);
                    }
                } else if (op == OR) {
                    for (List<License> licenses : le.licenseList()) {
                        // Since we're in OR operation we simply add a "row" in the list
                        // - as a path through the component's licenses
                        licenseList.add(licenses);
                    }
                } else if (op == AND) {
                    Log.d(LOG_TAG, " ------------- licenselist()  le:          " + le);
                    Log.d(LOG_TAG, " ------------- licenselist()  paths:       " + le.paths());
                    Log.d(LOG_TAG, " ------------- licenselist()  licenseList: " + licenseList);
                    Log.d(LOG_TAG, " ------------- licenselist()  licenseList: " + licenseList);
                    Log.d(LOG_TAG, " ------------- licenselist()  operator:    " + le.op());
                    if (le.op() == OR) {
                        // copy all existing licenses (licenseList)
                        // and add each new found license from the list (le.licenseList())

                        /*
                           Example:
                            LicenseExpression: AND [ g, OR [ a, b ] ]
                            licenseList = []

                           First round:
                           -----------------
                            licenseList = [ [g] ]

                           Second round, copy (since OR)
                           -----------------
                            licenseList = [ [g], [g] ]

                           Second round, add
                           -----------------
                            licenseList = [
                                [g, a],
                                [g, b]
                            ]
                         */

                        // Copy existing list as many times as we have paths through this le
                        Log.d(LOG_TAG, "           ------------- copy list :          " + licenseList + "  paths: " + le.paths());
                        licenseList = copyList(licenseList, le.paths());
                        Log.d(LOG_TAG, "           ------------- copy list :          " + licenseList + "   ");
//                        System.exit(1);

                        Log.d(LOG_TAG, "           ------------- copy list :          " + licenseList + "   add " + le.licenseList());
                        // Get licenses (from le) to add
                        List<List<License>> licensesListToAdd = le.licenseList();
                        // For each List in licenseList (which should have space for copying in licenses OR-wise)
                        // - add the license at position i%(list size)
                        // - licenseList example: [ [g], [g] ]
                        // -   ADD le:    OR [a,b]
                        // -   shoud give:[ [g,a], [g,b] ]
                        //
                        // Loop through licenseList: [ [g], [g] ]
                        int i = 0;

                        for (List<License> licenses : licenseList) {
                            // Get the list of licenses (orig from le) to add. Example: OR[a, b]
                            List<License> licensesToAdd = licensesListToAdd.get(i % licensesListToAdd.size());

                            Log.d(LOG_TAG, "           ------------- to add to:          " + licenses + "  add them: " + licensesListToAdd);

                            Log.d(LOG_TAG, "              ------------- prepare looping  i: " + i + "      " + licensesListToAdd.size() + "   " + licensesListToAdd + "    --> " + licenses);
                            for (License l : licensesToAdd) {
                                Log.d(LOG_TAG, "              ------------- looping  i: " + i + "  add l: " + l + "    to " + licenses);
                                licenses.add(l); // l comes from le
                                Log.d(LOG_TAG, "              ------------- looping  i: " + i + "  l: " + l + "    " + licenseList);
                                Log.d(LOG_TAG, "           ------------- to add to:          " + licenses + "  add this: " + l);
                                Log.d(LOG_TAG, "           ------------- to add to:          " + licenseList);
                             }
                            Log.d(LOG_TAG, "           ------------- to add :          " + licenseList);
                            i++;
                        }
                    } else if (le.op() == AND) {
                        // Get licenses (from le) to add
                        List<List<License>> licensesListToAdd = le.licenseList();
                        int i = 0;
                        for (List<License> licenses : licenseList) {
                            List<License> licensesToAdd = licensesListToAdd.get(i % licensesListToAdd.size());
                            Log.d(LOG_TAG, "           ------------- to add :          " + licensesListToAdd);
                            for (License l : licensesToAdd) {
                                Log.d(LOG_TAG, "              ------------- to add :          " + l);
                                licenses.add(l);
                            }
                            Log.d(LOG_TAG, "           ------------- to add :          " + licenseList);
                            i++;
                        }
                    } else if (le.license != null) {
                        // AND [B, G]
                        Log.d(LOG_TAG, "           ------------- to add :          " + le.license + "  list: " + licenseList);
                        for (List<License> licenses : licenseList) {
                            licenses.add(le.license);
                        }
                    } else {
                        throw new IllegalLicenseExpression("Illegal state for license");
                    }

                } else {
                    throw new IllegalLicenseExpression("Illegal state for license");
                }
            }
        }


        Log.d(LOG_TAG, " <------------- licenselist()   list: " + licenseList);
        return licenseList;
    }

    private List<List<License>> licenseList2(List<List<License>> licenseList) {
        Log.d(LOG_TAG, " -------------> licenselist()");

        if (license != null) {
            Log.d(LOG_TAG, "license found: " + license);
            // Create list from license, and add to licenseList
            licenseList.add(Arrays.asList(license));
            Log.d(LOG_TAG, " <------------- licenselist() license found, list: " + licenseList);
            return licenseList;
        }

        if (op == AND) {
            Log.d(LOG_TAG, "AND orig size: " + licenseList.size());

            // Since AND we only need to copy to the list
            for (LicenseExpression le : licenses) {
                Log.d(LOG_TAG, " * AND add license expression:" + le + " of type: " + le.op());
                // get the list of licenses from the expresssions
                for (List<License> leList : le.licenseList2()) {
                    Log.d(LOG_TAG, "   * AND add license list:" + leList);
                    // ... and add them to the "top" lists (licenseList)
                    for (License l : leList) {
                        Log.d(LOG_TAG, "     * AND add license:" + l + "   licenseList:  " + licenseList);
                        if (licenseList.size() == 0) {
                            licenseList.add(new ArrayList<>());
                        }
                        for (List<License> listToAddTo : licenseList) {
                            if (le.op() == AND) {
                                Log.d(LOG_TAG, "     * AND add AND license:  " + l + "  to " + listToAddTo + " <-------- 00");
                                listToAddTo.add(l);
                                Log.d(LOG_TAG, "     * AND added AND license:" + l + "  to " + listToAddTo + " <--------");
                            } else {
                                Log.d(LOG_TAG, "     * AND add OR license:  " + l + "  to " + listToAddTo);
                                listToAddTo.add(l);
                                Log.d(LOG_TAG, "     * AND add OR license:  " + l + "  to " + licenseList + " bailing out...");
                            }
                        }
                    }
                }
            }
        } else {
            // Since OR we need to copy to the list one time per license expression
            // First, prepare space

            int licenseCount = licenses.size();
//                int origSize = licenseList.size();
            Log.d(LOG_TAG, "OR old size 0: " + licenseList.size());
            licenseList = copyList(licenseList, licenseCount);


            Log.d(LOG_TAG, "OR new size 1: " + licenseList.size());
            Log.d(LOG_TAG, "OR new size 2: " + licenses.size());
            int listIndex = 0;
            Log.d(LOG_TAG, "OR with index: " + listIndex + " and licenses count to: " + licenseCount + " from :" + licenses);

            for (LicenseExpression le : licenses) {
                Log.d(LOG_TAG, " * OR add license expression:" + le + " to " + licenseList + "  ??");
                // get the list of licenses from the expresssions
                for (List<License> leList : le.licenseList2()) {
                    Log.d(LOG_TAG, "   * OR add license list:" + leList + "  <----------- ");
                    // ... and add them to the "top" lists (licenseList)
                    Log.d(LOG_TAG, "OR new size: " + licenseList.size());
                    for (License l : leList) {
                        if (le.op() == OR) {
                            Log.d(LOG_TAG, "OR add to index:   " + listIndex + "  " + l + "  " + licenseList.get(listIndex) + " with index: " + listIndex + "  of size: " + licenseList.size());
                            licenseList.get(listIndex).add(l);
                            Log.d(LOG_TAG, "OR added to index: " + listIndex + "  " + licenseList + "  <----");
                        } else {
                            Log.d(LOG_TAG, "     * OR add AND license:  " + l + " bailing out");
                            System.exit(1);
                        }
                    }
                    listIndex++;
                }
            }
            Log.d(LOG_TAG, "OR ended with index: " + listIndex);
        }

        Log.d(LOG_TAG, " RETURNING: " + licenseList + "  op: " + op);
        Log.d(LOG_TAG, " <------------- licenselist()");
        return licenseList;
    }

    public static String licenseListToString(List<List<License>> licenseList) {
        StringBuffer sb = new StringBuffer();
        sb.append("\n");
        sb.append("[");
        sb.append("\n");
        for (List<License> list : licenseList) {
            sb.append("  ");
            sb.append(list);
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }

    public boolean valid() {
        return (license != null || op != null);
    }


    @Override
    public String toString() {
        //System.out.println("Lice: " + license + "  op: " + op);
        if (license != null) {
            return "\"" + license.spdx() + "\"";
        } else if (op != null) {
            return op.toString() +
                    licenses +
                    "  ";
        } else {
            return "undef";
        }
    }


}
