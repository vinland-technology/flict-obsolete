// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.domain;

import java.util.ArrayList;
import java.util.List;

public class LicensePolicy {

    private String name;
    private List<License> allowedList;
    private List<License> avoidList;
    private List<License> deniedList;

    public LicensePolicy(String name, List<License> allowedList, List<License> grayList, List<License> deniedList) {
        this.name = name;
        this.allowedList = allowedList;
        this.avoidList = grayList;
        this.deniedList = deniedList;
    }

    public LicensePolicy() {
        name = "Default (empty policy)";
        allowedList = new ArrayList<>();
        avoidList = new ArrayList<>();
        deniedList = new ArrayList<>();
    }

    public String name() {
        return name;
    }
    public void addAllowedLicense(License license) {
        allowedList.add(license);
    }
    public void allowedLicense(List<License> licenses) {
        allowedList = licenses;
    }

    public void addAvoidLicense(License license) {
        avoidList.add(license);
    }
    public void grayLicense(List<License> licenses) {
        avoidList = licenses;
    }

    public void addDeniedLicense(License license) {
        deniedList.add(license);
    }
    public void deniedLicense(List<License> licenses) {
        deniedList = licenses;
    }

    public List<License> allowedList() {
        return allowedList;
    }

    public List<License> avoidList() {
        return avoidList;
    }

    public List<License> deniedList() {
        return deniedList;
    }

    public static String listType (ListType listType) {
        switch (listType) {
            case ALLOWED_LIST:
                return "allowedList";
            case AVOID_LIST:
                return "Graylist";
            case DENIED_LIST:
                return "DeniedList";
        }
        return "undefined";
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("name: ");
        sb.append(name);
        sb.append("\n");
        sb.append("allowedList: ");
        sb.append(allowedList);
        sb.append("\n");
        sb.append("avoidlist: ");
        sb.append(avoidList);
        sb.append("\n");
        sb.append("deniedList: ");
        sb.append(deniedList);
        sb.append("\n");
        return sb.toString();
    }
}