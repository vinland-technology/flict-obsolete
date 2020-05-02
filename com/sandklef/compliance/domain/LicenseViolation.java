// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.domain;

import java.util.ArrayList;
import java.util.List;

public class LicenseViolation {

    public static class ObligationViolation {
        public Component user;

        public ObligationViolation(Component user) {
            this.user = user;
        }

        @Override
        public String toString() {
            return " " + user.name() + " (" + user.licenses() + ")" ;
        }
    }

    private Component component;

    private List<ObligationViolation> violatedObligations;

    public LicenseViolation(Component component) {
        this.component = component;
        violatedObligations = new ArrayList<>();
    }

    public LicenseViolation(Component component, List<ObligationViolation> violatedObligations) {
        this.component = component;
        this.violatedObligations = violatedObligations;
    }

    public void addObligationViolation(ObligationViolation violatedObligation) {
        violatedObligations.add(violatedObligation);
    }

    public Component component() {
        return component;
    }

    public List<ObligationViolation> obligations() {
        return violatedObligations;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(component.name());
        sb.append("\n");
        for (ObligationViolation o : violatedObligations) {
            sb.append(" * ");
            sb.append(o.user.name());
            if (o.user.concludedLicense()==null) {
                sb.append(" has no concluded license");
                sb.append(")\n");
            } else {
                sb.append(" cant use any of the licenses in ");
                sb.append("\n");
                for (Component c : o.user.dependencies()) {
                    sb.append("  * ");
                    sb.append(c.name());
                    sb.append(" (");
                    for (License l : c.licenses()) {
                        sb.append(l.spdxTag());
                        sb.append("  | ");
                    }
                    sb.append(")\n");
                }
            }
        }
        return sb.toString();
    }


}