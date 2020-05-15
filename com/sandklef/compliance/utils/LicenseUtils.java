package com.sandklef.compliance.utils;

import com.sandklef.compliance.domain.LicenseConnector;

import java.io.PrintStream;

public class LicenseUtils {


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


}
