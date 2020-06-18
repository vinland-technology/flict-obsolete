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

        @Override
        public String toString() {
            return "\n[" +
                    "\n  color=" + color +
                    "\n  component=" + component +
                    "\n  compliant=" + compliant +
                    "]\n\n";
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

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("Report");
        sb.append("\n* component:");
        sb.append(component);
        sb.append("\n* policy:");
        sb.append(policy==null?"-":policy);
        sb.append("\n* metadata:");
        sb.append(metaData);
        sb.append("\n* ");
        sb.append("Component results:");
        for (ComponentResult cr : componentResults) {
            sb.append("\n  * [ ");
            sb.append(cr.compliant);
            sb.append("  | ");
            sb.append(cr.color);
            sb.append("  | ");
            sb.append(cr.component);
            sb.append(" ]");
        }
        return sb.toString();
    }
}