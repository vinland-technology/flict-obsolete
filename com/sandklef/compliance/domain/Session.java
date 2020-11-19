// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.domain;

import com.sandklef.compliance.arbiter.LicenseArbiterFactory;

public class Session {

    private String licenseDir;
    private String componentFile;
    private String policyFile;
    private String connectorFile;
    private String matrixFile;

    private String laterFile;
    private LicenseArbiterFactory.LICENSE_ARBITER_MODE arbiterMode;

    private static Session instance;
    private Session() {
        arbiterMode = LicenseArbiterFactory.LICENSE_ARBITER_MODE.LICENSE_ARBITER_MODE_UNSET;
    }

    public static Session getInstance() {
        if (instance==null) {
            instance = new Session();
        }
        return instance;
    }

    public LicenseArbiterFactory.LICENSE_ARBITER_MODE arbiterMode() {
        return arbiterMode;
    }

    public void arbiterMode(LicenseArbiterFactory.LICENSE_ARBITER_MODE arbiterMode) {
        this.arbiterMode = arbiterMode;
    }

    public String licenseDir() {
        return licenseDir;
    }

    public void licenseDir(String licenseDir) {
        this.licenseDir = licenseDir;
    }

    public String componentFile() {
        return componentFile;
    }

    public void componentFile(String componentFile) {
        this.componentFile = componentFile;
    }

    public String policyFile() {
        return policyFile;
    }

    public void policyFile(String policyFile) {
        this.policyFile = policyFile;
    }

    public String connectorFile() {
        return connectorFile;
    }

    public void connectorFile(String connectorFile) {
        this.connectorFile = connectorFile;
    }

    public String laterFile() {
        return laterFile;
    }

    public void laterFile(String laterFile) {
        this.laterFile = laterFile;
    }

    public String matrixFile() {
        return matrixFile;
    }

    public void matrixFile(String matrixFile) {
        this.matrixFile = matrixFile;
    }
}
