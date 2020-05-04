// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.domain;

import java.util.List;
import java.util.ArrayList;

import com.sandklef.compliance.utils.*;

public class Component {

  public static String LOG_TAG = Component.class.getSimpleName();

  // name, enough for now
  private String name;
  private License concludedLicense;
  private List<License> licenses;
  private List<Component> dependencies;
  
  public Component(String name, List<License> licenses, List<Component> dependencies) {
    this.name = name;
    this.licenses = licenses;
    this.dependencies = dependencies;
    if (dependencies==null) {
      this.dependencies = new ArrayList<>();
    }
/*    if (licenses.size()==1) {
      concludedLicense = licenses.get(0);
    }
    */
    Log.d(LOG_TAG, "new Component: " + name + "    license: " + concludedLicense()+ "    licenses: " + licenses());
  }

  public Component(String name, License license, List<Component> dependencies) {
    this.name = name;
    this.dependencies = dependencies;
    this.concludedLicense = null;
    if (dependencies==null) {
      this.dependencies = new ArrayList<>();
    }
    licenses = new ArrayList<>();
    licenses.add(license);
 //   concludedLicense = license;
    Log.d(LOG_TAG, "new Component: " + name + "    license: " + concludedLicense()+ "    licenses: " + licenses());
  }

  public String name() {
    return name;
  }
  
  public License concludedLicense() {
    return concludedLicense;
  }
  
  public List<License> licenses() {
    return licenses;
  }
  
  public void concludedLicense(License license) {
    Log.d(LOG_TAG, "\n ======== UPDATING LICENSE on \"" + name +  "\" to \"" +
                       (license!=null?""+license.spdxTag():license) + "\" ==========\n");
    concludedLicense = license;
  }
  
  public List<Component> dependencies() {
    return dependencies;
  }


  public String toStringLong() {
    StringBuffer sb = new StringBuffer();
    sb.append("{ ");
    sb.append(name);
    sb.append("(");
    if (concludedLicense()!=null) {
      sb.append("{ " + name + " (" + concludedLicense().spdxTag() + ") [");
    }
    sb.append(") [");
    for (Component c : dependencies) {
      sb.append( "  " + c.toStringLong()  );
    }
    sb.append(" ] }" );
    return sb.toString();
  }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(name);
    sb.append(" (");
    if (concludedLicense()!=null) {
      sb.append(name + " (" + concludedLicense().spdxTag() +")");
    } else {
      Log.d(LOG_TAG, "   toString c:" + name + "   licenses: " + licenses().size());
      Log.d(LOG_TAG, "   toString c:" + name + "   licenses: " + licenses());
      for (License l : licenses()) {
        Log.d(LOG_TAG, "   toString c:" + name + "   license: " + l);
        sb.append(l.spdxTag());
        sb.append(",");
      }
    }

    sb.append("[" );
    for (Component c : dependencies) {
      sb.append( " " + c.name() );
    }
    sb.append("]" );
    sb.append(" )");
    return sb.toString();
  }
  
  
}