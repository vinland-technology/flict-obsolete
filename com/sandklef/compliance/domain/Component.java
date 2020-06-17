// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.domain;

import java.util.List;
import java.util.ArrayList;

import com.sandklef.compliance.utils.*;

import javax.swing.*;

public class Component {

  public static String LOG_TAG = Component.class.getSimpleName();

  private String name;
  private String licenseString;
  private LicenseExpression licenseExpression;
  private List<List<License>> licenseList;

/*  private License concludedLicense;
  private List<License> licenses;
*/

//  private LicenseMeta licenseMeta = LicenseMeta.UNKNOWN_LICENSED ;
  private List<Component> dependencies;

/*
  public static enum LicenseMeta {
    UNKNOWN_LICENSED,
    DUAL_LICENSED,
    MANY_LICENSED,
    SINGLE_LICENSED;
  }


  public boolean dualLicensed() {
    if (singleLicensed()) {
      return false;
    }
    return LicenseMeta.DUAL_LICENSED == licenseMeta;
  }

  public boolean manyLicensed() {
    if (singleLicensed()) {
      return false;
    }
    return LicenseMeta.MANY_LICENSED == licenseMeta;
  }

  public boolean singleLicensed() {
    // TODO: what id licenses.size()<2???
    return LicenseMeta.SINGLE_LICENSED == licenseMeta;
  }
 */

  public Component(String name, List<License> licenses, List<Component> dependencies) {
    this.name = name;
    this.name = name;
    StringBuffer sb = new StringBuffer();
    for (License s : licenses) {
      if (sb.toString().length()!=0) {
        sb.append(" | ");
      }
      sb.append(s.spdx());
    }
    this.licenseString = sb.toString();
    this.dependencies = dependencies;
    if (dependencies==null) {
      this.dependencies = new ArrayList<>();
    }

    this.dependencies = dependencies;
    if (dependencies==null) {
      this.dependencies = new ArrayList<>();
    }

  }

  public Component(String name, String license, List<Component> dependencies) {
    this.name = name;
    this.licenseString = license;
    this.dependencies = dependencies;
    if (dependencies==null) {
      this.dependencies = new ArrayList<>();
    }
/*    if (licenses.size()==1) {
      concludedLicense = licenses.get(0);
    }
    */
    Log.d(LOG_TAG, "new Component: " + name + "    license: " + license );
  }

  public Component(String name, License license, List<Component> dependencies) {
    this.name = name;
    this.licenseString = license.spdx();
    this.dependencies = dependencies;
    if (dependencies==null) {
      this.dependencies = new ArrayList<>();
    }
/*    if (licenses.size()==1) {
      concludedLicense = licenses.get(0);
    }
    */
    Log.d(LOG_TAG, "new Component: " + name + "    license: " + license );
  }

  public Component(String name, String license) {
    this.name = name;
    this.licenseString = license;
    this.dependencies = new ArrayList<>();
    Log.d(LOG_TAG, "new Component: " + name + "    license: " + license );
  }

  public Component(String name, License license) {
    this.name = name;
    this.licenseString = license.spdx();
    this.dependencies = new ArrayList<>();
    Log.d(LOG_TAG, "new Component: " + name + "    license: " + license );
  }

  /*  public Component(String name, List<License> licenses, List<Component> dependencies, LicenseMeta meta) {
    this(name, licenses, dependencies);
    this.licenseMeta = meta;
  }
*/

  /*
  public Component(String name, License license, List<Component> dependencies) {
    this.name = name;
    this.dependencies = dependencies;
   // this.concludedLicense = null;
    if (dependencies==null) {
      this.dependencies = new ArrayList<>();
    }
//    licenses = new ArrayList<>();
  //  licenses.add(license);
 //   concludedLicense = license;
    Log.d(LOG_TAG, "new Component: " + name + "    license: " + concludedLicense()+ "    licenses: " + licenses());
  }
*/

  public LicenseExpression licenseExpression() throws LicenseExpressionException, IllegalLicenseExpression {
    if (licenseExpression==null) {
      expand();
    }
    return licenseExpression;
  }

  public List<List<License>> licenseList() throws IllegalLicenseExpression, LicenseExpressionException {
    if (licenseList==null) {
      expand();
    }
    return licenseList;
  }

  public void expand() throws LicenseExpressionException, IllegalLicenseExpression {
    licenseExpression = (new LicenseExpressionParser()).parse(this.licenseString);
    licenseList = licenseExpression.licenseList();
  }

  public String name() {
    return name;
  }
  /*
  public License concludedLicense() {
    return concludedLicense;
  }
  
  public List<License> licenses() {
    return licenses;
  }

  public void concludedLicense(License license) {
    Log.d(LOG_TAG, "\n ======== UPDATING LICENSE on \"" + name +  "\" to \"" +
            (license!=null?""+license.spdx():license) + "\" ==========\n");
    concludedLicense = license;
  }

  public void invalidateConcludedLicense() {
    concludedLicense = null;
  }
  */

  public String license() {
    return licenseString;
  }

  public void addDependency(Component c) {
    dependencies.add(c);
  }

  public List<Component> dependencies() {
    return dependencies;
  }

  public int licensePaths() {
    return licenseExpression.paths();
  }

  public int paths() throws LicenseExpressionException, IllegalLicenseExpression {
    if (licenseExpression==null) {
      expand();
    }
    System.out.println(" paths(): " + licenseString);

    if (dependencies.size()==0) {
      return licenseExpression.paths();
    }
    int sum=0;
    for (Component d : dependencies) {
      sum += d.paths();
    }

    return sum;
  }

  public String toStringLong() {
    StringBuffer sb = new StringBuffer();
    sb.append("{ ");
    sb.append(name);
    sb.append("(");
/*    if (concludedLicense()!=null) {
      sb.append("{ " + name + " (" + concludedLicense().spdx() + ") [");
    }
    switch(licenseMeta) {
    case UNKNOWN_LICENSED:
      sb.append("Unknown license");
      break;
    case DUAL_LICENSED:
      sb.append("Dual licensed");
      break;
    case MANY_LICENSED:
      sb.append("Many licensed");
      break;
    case SINGLE_LICENSED:
      sb.append("Single licensed");
      break;
    }
    sb.append(") [");
    for (License l : licenses) {
      sb.append( "  " + l.spdx()  );
    }
    sb.append(" ],  ");
 */

    sb.append(" [");
    for (Component c : dependencies) {
      sb.append( "  " + c.toStringLong()  );
    }
    sb.append(" ] }" );
    return sb.toString();
  }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(" #" + name + "# ");
/*    sb.append(" (");
    if (concludedLicense()!=null) {
      sb.append(concludedLicense().spdx());
    }
    Log.d(LOG_TAG, "   toString c:" + name + "   licenses: " + licenses().size());
    Log.d(LOG_TAG, "   toString c:" + name + "   licenses: " + licenses());
   if (licenses().size()>1 && concludedLicense()==null) {
      sb.append(" (");
      for (License l : licenses()) {
        //        Log.level(Log.DEBUG);
        Log.d(LOG_TAG, "   toString c: " + name + "   license: " + l);
        sb.append(l.spdx());
        sb.append(",");
      }
      sb.append(") ");
    }

    sb.append("   [" );
    for (Component c : dependencies) {
      sb.append( " " + c.name() );
    }
    sb.append("]" );
    sb.append(" )");

 */
    return sb.toString();
  }
  
}
