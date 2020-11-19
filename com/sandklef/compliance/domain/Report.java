// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.domain;

import com.sandklef.compliance.arbiter.LicenseArbiter;
import com.sandklef.compliance.utils.ComponentArbiter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Report {

    public static class ComponentResult {
        private final ListType type;
        private final ComponentArbiter.InterimComponent component;
        boolean compliant ;

        public ComponentResult(ListType type, ComponentArbiter.InterimComponent component, boolean compliant) {
            this.type = type;
            this.component = component;
            this.compliant = compliant;
        }

        public boolean compliant() {
            return compliant;
        }

        public ListType type() {
            return type;
        }

        public ComponentArbiter.InterimComponent component() {
            return component;
        }



        @Override
        public String toString() {
            return "" +
                    "\n  policy:    " + type +
                    "\n  compliant: " + compliant +
                    "\n  component: \n" + component +
                    "\n\n";
        }
    }

    private final Component component;
    private final LicensePolicy policy;
    private final MetaData metaData;
    private final LicenseArbiter arbiter;
    private final List<ComponentResult> componentResults;

    public Report(Component component, LicensePolicy policy, LicenseArbiter arbiter) {
        this.component = component;
        this.policy = policy;
        this.metaData = new MetaData();
        this.arbiter = arbiter;
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

    public LicenseArbiter arbiter() { return arbiter; }

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

    public List<ComponentResult> complianAllowedtPaths() {
        return componentResults.stream().
                filter(c -> c.compliant).
                filter(c -> c.type == ListType.ALLOWED_LIST).
                collect(Collectors.toList());
    }

    public List<ComponentResult> compliantPaths() {
        return componentResults.stream().
                filter(c -> c.compliant).
                filter(c -> c.type != ListType.DENIED_LIST).
                collect(Collectors.toList());
    }

    public List<ComponentResult> compliantAvoidPaths() {
        return componentResults.stream().
                filter(c -> c.compliant()).
                filter(c -> c.type == ListType.AVOID_LIST).
                collect(Collectors.toList());
    }

    public List<ComponentResult> compliantDeniedPaths() {
        return componentResults.stream().
                filter(c -> c.compliant()).
                filter(c -> c.type == ListType.DENIED_LIST).
                collect(Collectors.toList());
    }

    public List<ComponentResult> nonCompliantPaths() {
        return componentResults.stream().
                filter(c -> !c.compliant() || c.type == ListType.DENIED_LIST).
                collect(Collectors.toList());
    }

    public int compliantCount() {
        return compliantPaths().size();
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
            sb.append(cr.type);
            sb.append("  | ");
            sb.append(cr.component);
            sb.append(" ]");
        }
        return sb.toString();
    }
}
