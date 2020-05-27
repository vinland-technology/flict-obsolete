// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.domain;

import com.sandklef.compliance.utils.LicenseArbiter;

import java.util.ArrayList;
import java.util.List;

public class Report {

    public static class ComponentResult {
        private ListType color;
        private LicenseArbiter.InterimComponent component;
        boolean compliant ;

        public ComponentResult(ListType color, LicenseArbiter.InterimComponent component, boolean compliant) {
            this.color = color;
            this.component = component;
            this.compliant = compliant;
        }

        public boolean compliant() {
            return compliant;
        }

        public ListType color() {
            return color;
        }

        public LicenseArbiter.InterimComponent component() {
            return component;
        }
    }

    private Component component;
    private LicensePolicy policy;
    private MetaData metaData;
    private List<ComponentResult> componentResults;

    public Report(Component component, LicensePolicy policy) {
        this.component = component;
        this.policy = policy;
        this.metaData = new MetaData();
        this.componentResults = new ArrayList<>();
    }

    public Component component() {
        return component;
    }

    public LicensePolicy policy() {
        return policy;
    }

    public MetaData metaData() {
        return metaData;
    }

    public void addComponentResult(ComponentResult result) {
        this.componentResults.add(result);
    }

    public List<ComponentResult> componentResults() {
        return componentResults;
    }

    public void finished() {
        metaData.finished();
    }

    public String duration() {
        return metaData.duration();
    }


}