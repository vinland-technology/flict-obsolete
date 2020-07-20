// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.domain;

import java.util.ArrayList;
import java.util.List;

public class LicenseGroup {

    private final String name;
    private final List<License> memberLicenses;

    public LicenseGroup(String name) {
        this.name = name;
        memberLicenses = new ArrayList<>();
    }

    public String name() {
        return name;
    }

    public void addLicense(License license) {
        memberLicenses.add(license);
    }

    public List<License> memberLicenses() {
        return memberLicenses;
    }

}
