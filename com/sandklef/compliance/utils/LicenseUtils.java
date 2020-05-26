package com.sandklef.compliance.utils;

import com.sandklef.compliance.domain.ComplianceAnswer;
import com.sandklef.compliance.domain.Component;
import com.sandklef.compliance.domain.LicenseConnector;
import com.sandklef.compliance.domain.ListType;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LicenseUtils {


    private static final String LOG = LicenseUtils.class.getSimpleName();

    public static String listCanUse(LicenseConnector connector) {
        return listCanUse(connector, 0);
    }
    private static String listCanUse(LicenseConnector connector, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<level; i++) {
            sb.append("| ");
        }
        sb.append(connector.license().spdx());
        sb.append("\n");
        for (LicenseConnector l : connector.canUse()){
            sb.append(listCanUse(l, level+1));
        }
        return sb.toString();
    }

    public static String listCanBeUsedBy(LicenseConnector connector) {
        return listCanBeUsedBy(connector, 0);
    }

    private static String listCanBeUsedBy(LicenseConnector connector, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<level; i++) {
            sb.append("| ");
        }
        sb.append(connector.license().spdx());
        sb.append("\n");
        for (LicenseConnector l : connector.canBeUsedBy()){
            sb.append(listCanBeUsedBy(l, level+1));
        }
        return sb.toString();
    }

    public static void connectionsPrintDot(PrintStream writer) {
        writer.println("digraph depends {\nnode [shape=plaintext]");
        for ( LicenseConnector lc : LicenseStore.getInstance().connectors().values()) {
            for (LicenseConnector l : lc.canBeUsedBy()) {
                writer.println("\"" + lc.license().spdx() +  "\" ->  \"" + l.license().spdx() + "\"");
            }
        }
        writer.println("}");
    }


    private static class MiniComponent {
        public Component component;
        public List<MiniComponent>dependencies;
        public String license;
        public ListType color;

        public MiniComponent(Component component, String license, ListType color) {
            this.component = component;
            this.license = license;
            this.color = color;
            dependencies = new ArrayList<>();
        }

        @Override
        public String toString() {
            return "{ " +
                    component.name() +
                    ", " + dependencies +
                    ", " + license +
                    "}";
        }
    }

    public static void stupidifier(Component c,
                                   Map<String, Map<Component, List<ComplianceAnswer>>> answers) {
        ArrayList<MiniComponent> components = new ArrayList<>();
        Log.level(Log.DEBUG);
        stupidifierHelper(components, answers, null);
    }


    public static void stupidifierHelper(ArrayList<MiniComponent> topList,
                                         Map<String, Map<Component, List<ComplianceAnswer>>> answers,
                                         MiniComponent parentMc) {


        if (answers.entrySet().size() > 0) {
            for (Map.Entry<String, Map<Component, List<ComplianceAnswer>>> entry : answers.entrySet()) {
                String license = entry.getKey();
                Object mapCL = entry.getValue();
                if (((Map<Component, List<ComplianceAnswer>>) mapCL).entrySet().size() > 0) {
//                    Log.d(LOG, "license: " + license);
                    //                  Log.d(LOG, "color: " + color);


                    for (Map.Entry<Component, List<ComplianceAnswer>> entry2 : ((Map<Component, List<ComplianceAnswer>>) mapCL).entrySet()) {

                        // Component to add
                        Component c = entry2.getKey();
                        Log.d(LOG, " component: " + c.name() + " ");
                        MiniComponent mc = new MiniComponent(c, license, ListType.WHITE_LIST);

                        if (parentMc==null) {
                            // Top component: add to list
                            topList.add(mc);
                        }  else {
                            // Not top component
                            Log.d(LOG, " ============================================================= " );
                            //ArrayList<MiniComponent> listCopy = (ArrayList<MiniComponent>) topList.clone();
                            parentMc.dependencies.add(mc);
                        }
                        Log.d(LOG, "list: " + topList);

                        // Dependencies
                        for (ComplianceAnswer ca : entry2.getValue()) {
                            Log.d(LOG, "  --- " + ca.answers().size());
                            stupidifierHelper(topList, ca.answers(), mc);
                        }
                    }
                }
            }
        } else {
            Log.d(LOG, "    OK");
        }
    }
}
