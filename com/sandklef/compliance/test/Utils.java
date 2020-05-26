package com.sandklef.compliance.test;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.json.JsonLicenseConnectionsParser;
import com.sandklef.compliance.json.JsonLicenseParser;
import com.sandklef.compliance.utils.LicenseStore;
import com.sandklef.compliance.utils.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.sandklef.compliance.domain.License.*;
//import static com.sandklef.compliance.domain.License.LOG_TAG;

public class Utils {
    public static String beforeFormat = "%-50s";
    public static License lgpl21;
    public static License lgpl31;
    public static License lgpl30;
    public static License gpl20;
    public static License gpl20_later;
    public static License lgpl21_only;
    public static License lgpl21_later;
    public static License gpl30;
    public static License gpl30_later;
    public static License gpl31;
    public static License apache20;
    public static License bsd3;
    public static boolean useAsserts;
    private static int counter;
    private static int errorCounter;
    private static int successCounter;
    private static List<String> fails;
    static {
        try {
            counter = 0;
            errorCounter = 0;
            successCounter = 0;
            fails = new ArrayList<>();
            LicenseStore.getInstance().addLicenses(new JsonLicenseParser().readLicenseDir("licenses/json"));
            LicenseStore.getInstance().connector(new JsonLicenseConnectionsParser().readLicenseConnection("licenses/connections/dwheeler.json"));
            lgpl21 = LicenseStore.getInstance().license(LGPL_2_0_SPDX);
            lgpl31 = LicenseStore.getInstance().license(LGPL_3_1_SPDX);
            lgpl30 = LicenseStore.getInstance().license(LGPL_3_0_SPDX);
            lgpl21 = LicenseStore.getInstance().license(LGPL_2_1_SPDX);
            gpl20 = LicenseStore.getInstance().license(GPL_2_0_SPDX);
            gpl20_later = LicenseStore.getInstance().license(GPL_2_0_LATER_SPDX);
            lgpl21_later = LicenseStore.getInstance().license(LGPL_2_1_LATER_SPDX);
            lgpl21_only = LicenseStore.getInstance().license(LGPL_2_1_ONLY_SPDX);
            gpl30 = LicenseStore.getInstance().license(GPL_3_0_SPDX);
            gpl30_later = LicenseStore.getInstance().license(GPL_3_0_LATER_SPDX);
            gpl31 = LicenseStore.getInstance().license(GPL_3_1_SPDX);
            apache20 = LicenseStore.getInstance().license(APACHE_2_0_SPDX);
            bsd3 = LicenseStore.getInstance().license(BSD_3_SPDX);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static LicensePolicy blackListApachePolicy() {
        LicensePolicy policy = new LicensePolicy();
        policy.addBlackLicense(apache20);
        return policy;
    }

    public static LicensePolicy grayListApachePolicy() {
        LicensePolicy policy = new LicensePolicy();
        policy.addGrayLicense(apache20);
        return policy;
    }

    public static LicensePolicy grayListBSDPolicy() {
        LicensePolicy policy = new LicensePolicy();
        policy.addGrayLicense(bsd3);
        return policy;
    }

    public static LicensePolicy grayListApacheBlackBSDPolicy() {
        LicensePolicy policy = new LicensePolicy();
        policy.addGrayLicense(apache20);
        policy.addBlackLicense(bsd3);
        return policy;
    }

    public static LicensePolicy permissiveAndWeakPolicy() {
        LicensePolicy policy = new LicensePolicy();

        policy.addWhiteLicense(apache20);
        policy.addGrayLicense(lgpl21);
        policy.addBlackLicense(gpl20);

        return policy;
    }

    public static LicensePolicy copyleftAndWeakPolicy() {
        LicensePolicy policy = new LicensePolicy();

        policy.addWhiteLicense(gpl20);
        policy.addGrayLicense(lgpl21);
        policy.addBlackLicense(apache20);

        return policy;
    }



    public static Component bigComponent() {
    /*
                   A (gpl2|bsd|gpl3)
                     |
           +---------+------------------------+
           |                                  |
           AA (apache2|bsd)           AB (gpl2|gpl3|apache|lgpl3)
           |                                  |
        +--+------------+                +----+-------------+
        |               |                |                  |
 AAA (apache2|gpl3)  AAB (apache|gpl3)  ABA (apache|bsd3)  ABB (gpl3|bsd3|apache)
                                   |                                |
                      +------------+--------+                    ABBA (bsd)
                      |                     |                        \
             AABA (apache|bsd3|lgpl3)     AABB (bsd3|apache|gpl3)     AAA2


     */


        // left
        Component AABA = new Component("AABA", Arrays.asList(new License[]{apache20, bsd3, lgpl30}), null);
        Component AABB = new Component("AABB", Arrays.asList(new License[]{bsd3, apache20, gpl30}), null);
        Component AAB = new Component("AAB", Arrays.asList(new License[]{apache20, lgpl30}), Arrays.asList(new Component[]{AABA, AABB}));

        Component AAA = new Component("AAA", Arrays.asList(new License[]{apache20, gpl30}), null);
        Component AAA2 = new Component("AAA2", Arrays.asList(new License[]{apache20}), null);

        Component AA = new Component("AA", Arrays.asList(new License[]{apache20, bsd3}), Arrays.asList(new Component[]{AAA, AAB}));

        // Right
        Component ABBA = new Component("ABBA", Arrays.asList(new License[]{bsd3, apache20}), Arrays.asList(new Component[]{AAA2}));
        Component ABA = new Component("ABA", Arrays.asList(new License[]{apache20, bsd3}), null);
        Component ABB = new Component("ABB", Arrays.asList(new License[]{gpl30, bsd3, apache20}), Arrays.asList(new Component[]{ABBA}));
        Component AB = new Component("AB", Arrays.asList(new License[]{gpl20, gpl30, apache20, lgpl30}), Arrays.asList(new Component[]{ABA, ABB}));

        // A
        Component A = new Component("A", Arrays.asList(new License[]{bsd3 , gpl20, lgpl21, gpl20, gpl30/*, gpl20*/}), Arrays.asList(new Component[]{AA, AB}));

        return A;
    }



    public static Component validComponent() {
    /*
             top (gpl3)
              |
           +--+--------------------+
           |                       |
           a (apache2)             b (gpl3)
           |                       |
        +--+---------+             +------+--------+
        |            |             |               |
      a1 (apache2)  a2 (apachw2)     b1 (apache2)   b2 (gpl3)
                                   |
                         +---------+-----+
                         |               |
                         b11 (apache2)   b12 (apache2)
     */

        // a1
        Component a1 = new Component("a1", apache20, null);

        // a2
        Component a2 = new Component("a2", apache20, null);

        // a    q
        ArrayList<Component> aDeps = new ArrayList<>();
        aDeps.add(a1);
        aDeps.add(a2);
        Component a = new Component("a", apache20, aDeps);

        // b11
        Component b11 = new Component("b11", apache20, null);

        // b12
        Component b12 = new Component("b12", apache20, null);

        // b1
        ArrayList<Component> b1Deps = new ArrayList<>();
        b1Deps.add(b11);
        b1Deps.add(b12);
        Component b1 = new Component("b1", apache20, b1Deps);

        // b2
        Component b2 = new Component("b2", gpl30, null);

        // b
        ArrayList<Component> bDeps = new ArrayList<>();
        bDeps.add(b1);
        bDeps.add(b2);
        Component b = new Component("b", gpl30, bDeps);


        // top
        ArrayList<Component> deps = new ArrayList<>();
        deps.add(a);
        deps.add(b);
        Component top = new Component("Top", gpl30, deps);

        return top;
    }

    public static Component dynamicComponent() {
    /*
             top (GPL)
              |
           +--+--------------------+---------------------+
           |                       |                     |
           a (apache2)             b (lgpl2)         c(BSD)
*/
        Component a = new Component("a", apache20, null);

        // b
        Component b = new Component("b", lgpl21, null);

        // c
        Component c = new Component("c", bsd3, null);


        // top
        ArrayList<Component> deps = new ArrayList<>();
        deps.add(a);
        deps.add(b);
        deps.add(c);
        Component top = new Component("DynamicTop", gpl30, deps);

        return top;


    }

    public static Component testComponent() {
        /*

                    A (apache, bsd, gpl3)
               +----+-----------+
               |                |
              AA (bsd)         AB (gpl30, lgpl30)
               |
              AAA (gpl2, apache)
        */


        // Left
        Component AAA = new Component("AAA", Arrays.asList(new License[]{bsd3, lgpl30, apache20}), null);
        Component AA = new Component("AA", Arrays.asList(new License[]{gpl30, lgpl30}), Arrays.asList(new Component[]{AAA}));
   //     Component AA = new Component("AA", Arrays.asList(new License[]{gpl30, bsd3, apache20}), null);
        // Right
        Component AB = new Component("AB", Arrays.asList(new License[]{gpl30,lgpl30}), null);

        // A
        Component A = new Component("A", Arrays.asList(new License[]{apache20, bsd3, lgpl30}), Arrays.asList(new Component[]{AA, AB}));

        return A;
    }

    public static Component invalidComponent() {
    /*
             top (apache2)
              |
           +--+--------------------+
           |                       |
           a (apache2)             b (lgpl2)
           |                       |
        +--+---------+             +------+--------+
        |            |             |               |
      a1 (apache2)  a2 (lgpl2)     b1 (apache2)   b2 (gpl2)
                                   |
                         +---------+-----+
                         |               |
                         b11 (apache2)   b12 (apache2)
     */

        // a1
        Component a1 = new Component("a1", apache20, null);

        // a2
        Component a2 = new Component("a2", lgpl21, null);

        // a    q
        ArrayList<Component> aDeps = new ArrayList<>();
        aDeps.add(a1);
        aDeps.add(a2);
        Component a = new Component("a", apache20, aDeps);

        // b11
        Component b11 = new Component("b11", apache20, null);

        // b12
        Component b12 = new Component("b12", apache20, null);

        // b1
        ArrayList<Component> b1Deps = new ArrayList<>();
        b1Deps.add(b11);
        b1Deps.add(b12);
        Component b1 = new Component("b1", apache20, b1Deps);

        // b2
        Component b2 = new Component("b2", gpl20, null);

        // b
        ArrayList<Component> bDeps = new ArrayList<>();
        bDeps.add(b1);
        bDeps.add(b2);
        Component b = new Component("b", lgpl21, bDeps);


        // top
        ArrayList<Component> deps = new ArrayList<>();
        deps.add(a);
        deps.add(b);
        Component top = new Component("InvalidTop", apache20, deps);

        return top;
    }

    public static Component dualLicensedComponent() {
    /*
             top (gpl3)
              |
           +--+--------------------
           |
           a (apache2)
           |
        +--+---------+
        |            |
      a1 (apache2)  a2 (lgpl2|gpl2)
     */

        // a1
        Component a1 = new Component("a1", apache20, null);

        // a2
        List<License> licenses = new ArrayList<>();
        licenses.add(lgpl21);
        licenses.add(gpl20);
        Component a2 = new Component("a2", licenses, null);

        // a    q
        ArrayList<Component> aDeps = new ArrayList<>();
        aDeps.add(a1);
        aDeps.add(a2);
        Component a = new Component("a", apache20, aDeps);

        // top
        ArrayList<Component> deps = new ArrayList<>();
        deps.add(a);
        Component top = new Component("Top", gpl30, deps);

        return top;
    }

    public static void println(String string) {
        System.out.println(string);
    }

    public static void print(String string) {
        System.out.print(String.format(beforeFormat, string));
    }

    public static int erorCounter() {
        return errorCounter;
    }

    public static int sucessCounter() {
        return successCounter;
    }

    public static int counter() {
        return counter;
    }

    public static void assertHelper(String before, boolean value){
//        println("  ====  value: " + value);
        counter++;
        print("  * Verify " + before +": ");
        if (useAsserts ) {
            assert(value);
        }
        if (value) {
            successCounter++;
            println("OK");
        } else {
            errorCounter++;
            fails.add(before);
            println("FAIL");
        }
    }

    public static List<String> fails() {
        return fails;
    }

    public static void printTestStart(String s) {
        println(s);
        println("============================");
    }

    public static void printSubTestStart(String s) {
        println(" " + s);
        println(" ----------------------------");
    }

    public static int countDependencies(Component component) {
        if (component.dependencies()==null) {
            return 0;
        }
        int deps = component.dependencies().size();
        for (Component c : component.dependencies()) {
            Log.d(LOG_TAG, "Adding " + c.dependencies().size() + " for " + c.name());
            deps += countDependencies(c);
//      deps += c.dependencies().size();
        }
        return deps;
    }

/*    public static boolean checkViolation(Report report, Component c) {
        for (LicenseObligationViolation ov : report.violations()) {
            if (ov.user().name().equals(c.name())) { return true; }
        }
        return false;
    }

    public static boolean checkConclusion(Report report, Component c) {
        for (LicenseConclusion lc : report.conclusions()) {
            if (lc.component().name().equals(c.name())) { return true; }
        }
        return false;
    }

    public static boolean checkConcern(Report report, Component c) {
        for (PolicyConcern pc : report.concerns()) {
            if (pc.component().name().equals(pc.component().name())) { return true; }
        }
        return false;
    }
*/
}
