// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.domain;

import java.util.ArrayList;
import java.util.List;

public class Report {

    private Component component;
    public List<LicenseObligationViolation> violations;
    public List<LicenseConclusion> conclusions;
    private List<PolicyConcern> concerns;
    private List<PolicyViolation> policyViolations;

    public Report(Component component) {
        this.component = component;
        violations = new ArrayList<>();
        conclusions = new ArrayList<>();
        concerns = new ArrayList<>();
        policyViolations = new ArrayList<>();
    }

    //
    // add
    //
    public void addLicenseObligationViolation(LicenseObligationViolation violation) {
        violations.add(violation);
    }

    public void addLicenseConcern(PolicyConcern concern) {
        concerns.add(concern);
    }

    public void addLicenseConclusion(LicenseConclusion conclusion) {
        conclusions.add(conclusion);
    }

    public void addPolicyViolation(PolicyViolation violations) {
        policyViolations.add(violations);
    }

    //
    // get
    //
    public List<PolicyConcern> concerns() {
        return concerns;
    }

    public List<PolicyViolation> policyViolations() {
        return policyViolations;
    }

    public List<LicenseConclusion> conclusions() {
        return conclusions;
    }

    public List<LicenseObligationViolation> violations() {
        return violations;
    }

    public Component component() {
        return component;
    }


    // has
    public boolean licenseViolation() {
        return violations.size()>0;
    }

    public boolean policyViolation() {
        return policyViolations.size()>0;
    }

    public boolean hasViolations() {
         return policyViolation() || licenseViolation();
    }


}