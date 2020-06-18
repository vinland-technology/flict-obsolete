// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.domain;

import java.util.ArrayList;
import java.util.List;

public class LicensePolicy {

    private String name;
    private List<License> allowedList;
    private List<License> grayList;
    private List<License> deniedList;

    public LicensePolicy(String name, List<License> allowedList, List<License> grayList, List<License> deniedList) {
        this.name = name;
        this.allowedList = allowedList;
        this.grayList = grayList;
        this.deniedList = deniedList;
    }

    public LicensePolicy() {
        allowedList = new ArrayList<>();
        grayList = new ArrayList<>();
        deniedList = new ArrayList<>();
    }

    public void addAllowedLicense(License license) {
        allowedList.add(license);
    }
    public void allowedLicense(List<License> licenses) {
        allowedList = licenses;
    }

    public void addGrayLicense(License license) {
        grayList.add(license);
    }
    public void grayLicense(List<License> licenses) {
        grayList = licenses;
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

    public List<License> grayList() {
        return grayList;
    }

    public List<License> deniedList() {
        return deniedList;
    }

    public static String listType (ListType listType) {
        switch (listType) {
            case ALLOWED_LIST:
                return "allowedList";
            case GRAY_LIST:
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
        sb.append("graylist: ");
        sb.append(grayList);
        sb.append("\n");
        sb.append("DeniedList: ");
        sb.append(deniedList);
        sb.append("\n");
        return sb.toString();
    }
}