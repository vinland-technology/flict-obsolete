package com.sandklef.compliance.domain;

import com.sandklef.compliance.arbiter.LicenseArbiter;
import com.sandklef.compliance.utils.ComponentPileArbiter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class PileComplianceReport {

    public static class ComponentStatus {
        private boolean compliant;
        private List<License> outboundLicenseCoice;
        private Map<Component, List<List<License>>> map;
        private List<License> avoidLicenses;
        private List<License> deniedLicenses;

        public ComponentStatus() {
            outboundLicenseCoice = new ArrayList<>();
        }

        public boolean compliant() {
            return compliant;
        }

        public void compliant(boolean compliant) {
            this.compliant = compliant;
        }

        public List<License> outboundLicenseCoice() {
            return outboundLicenseCoice;
        }

        public void outboundLicenseCoice(List<License> outboundLicenseCoice) {
            this.outboundLicenseCoice = outboundLicenseCoice;
        }

        public Map<Component, List<List<License>>> map() {
            return map;
        }

        public void map(Map<Component, List<List<License>>> map) {
            this.map = map;
        }

        public List<License> avoidLicenses() {
            return avoidLicenses;
        }

        public void avoidLicenses(List<License> avoidLicenses) {
            this.avoidLicenses = avoidLicenses;
        }

        public List<License> deniedLicenses() {
            return deniedLicenses;
        }

        public void deniedLicenses(List<License> deniedLicenses) {
            this.deniedLicenses = deniedLicenses;
        }
    }


    private final Component component;
    private final LicensePolicy policy;
    private final String laterFile;
    private final MetaData metaData;
    private final LicenseArbiter arbiter;
    private List<ComponentStatus> statuses;

    public MetaData metaData() {
        return metaData;
    }

    public List<ComponentStatus> compliantComponents() {
        return predicatedComponents(c -> c.compliant && c.avoidLicenses.size() == 0 && c.deniedLicenses.size() == 0 );
    }

    public List<ComponentStatus> compliantAvoidedComponents() {
        return predicatedComponents(c -> c.compliant && c.avoidLicenses.size() > 0);
    }

    public List<ComponentStatus> compliantDeniedComponents() {
        return predicatedComponents(c -> c.compliant && c.deniedLicenses.size() > 0);
    }

    public List<ComponentStatus> inCompliantComponents() {
        return predicatedComponents(c -> !c.compliant);
    }

    public List<ComponentStatus> predicatedComponents(Predicate<ComponentStatus> p) {
        return statuses.stream().filter(c -> p.test(c)).collect(Collectors.toList());
    }

    public Component component() {
        return component;
    }

    public LicensePolicy policy() {
        return policy;
    }

    public LicenseArbiter arbiter() {
        return arbiter;
    }

    public List<ComponentStatus> statuses() {
        return statuses;
    }

    public String laterFile() {
        return laterFile;
    }

    public PileComplianceReport(Component component, LicensePolicy policy, String laterFile, MetaData metaData, LicenseArbiter arbiter) {
        this.component = component;
        this.policy = policy;
        this.metaData = metaData;
        this.arbiter = arbiter;
        this.laterFile = laterFile;
        statuses = new ArrayList<>();
    }

    public PileComplianceReport(Component component,
                                LicensePolicy policy,
                                String laterFile,
                                MetaData metaData,
                                LicenseArbiter arbiter,
                                List<ComponentStatus> statuses) {
        this(component, policy, laterFile, metaData, arbiter);
        this.statuses = statuses;
    }

    public void addComponentStatus(ComponentStatus cs) {
        statuses.add(cs);
    }

    public int combinations() {
        if (statuses == null) {
            return -1;
        }
        return statuses.size();
    }

    public boolean compliantWithAllowed() {
        return compliantComponents().size() > 0;
    }

    public boolean compliantWithAvoided() {
        return compliantAvoidedComponents().size() > 0;
    }

    public boolean compliantWithDenied() {
        return compliantDeniedComponents().size() > 0;
    }

    public boolean inCompliant() {
        return (!compliantWithAllowed()) || (!compliantWithAvoided());
    }

    public List<List<License>> compliantAllowed() {
        return compliant(s -> s.avoidLicenses().size() == 0);
    }

    public List<List<License>> compliantAvoid() {
        return compliant(s -> s.avoidLicenses.size() > 0);
    }

    private List<List<License>> compliant(Predicate<ComponentStatus> p) {
        List<List<License>> list = new ArrayList<>();
        for (ComponentStatus status : statuses()) {
            if (status.compliant && status.deniedLicenses.size()==0) {
                if (p.test(status)) {
                    list.add(status.outboundLicenseCoice());
                }
            }
        }
        return list.stream().distinct().collect(Collectors.toList());
    }

        /*
        public List<List<String>> compliantCombinations() {
            List<List<String>> componentCombinations = new ArrayList<>();
            for (ComponentStatus status : statuses()) {
                if (status.compliant) {
                    List<String> components = new ArrayList<>();
                    Map<Component, List<List<License>>> map = status.map;
                    for (Map.Entry<Component, List<List<License>>> entry : status.map.entrySet()) {
                        List<List<License>> licensList = entry.getValue();

                        components.add(entry.getKey() + " (" + entry.getKey().license() + ")");
                    }
                    components = components.stream()
                            .distinct()
                            .collect(Collectors.toList());
                    componentCombinations.add(components);
                }
            }
            System.out.println(" returning: " + componentCombinations.size() + " lists");
            return componentCombinations;
        }

         */
}
