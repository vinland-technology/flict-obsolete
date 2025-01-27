// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.domain;

import com.sandklef.compliance.utils.Log;

import java.util.HashSet;
import java.util.Set;

public class LicenseCompatibility {

    private static final String LOG_TAG = LicenseCompatibility.class.getSimpleName();

    private License license;
    private LicenseGroup licenseGroup;
    private final Set<LicenseCompatibility> canUse;
    private final Set<LicenseCompatibility> canBeUsedBy;

    public static class LicenseConnectorException extends Exception {
        public LicenseConnectorException(String msg) {
            super(msg);
        }
    }

    public LicenseCompatibility(License license) {
        this.license = license;
        canUse = new HashSet<>();
        canBeUsedBy = new HashSet<>();
    }

    public LicenseCompatibility(LicenseGroup licenseGroup) {
        this.licenseGroup = licenseGroup;
        canUse = new HashSet<>();
        canBeUsedBy = new HashSet<>();
    }

    public LicenseCompatibility(License license, Set<LicenseCompatibility> canUse, Set<LicenseCompatibility> canBeUsedBy) {
        this.license = license;
        this.canUse = canUse;
        this.canBeUsedBy = canBeUsedBy;
    }

    private void valid() throws LicenseConnectorException {
        if (license!=null && licenseGroup!=null) {
            throw new LicenseConnectorException("A connector can't contain both License (" + license +") and LicenseGroup (" + licenseGroup + ")");
        }
        if (license==null && licenseGroup==null) {
            throw new LicenseConnectorException("A connector must contain either License or LicenseGroup");
        }
    }

    public boolean hasLicense() throws LicenseConnectorException {
        valid();
        return license!=null;
    }

    public License license() throws LicenseConnectorException {
        valid();
        return license;
    }

    public LicenseGroup licenseGroup() throws LicenseConnectorException {
        valid();
        return licenseGroup;
    }

    public boolean contains(License license) throws LicenseConnectorException {
        valid();
        if (hasLicense()) {
            throw new LicenseConnectorException("Can't check license unless LicenseConnector contains a LicenseGroup");
        }
        return licenseGroup.memberLicenses().contains(license);
    }

    public Set<LicenseCompatibility> canUse() {
        return canUse;
    }

    public Set<LicenseCompatibility> canBeUsedBy() {
        return canBeUsedBy;
    }

    public void addCanUse(LicenseCompatibility licenseConnector) {
        canUse.add(licenseConnector);
        licenseConnector.canBeUsedBy.add(this);
    }

    public void canBeUsedBy(LicenseCompatibility licenseConnector) {
        licenseConnector.canUse.add(this);
        this.canBeUsedBy.add(licenseConnector);
        Log.d(LOG_TAG, "canBeUsedBy: " + licenseConnector );
    }

    @Override
    public String toString() {
        if (license!=null) {
            return "LicenseConnector{" +
                    "license=" + license.spdx() +
                    "}";
        } else {
            return "LicenseConnector{" +
                    "licenseGroup=" + licenseGroup.name() +
                    "}";
        }
    }
}
