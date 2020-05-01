package com.sandklef.compliance.domain;

import java.util.ArrayList;
import java.util.List;

public class Concern {

    public static class LicenseConcern {
        private License license;
        private ListType listType;
        private Component component;

        public Component component() {
            return component;
        }

        public License license() {
            return license;
        }

        public ListType listType() {
            return listType;
        }

        public LicenseConcern(Component component, License license, ListType listType) {
            this.component = component;
            this.license = license;
            this.listType = listType;
        }

        @Override
        public String toString() {
            return "LicenseConcern{" +
                    "license=" + license +
                    ", listType=" + listType +
                    ", component=" + component +
                    '}';
        }
    }

    private static final String LOG_TAG = Concern.class.getSimpleName() ;

    private Component component;
    private List<LicenseConcern> licenseConcerns;

    public Concern(Component component) {
        this.component = component;
        licenseConcerns = new ArrayList<>();
    }

    public void addLicenseConcern(LicenseConcern concern) {
        licenseConcerns.add(concern);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(component.name());
        sb.append(":\n");
        for (LicenseConcern concern: licenseConcerns) {
            sb.append(" * ");
            sb.append(concern.component().name());
            sb.append(" (");
            sb.append(concern.license().spdxTag());
            sb.append(") is ");
            sb.append(LicensePolicy.listType(concern.listType()));
            sb.append("\n");
        }
        return sb.toString();
    }
}
