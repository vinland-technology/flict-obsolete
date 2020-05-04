// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.domain;

import java.util.ArrayList;
import java.util.List;

public class LicenseViolation {

    public static class ObligationViolation {
        public Component user;
        public List<Component> violatedComponents;

        public ObligationViolation(Component user, List<Component> violations) {
            this.user = user;
            this.violatedComponents = violations;
        }

        @Override
        public String toString() {
            return " " + user.name() + " (" + user.licenses() + ") + [" + violatedComponents + "]";
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


            if (o.user.concludedLicense() == null) {
            //    sb.append(" has no concluded license or a violation detected\n");
            } else {
                throw new IllegalArgumentException("Damn it .... we should not be here. Can't be a violation and still have a concluded license");

            }

/*
            if (o.violatedComponents==null) {
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
                } else {
                */
            if (o.violatedComponents.size() > 0) {
                sb.append(" cant use any of the licenses in:\n");
                for (Component ov : o.violatedComponents) {
                    sb.append("   * ");
                    sb.append(ov);
                    sb.append("\n");
                }
            } else {
                throw new IllegalArgumentException("Damn it .... we should not be here. Can't be a violation with no violations?");
            }
        }

        return sb.toString();
    }


}