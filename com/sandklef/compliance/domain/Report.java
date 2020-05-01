// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.domain;

public class Report {

    public LicenseViolation violation;
    public Conclusion conclusion;
    public Concern concern;

    public Report(Component c) {
        violation = new LicenseViolation(c);
        conclusion = new Conclusion(c);
        this.concern = new Concern(c);
    }

    public Concern concern() {
        return concern;
    }

    public Conclusion conslusion() {
        return conclusion;
    }

    public LicenseViolation violation() {
        return violation;
    }

    public boolean hasViolation() {
        return violation.obligations().size()>0;
    }


}