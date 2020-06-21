// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.utils;

import java.util.HashMap;
import java.util.Map;

import com.sandklef.compliance.domain.*;

public class LicenseStore {


  private static final String LOG_TAG = LicenseStore.class.getSimpleName() ;

  static {
    getInstance();
  }
  
  //TODO: add support for Private use, Patent claims, Trademark, Notice
  private Map<String, License> licenses;
  private Map<String, LicenseGroup> licenseGroups;
  private Map<String, LicenseConnector> connectors;

  private static LicenseStore store;
  public static LicenseStore getInstance() {
    if (store==null) {
      store = new LicenseStore();
    }
    return store;
  }
  
  private LicenseStore() {
    licenses = new HashMap<>();
    licenseGroups = new HashMap<>();
  }

  /*
  private License addLicense(String name, Map<String, LicenseObligation> obligations) {
    License l = new License(name, obligations);
    licenses.put(name, l);
    return l;
  }
*/
  public Map<String, License> licenses() {
    return licenses;
  }


  public String licenseString() {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, License> entry : licenses.entrySet())
    {
      sb.append(entry.getKey());
      sb.append("\n");
  /*    for (LicenseObligation obligation : entry.getValue().obligations().values()) {
        sb.append(" * ");
        sb.append(obligation.name());
        sb.append(": ");
        sb.append(obligation.state());
        sb.append("\n");
      }*/
    }
    return sb.toString();
  }


  public void addLicenses(Map<String, License> licenses) {
    this.licenses.putAll(licenses);
  }

  public void addLicenseGroups(Map<String, LicenseGroup> licenseGroups) {
    this.licenseGroups.putAll(licenseGroups);
  }

  public License license(String name) throws LicenseExpressionException {
    License license = licenses.get(name);
    if ( license == null ) {
      throw new LicenseExpressionException("Can't find a License match for \"" + name + "\"");
    }
    return license;
  }

  public LicenseGroup licenseGroup(String name) throws LicenseExpressionException {
    LicenseGroup licenseGroup = licenseGroups.get(name);
    if ( licenseGroup == null ) {
      throw new LicenseExpressionException("Can't find a LicenseGroup match for \"" + name + "\"");
    }
    return licenseGroup;
  }

  public LicenseConnector connector(License license) throws LicenseConnector.LicenseConnectorException {
    // Try get directly via
    LicenseConnector connector = connectors.get(license.spdx());
    if (connector!=null) {
      return connector;
    }

    // Ok, so hopefully the license can be found in a group
    String groupName = license.licenseGroup();
    // find group via group name
    connector = connectors.get(groupName);
    Log.d(LOG_TAG, "groupName:  " + groupName);
    Log.d(LOG_TAG, "connector:  " + connector);
    Log.d(LOG_TAG, "connectors: " + connectors);
    if (connector!=null) {
      return connector;
    }

    throw new LicenseConnector.LicenseConnectorException("Could not find connector for: " + license.info());
  }


  public Map<String, LicenseConnector> connectors() {
    return connectors;
  }

  public void connector(Map<String, LicenseConnector> connector) {
        this.connectors = connector;
  }
}
