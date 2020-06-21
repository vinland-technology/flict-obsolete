package com.sandklef.compliance.utils;

import com.sandklef.compliance.domain.*;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LicenseUtils {


    private static final String LOG_TAG = LicenseUtils.class.getSimpleName();

    public static String listCanUse(LicenseConnector connector) throws LicenseConnector.LicenseConnectorException {
        return listCanUse(connector, 0);
    }
    private static String listCanUse(LicenseConnector connector, int level) throws LicenseConnector.LicenseConnectorException {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<level; i++) {
            sb.append("| ");
        }
        if (connector.hasLicense()) {
            sb.append(connector.license().spdx());
        } else {
            sb.append(connector.licenseGroup().name());
        }
        sb.append("\n");
        for (LicenseConnector l : connector.canUse()){
            sb.append(listCanUse(l, level+1));
        }
        return sb.toString();
    }

    public static String listCanBeUsedBy(LicenseConnector connector) throws LicenseConnector.LicenseConnectorException {
        return listCanBeUsedBy(connector, 0);
    }

    private static String listCanBeUsedBy(LicenseConnector connector, int level) throws LicenseConnector.LicenseConnectorException {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<level; i++) {
            sb.append("| ");
        }
        if (connector.hasLicense()) {
            sb.append(connector.license().spdx());
        } else {
            sb.append(connector.licenseGroup().name());
        }
        sb.append("\n");
        for (LicenseConnector l : connector.canBeUsedBy()){
            sb.append(listCanBeUsedBy(l, level+1));
        }
        return sb.toString();
    }

    public static void connectionsPrintDot(PrintStream writer) throws LicenseConnector.LicenseConnectorException {
        writer.println("digraph depends {\nnode [shape=plaintext]");
        for ( LicenseConnector lc : LicenseStore.getInstance().connectors().values()) {
            for (LicenseConnector l : lc.canBeUsedBy()) {
                String from = lc.hasLicense()?lc.license().spdx():lc.licenseGroup().name();
                String to = l.hasLicense()?l.license().spdx():l.licenseGroup().name();

                writer.println("\"" + from +  "\" ->  \"" + to + "\"");

            }
        }
        writer.println("}");
    }


    private static class MiniComponent {
        public Component component;
        public List<MiniComponent>dependencies;
        public String license;
        public ListType color;
        public int id;

        private static int counter = 200;

        public static int next() { return ++counter; }

        public MiniComponent(Component component, String license, ListType color) {
            this.component = component;
            this.license = license;
            this.color = color;
            this.id = counter++;
            dependencies = new ArrayList<>();
        }

        @Override
        public String toString() {
            return "{ " +
                    " " + id +
                    ",  " + component.name() +
                    ",  " + license +
                    ",  " + dependencies +
                    "}";
        }

        public MiniComponent clone() {
            MiniComponent mc = new MiniComponent(component, license, color);
            mc.id = this.id;
            for (MiniComponent d : dependencies) {
                mc.dependencies.add(d.clone());
            }
            return mc;
        }

        public MiniComponent find(int id) {
            if (this.id == id ) {
                return this;
            }
            for (MiniComponent d : dependencies) {
                if ( d.find(id) != null ) {
                    return d;
                }
            }
            return null;
        }




    }

    public static void verifyLicenses() throws LicenseConnector.LicenseConnectorException {
        Log.d(LOG_TAG, "Verify Licenses and connectors");
        // Verify all licenses either is part of a connector or
        // is part of a group that is part of a connector
        Log.d(LOG_TAG, " * Licenses are part of a connector:");
        for (License license : LicenseStore.getInstance().licenses().values() ) {
            Log.dn(LOG_TAG, "   * " + license.spdx() + ": ");
            String key = null;
            if (license.licenseGroup()!=null) {
                key = license.licenseGroup();
            } else {
                key = license.spdx();
            }
            LicenseConnector c = LicenseStore.getInstance().connectors().get(key);
            if (c != null) {
                Log.d(null, " OK");
            } else {
                throw new LicenseConnector.LicenseConnectorException("Missing connector for: " + key);
            }
        }
    }

    public static void stupidifier(Component c,
                                   Map<String, Map<Component, List<ComplianceAnswer>>> answers) {
        ArrayList<MiniComponent> components = new ArrayList<>();
        stupidifierHelper(components, answers, null, null);
    }


    public static void stupidifierHelper(ArrayList<MiniComponent> topList,
                                         Map<String, Map<Component, List<ComplianceAnswer>>> answers,
                                         MiniComponent topMc,
                                         MiniComponent parentMc) {

        int i =0;
        Log.d(LOG_TAG, " LIST print " + topList.size() + "  add to : " + (parentMc==null?0:parentMc.id));
        for (MiniComponent mc : topList) {
            Log.d(LOG_TAG, " * LIST print " + i++ + ": " + mc);
        }
        Log.d(LOG_TAG, " * LIST print ");

        int parentId = -1;
        if (parentMc!=null) {
            parentId = parentMc.id;
        }


        if (answers.entrySet().size() > 0) {

            Log.d(LOG_TAG, " ============================================================= PREPARE ADDING LICENSE " + answers.entrySet().size() + "    parentId: " + parentId);

            int outer=0;
            // For every license
            for (Map.Entry<String, Map<Component, List<ComplianceAnswer>>> entry : answers.entrySet()) {

                outer++;
                // license (spdx)
                String license = entry.getKey();
                // Map<Component, List<ComplianceAnswer>> for above license
                Object mapCL = entry.getValue();
                Log.d(LOG_TAG, " ============================================================= SOON   ADDING LICENSE " +
                        answers.entrySet().size() + " | " +
                        ((Map<Component, List<ComplianceAnswer>>) mapCL).entrySet().size() );

                if (parentMc!=null)
                Log.d(LOG_TAG, " ============================================================= ADDING LICENSE " +
                         license + " out: "  + outer + " " +
                        "\"" + parentMc.component.name() + "\"   size: " +
                        ((Map<Component, List<ComplianceAnswer>>) mapCL).entrySet().size());

                if (((Map<Component, List<ComplianceAnswer>>) mapCL).entrySet().size() > 0) {

                    int added = -1;

                    // Loop through components (key)
                    for (Map.Entry<Component, List<ComplianceAnswer>> entry2 : ((Map<Component, List<ComplianceAnswer>>) mapCL).entrySet()) {
                        added++;

                        // Component to add
                        Component c = entry2.getKey();
                        Log.d(LOG_TAG, " component: " + c.name() + " ");
                        MiniComponent mc = new MiniComponent(c, license, ListType.ALLOWED_LIST);

                        if (parentMc==null) {
                            // Top component: add to list
                            Log.d(LOG_TAG, " ============================================================= ADDING TOP 0    " + mc.component.name() + " to " + "---" + " " + added + " " +outer + " " + mc);
                            topList.add(mc);
                            topMc = mc;
                        }  else {
                            Log.d(LOG_TAG, " ============================================================= ADDING TOP -    " + mc.component.name() + " to " + parentMc.id + " " + added + " " +outer + " " + mc);
                            if (outer==1) {
                                parentId = parentMc.id;
                                parentMc.dependencies.add(mc);
                                Log.d(LOG_TAG, " ============================================================= ADDING TOP 1 " + mc.component.name() + " to " + parentMc.id + " " + added + " " +outer);
                            } else {
                                MiniComponent newTopMc = topMc.clone();
                                newTopMc = newTopMc.find(parentId);
                                topList.add(newTopMc);
                                newTopMc.id =newTopMc.id+1000;
                                        Log.d(LOG_TAG, " ============================================================= ADDING TOP 2 newTopMc: " + newTopMc + " from " + parentId);
                             /*   newTopMc.id = MiniComponent.next();
                                topList.add(newTopMc);
                                newTopMc.license = " DONKEY BALLS";
                                newTopMc.id = MiniComponent.next();
                                newTopMc.dependencies.add(mc);
*/
                                topMc = newTopMc;

                            }

                            // Not top component
//                            parentMc.dependencies.add(mc);
                        }
                        Log.d(LOG_TAG, " ============================================================= " );
                        Log.d(LOG_TAG, "list: " + topList.size() + " " + topList);

                        // Dependencies
                        for (ComplianceAnswer ca : entry2.getValue()) {
                            Log.d(LOG_TAG, "  --- " + ca.answers().size());
                           stupidifierHelper(topList, ca.answers(), topMc, mc);
                        }
                    }
                }
            }
        } else {
            Log.d(LOG_TAG, "    OK");
        }
    }
}
