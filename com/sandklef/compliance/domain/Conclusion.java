// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.domain;

import com.sandklef.compliance.utils.Log;

import java.util.ArrayList;
import java.util.List;

public class Conclusion {

    private static final String LOG_TAG = Conclusion.class.getSimpleName() ;
    private Component component;
    private List<LicenseConclusion> licenseConclusions;


    public static class LicenseConclusion {
        private Component component;
        private License license;
        public LicenseConclusion(Component c, License l) {
            component = c;
            license = l;
        }
    }

    public Conclusion(Component component) {
        this.component = component;
        licenseConclusions = new ArrayList<>();
    }

    public Component component() {
        return component;
    }

    public Conclusion(Component component, List<LicenseConclusion> conclusions) {
        this.component = component;
        this.licenseConclusions = conclusions;
    }

    public void addLicenseConclusion(LicenseConclusion licenseConclusion) {
        Log.d(LOG_TAG, " adding conclusion: " + licenseConclusion.component.name() + "  " + licenseConclusion.license.spdxTag());
        licenseConclusions.add(licenseConclusion);
    }

    public List<LicenseConclusion> licenseConclusions() {
        return licenseConclusions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(component.name());
        sb.append("\n");
        for (LicenseConclusion lc : licenseConclusions) {
            sb.append(" * concluded license for \"");
            sb.append(lc.component.name() );
            sb.append("\": ");
            sb.append(lc.license.spdxTag());
            sb.append("         from the following licenses: ");
            sb.append(lc.component.licenses());
            sb.append("\n");
        }
        return sb.toString();
    }


}