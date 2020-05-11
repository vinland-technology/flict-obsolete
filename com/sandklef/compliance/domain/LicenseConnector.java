package com.sandklef.compliance.domain;

import java.util.HashSet;
import java.util.Set;

public class LicenseConnector {

    private License license;
    private Set<License> canUse;
    private Set<License> canBeUsedBy;

    public LicenseConnector(License license) {
        this.license = license;
        canUse = new HashSet<>();
        canBeUsedBy = new HashSet<>();
    }

    public LicenseConnector(License license, Set<License> canUse, Set<License> canBeUsedBy) {
        this.license = license;
        this.canUse = canUse;
        this.canBeUsedBy = canBeUsedBy;
    }

    public License license() {
        return license;
    }

    public Set<License> canUse() {
        return canUse;
    }

    public Set<License> canBeUsedBy() {
        return canBeUsedBy;
    }

    public void addCanUse(LicenseConnector licenseConnector) {
        canUse.add(licenseConnector.license());
        licenseConnector.canBeUsedBy.add(this.license());
    }

    public void canBeUsedBy(LicenseConnector licenseConnector) {
        licenseConnector.canUse.add(this.license());
        this.canBeUsedBy.add(licenseConnector.license());
    }

}
