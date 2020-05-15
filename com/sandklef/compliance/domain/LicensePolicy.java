// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.domain;

import java.util.ArrayList;
import java.util.List;

public class LicensePolicy {

    private String name;
    private List<License> whiteList;
    private List<License> grayList;
    private List<License> blackList;

    public LicensePolicy(String name, List<License> whiteList, List<License> grayList, List<License> blackList) {
        this.name = name;
        this.whiteList = whiteList;
        this.grayList = grayList;
        this.blackList = blackList;
    }

    public LicensePolicy() {
        whiteList = new ArrayList<>();
        grayList = new ArrayList<>();
        blackList = new ArrayList<>();
    }

    public void addWhiteLicense(License license) {
        whiteList.add(license);
    }
    public void whiteLicense(List<License> licenses) {
        whiteList = licenses;
    }

    public void addGrayLicense(License license) {
        grayList.add(license);
    }
    public void grayLicense(List<License> licenses) {
        grayList = licenses;
    }

    public void addBlackLicense(License license) {
        blackList.add(license);
    }
    public void blackLicense(List<License> licenses) {
        blackList = licenses;
    }

    public List<License> whiteList() {
        return whiteList;
    }

    public List<License> grayList() {
        return grayList;
    }

    public List<License> blackList() {
        return blackList;
    }

    public static String listType (ListType listType) {
        switch (listType) {
            case WHITE_LIST:
                return "Whitelist";
            case GRAY_LIST:
                return "Graylist";
            case BLACK_LIST:
                return "Blacklist";
        }
        return "undefined";
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("name: ");
        sb.append(name);
        sb.append("\n");
        sb.append("whitelist: ");
        sb.append(whiteList);
        sb.append("\n");
        sb.append("graylist: ");
        sb.append(grayList);
        sb.append("\n");
        sb.append("blacklist: ");
        sb.append(blackList);
        sb.append("\n");
        return sb.toString();
    }
}