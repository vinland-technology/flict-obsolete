// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.domain;

import com.sandklef.compliance.utils.Log;

import java.util.ArrayList;
import java.util.List;

public class LicenseConclusion {

    private static final String LOG_TAG = LicenseConclusion.class.getSimpleName() ;
    private Component component;
    private License license;
//    private List<LicenseConclusion2> licenseConclusions;
/*
    public static class LicenseConclusion2 {
        private Component component;
        private License license;
        public LicenseConclusion(Component c, License l) {
            component = c;
            license = l;
        }

        public Component component() {
            return component;
        }

        public License license() {
            return license;
        }

        @Override
        public String toString() {
            return  component + "  " + license;
        }
    }
*/
    /*
    public LicenseConclusion(Component component, License license) {
        this.component = component;
        this.license = license;
//        licenseConclusions = new ArrayList<>();
    }

    public Component component() {
        return component;
    }

   public License license() {
        return license;
    }

    @Override
    public String toString() {

        return component.name()  + " (" + license.spdx() + "  from: " + component.licenses() + ")";
    }
*/
    /*    public LicenseConclusion(Component component, List<LicenseConclusion> conclusions) {
        this.component = component;
        this.licenseConclusions = conclusions;
    }
*/
    /*public void addLicenseConclusion(LicenseConclusion licenseConclusion) {
        Log.d(LOG_TAG, " adding conclusion: " + licenseConclusion.component.name() + "  " + licenseConclusion.license.spdx());
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
            sb.append(lc.component().concludedLicense().spdx());
            sb.append("         from the following " + (lc.component().dualLicensed()?"dual ":"many ") + "licenses: ");
            sb.append(lc.component().licenses());
            sb.append("\n");
        }
        return sb.toString();
    }
*/

}