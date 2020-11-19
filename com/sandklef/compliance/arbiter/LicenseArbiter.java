package com.sandklef.compliance.arbiter;

import com.sandklef.compliance.domain.IllegalLicenseExpression;
import com.sandklef.compliance.domain.License;
import com.sandklef.compliance.domain.LicenseCompatibility;
import com.sandklef.compliance.utils.LicenseStore;
import com.sandklef.compliance.utils.Log;

import java.util.ArrayList;
import java.util.List;

public interface LicenseArbiter {

    boolean aCanUseB(License a, List<License> bLicenses) throws IllegalLicenseExpression, LicenseCompatibility.LicenseConnectorException;
    boolean aCanUseB(License a, License b) throws IllegalLicenseExpression, LicenseCompatibility.LicenseConnectorException;
    String name();

}
