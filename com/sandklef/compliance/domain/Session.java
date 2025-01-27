// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.domain;

public class Session {

    private String licenseDir;
    private String componentFile;
    private String policyFile;
    private String connectorFile;

    private String laterFile;

    private static Session instance;
    private Session() { }

    public static Session getInstance() {
        if (instance==null) {
            instance = new Session();
        }
        return instance;
    }

    public String lLicenseDir() {
        return licenseDir;
    }

    public void lLicenseDir(String licenseDir) {
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


}
