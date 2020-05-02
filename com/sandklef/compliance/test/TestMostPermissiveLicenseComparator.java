// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.test;

import com.sandklef.compliance.domain.License;
import com.sandklef.compliance.domain.LicenseViolationException;
import com.sandklef.compliance.domain.Report;
import com.sandklef.compliance.json.JsonLicenseParser;
import com.sandklef.compliance.utils.LeastPermissiveLicenseComparator;
import com.sandklef.compliance.utils.LicenseArbiter;
import com.sandklef.compliance.utils.LicenseStore;
import com.sandklef.compliance.utils.MostPermissiveLicenseComparator;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.sandklef.compliance.domain.License.*;
import static com.sandklef.compliance.test.Utils.*;

public class TestMostPermissiveLicenseComparator {

    public static void test() {
        printTestStart("TestMostPermissiveLicenseComparator");
        List<License> licenses = new ArrayList<>();
        licenses.add(gpl2);
        licenses.add(apache2);
        licenses.add(lgpl2);
        printSubTestStart("MostPermissiveLicenseComparator");
        licenses.sort(MostPermissiveLicenseComparator.comparator);
        assertHelper("apache is most permissive", licenses.get(0).spdxTag().equals(apache2.spdxTag()));
        assertHelper("lgpl is next", licenses.get(1).spdxTag().equals(lgpl2.spdxTag()));
        assertHelper("gpl is the least", licenses.get(2).spdxTag().equals(gpl2.spdxTag()));
        printSubTestStart("LeastPermissiveLicenseComparator");
        licenses.sort(LeastPermissiveLicenseComparator.comparator);
        assertHelper("gpl is the least permissive", licenses.get(0).spdxTag().equals(gpl2.spdxTag()));
        assertHelper("lgpl is next", licenses.get(1).spdxTag().equals(lgpl2.spdxTag()));
        assertHelper("apache is most permissive", licenses.get(2).spdxTag().equals(apache2.spdxTag()));
    }

    public static void main(String[] args) {
        test();
    }

}