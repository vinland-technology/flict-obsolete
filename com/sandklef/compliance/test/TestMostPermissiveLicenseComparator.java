// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.test;

public class TestMostPermissiveLicenseComparator {


    public static void test() {
    /*    printTestStart("TestMostPermissiveLicenseComparator");
        List<License> licenses = new ArrayList<>();
        licenses.add(gpl20);
        licenses.add(apache20);
        licenses.add(lgpl20);
        printSubTestStart("MostPermissiveLicenseComparator");
        licenses.sort(MostPermissiveLicenseComparator.comparator);
        assertHelper("apache is most permissive", licenses.get(0).spdxTag().equals(apache20.spdxTag()));
        assertHelper("lgpl is next", licenses.get(1).spdxTag().equals(lgpl20.spdxTag()));
        assertHelper("gpl is the least", licenses.get(2).spdxTag().equals(gpl20.spdxTag()));
        printSubTestStart("LeastPermissiveLicenseComparator");
        licenses.sort(LeastPermissiveLicenseComparator.comparator);
        assertHelper("gpl is the least permissive", licenses.get(0).spdxTag().equals(gpl20.spdxTag()));
        assertHelper("lgpl is next", licenses.get(1).spdxTag().equals(lgpl20.spdxTag()));
        assertHelper("apache is most permissive", licenses.get(2).spdxTag().equals(apache20.spdxTag()));
    */
    }

    public static void main(String[] args) {
        test();
    }

}