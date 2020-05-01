// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.domain;

public class Report {

    public Violation violation;
    public Conclusion conclusion;

    public Report(Component c) {
        violation = new Violation(c);
        conclusion = new Conclusion(c);
    }

    public Conclusion conslusion() {
        return conclusion;
    }

    public Violation violation() {
        return violation;
    }

    public boolean hasViolation() {
        return violation.obligations().size()>0;
    }


}