// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.utils;

import java.util.HashMap;
import java.util.Map;

import com.sandklef.compliance.domain.*;

public class LicenseStore {


  static {
    getInstance();
  }
  
  //TODO: add support for Private use, Patent claims, Trademark, Notice
  private Map<String, License> licenses;
  private  Map<String, LicenseConnector> connectors;

  private static LicenseStore store;
  public static LicenseStore getInstance() {
    if (store==null) {
      store = new LicenseStore();
    }
    return store;
  }
  
  private LicenseStore() {
    licenses = new HashMap<>();
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
  
  public License license(String name) throws LicenseExpressionException {
    License license = licenses.get(name);
    if ( license == null ) {
      throw new LicenseExpressionException("Can't find a License match for \"" + name + "\"");
    }
    return license;
  }

  public Map<String, LicenseConnector> connectors() {
    return connectors;
  }

  public void connector(Map<String, LicenseConnector> connector) {
        this.connectors = connector;
  }
}
