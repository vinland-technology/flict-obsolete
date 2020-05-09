// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.domain;

import java.util.ArrayList;
import java.util.List;

public class LicenseObligationViolation {

    private Component user;
/*    private Component used;
    private License violatedLicense;
*/
    public LicenseObligationViolation(Component user /*, Component used, License license*/) {
        this.user = user;
/*        this.used = used;
        this.violatedLicense = license;
        */
    }

    @Override
    public String toString() {
//        return " " + user.name() + " violates  " + used.name() + "[" + violatedLicense + "]";
        return user.name();
    }

    public Component user() {
        return user;
    }

/*    public Component used() {
        return used;
    }

    public License violatedLicense() {
        return violatedLicense;
    }

 */
}
    /*
    private Component component;

    private List<ObligationViolation> violatedObligations;

    public LicenseObligationViolation(Component component) {
        this.component = component;
        violatedObligations = new ArrayList<>();
    }

    public LicenseObligationViolation(Component component, List<ObligationViolation> violatedObligations) {
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

            if (o.user.dualLicensed()) {
                sb.append(" concluded license: (");
                if (o.user.concludedLicense() == null) {
                    sb.append(" no license concluded ");
                    //    sb.append(" has no concluded license or a violation detected\n");
                } else {
                    sb.append(o.user);
                }
                sb.append(" )");

                if (o.violatedComponents != null && o.violatedComponents.size() > 0) {
                    sb.append(" cant use any of the licenses in:\n");
                    for (Component ov : o.violatedComponents) {
                        sb.append("   * ");
                        sb.append(ov);
                        sb.append("\n");
                    }
                } else {
//                throw new IllegalArgumentException("Damn it .... we should not be here. Can't be a violation with no violations?");
                    //TODO: print
                    System.out.println("  Liverpool ---------------------------- " + o.user.name() + " " + o.violatedComponents);
                }
            } else {
                if (o.blackListed != null && o.blackListed.size() > 0) {
                    sb.append("  found black listed: (");
                    for (License l : o.blackListed) {
                        sb.append(l.spdxTag());
                    }
                    sb.append(")");
                }
            }
        }

        return sb.toString();
    }


}

     */