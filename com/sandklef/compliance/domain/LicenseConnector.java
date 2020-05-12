package com.sandklef.compliance.domain;

import java.util.HashSet;
import java.util.Set;

public class LicenseConnector {

    private License license;
    private Set<LicenseConnector> canUse;
    private Set<LicenseConnector> canBeUsedBy;

    public LicenseConnector(License license) {
        this.license = license;
        canUse = new HashSet<>();
        canBeUsedBy = new HashSet<>();
    }

    public LicenseConnector(License license, Set<LicenseConnector> canUse, Set<LicenseConnector> canBeUsedBy) {
        this.license = license;
        this.canUse = canUse;
        this.canBeUsedBy = canBeUsedBy;
    }

    public License license() {
        return license;
    }

    public Set<LicenseConnector> canUse() {
        return canUse;
    }

    public Set<LicenseConnector> canBeUsedBy() {
        return canBeUsedBy;
    }

    public void addCanUse(LicenseConnector licenseConnector) {
        canUse.add(licenseConnector);
        licenseConnector.canBeUsedBy.add(this);
    }

    public void canBeUsedBy(LicenseConnector licenseConnector) {
        licenseConnector.canUse.add(this);
        this.canBeUsedBy.add(licenseConnector);
    }

    @Override
    public String toString() {
        return "LicenseConnector{" +
                "license=" + license.spdxTag() +
                "}";
    }
}
