// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.domain;

public class IllegalLicenseExpression extends Exception {

    private LicenseExpression licenseExpression;

    public IllegalLicenseExpression(String s, LicenseExpression licenseExpression) {
        super(s);
        this.licenseExpression = licenseExpression;
    }

    public IllegalLicenseExpression(String s) {
        super(s);
    }

}
