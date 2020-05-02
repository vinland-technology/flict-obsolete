package com.sandklef.compliance.test;

import com.sandklef.compliance.domain.Component;
import com.sandklef.compliance.domain.License;
import com.sandklef.compliance.json.JsonLicenseParser;
import com.sandklef.compliance.utils.LicenseStore;
import com.sandklef.compliance.utils.Log;

import java.io.IOException;
import java.util.ArrayList;

import static com.sandklef.compliance.domain.License.*;
import static com.sandklef.compliance.domain.License.LOG_TAG;

public class Utils {
    public static String beforeFormat = "%-50s";
    public static License lgpl2;
    public static License gpl2;
    public static License apache2;
    static {
        try {
            LicenseStore.getInstance().addLicenses(new JsonLicenseParser().readLicenseDir("licenses/json"));
            lgpl2 = LicenseStore.getInstance().license(LGPL_2_0_SPDX);
            gpl2 = LicenseStore.getInstance().license(GPL_2_0_SPDX);
            apache2 = LicenseStore.getInstance().license(APACHE_2_0_SPDX);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static Component validComponent() {
    /*
             top (gpl2)
              |
           +--+--------------------+
           |                       |
           a (apache2)             b (gpl2)
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
        Component a1 = new Component("a1", apache2, null);

        // a2
        Component a2 = new Component("a2", lgpl2, null);

        // a    q
        ArrayList<Component> aDeps = new ArrayList<>();
        aDeps.add(a1);
        aDeps.add(a2);
        Component a = new Component("a", apache2, aDeps);

        // b11
        Component b11 = new Component("b11", apache2, null);

        // b12
        Component b12 = new Component("b12", apache2, null);

        // b1
        ArrayList<Component> b1Deps = new ArrayList<>();
        b1Deps.add(b11);
        b1Deps.add(b12);
        Component b1 = new Component("b1", apache2, b1Deps);

        // b2
        Component b2 = new Component("b2", gpl2, null);

        // b
        ArrayList<Component> bDeps = new ArrayList<>();
        bDeps.add(b1);
        bDeps.add(b2);
        Component b = new Component("b", gpl2, bDeps);


        // top
        ArrayList<Component> deps = new ArrayList<>();
        deps.add(a);
        deps.add(b);
        Component top = new Component("Top", gpl2, deps);

        return top;
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
        Component a1 = new Component("a1", apache2, null);

        // a2
        Component a2 = new Component("a2", lgpl2, null);

        // a    q
        ArrayList<Component> aDeps = new ArrayList<>();
        aDeps.add(a1);
        aDeps.add(a2);
        Component a = new Component("a", apache2, aDeps);

        // b11
        Component b11 = new Component("b11", apache2, null);

        // b12
        Component b12 = new Component("b12", apache2, null);

        // b1
        ArrayList<Component> b1Deps = new ArrayList<>();
        b1Deps.add(b11);
        b1Deps.add(b12);
        Component b1 = new Component("b1", apache2, b1Deps);

        // b2
        Component b2 = new Component("b2", gpl2, null);

        // b
        ArrayList<Component> bDeps = new ArrayList<>();
        bDeps.add(b1);
        bDeps.add(b2);
        Component b = new Component("b", lgpl2, bDeps);


        // top
        ArrayList<Component> deps = new ArrayList<>();
        deps.add(a);
        deps.add(b);
        Component top = new Component("InvalidTop", apache2, deps);

        return top;
    }

    public static void println(String string) {
        System.out.println(string);
    }

    public static void print(String string) {
        System.out.print(String.format(beforeFormat, string));
    }

    public static void assertHelper(String before, boolean value, String after){
        print("  * " + before+": ");
        assert value;
        println(after);
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

}