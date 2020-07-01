// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.utils;

public class Log {

    public static int ERROR = 0;
    public static int WARNING = 1;
    public static int INFO = 2;
    public static int DEBUG = 3;
    public static int VERBOSE = 4;

    private static int currentLevel = WARNING;
    private static String tagFilter;
    private static String messageFilter;


    private static boolean doPrint(int level, String tag, String message) {
        boolean doPrint = false;
        if (level <= currentLevel) {
            if ((tagFilter == null) && (messageFilter == null)) {
                doPrint = true;
            } else if (tagFilter != null) {
                if (tag.contains(tagFilter)) {
                    doPrint = true;
                }
            } else /*if (messageFilter != null)*/ {
                if (message.contains(messageFilter)) {
                    doPrint = true;
                }
            }
        }
      //  System.out.println("  doPrint  tag:" + tag + "   " + "  message: " + messageFilter + "  ==> " + doPrint);
        return doPrint;
    }

    private static void println(int level, String tag, String message) {
        //    System.err.println("COMPARE : " + level + " with " + currentLevel);
        //System.out.println("  LOG: " + level +  "  " +  tag + "  filter: " + filter);
        if (doPrint(level, tag, message)) {
            if (tag!=null) {
                System.err.println("[" + tag + "]: " + message);
            } else {
                System.err.println(message);
            }
        }
    }


    private static void print(int level, String tag, String message) {
        if (doPrint(level, tag, message)) {
                System.err.print("[" + tag + "]: " + message);
        }
    }

    public static void filterTag(String filter) {
        Log.tagFilter = filter;
    }

    public static void filterMessage(String filter) {
        Log.messageFilter = filter;
    }

    public static void d(String tag, String message) {
        println(DEBUG, tag, message);
    }

    public static void dn(String tag, String message) {
        print(DEBUG, tag, message);
    }

    public static void v(String tag, String message) {
        println(VERBOSE, tag, message);
    }

    public static void i(String tag, String message) {
        println(INFO, tag, message);
    }

    public static void e(String tag, String message) {
        println(ERROR, tag, message);
    }

    public static void level(int level) {
        currentLevel = level;
    }

    public static void debug(String method, String msg) {
        Log.d(method + "." + method, msg);
    }

    public static String indents(int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("  ");
        }
        return sb.toString();
    }

    public static void debug(String method, String msg, int indent) {
        Log.d(method + method, indents(indent) + msg);
    }

}
