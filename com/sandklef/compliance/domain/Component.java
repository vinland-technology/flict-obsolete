// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.domain;

import java.util.*;

import com.sandklef.compliance.utils.*;

public class Component {

  public static String LOG_TAG = Component.class.getSimpleName();

  private String name;
  private final String licenseString;
  private LicenseExpression licenseExpression;
  private List<List<License>> licenseList;

/*  private License concludedLicense;
  private List<License> licenses;
*/

//  private LicenseMeta licenseMeta = LicenseMeta.UNKNOWN_LICENSED ;
  private List<Component> dependencies;



  // For test clasess
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

  // For test clasess
  public Component(String name, License license, List<Component> dependencies) {
    this.name = name;
    this.name = name;
    this.licenseString = license.spdx();
    this.dependencies = dependencies;
    if (dependencies==null) {
      this.dependencies = new ArrayList<>();
    }

    this.dependencies = dependencies;
    if (dependencies==null) {
      this.dependencies = new ArrayList<>();
    }

  }

  public Component(String name, String license, List<Component> dependencies) throws LicenseExpressionException, IllegalLicenseExpression {
    this.name = name;
    this.licenseString = license;
    this.dependencies = dependencies;
    if (dependencies==null) {
      this.dependencies = new ArrayList<>();
    }
    expand();

    Log.d(LOG_TAG, "new Component: " + name + "    license: " + license );
  }


  public Component(String name, String license) throws LicenseExpressionException, IllegalLicenseExpression {
    this.name = name;
    this.licenseString = license;
    this.dependencies = new ArrayList<>();
    expand();
    Log.d(LOG_TAG, "new Component: " + name + "    license: " + license );
  }

  public LicenseExpression licenseExpression() throws LicenseExpressionException, IllegalLicenseExpression {
    return licenseExpression;
  }

  public List<List<License>> licenseList() throws IllegalLicenseExpression, LicenseExpressionException {
    return licenseList;
  }

  public void expand() throws LicenseExpressionException, IllegalLicenseExpression {
    if (licenseExpression==null) {
      licenseExpression = (new LicenseExpressionParser()).parse(this.licenseString);
    }
    if (licenseList == null ) {
      licenseList = licenseExpression.licenseList();
    }
  }

  public String name() {
    return name;
  }

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
    expand();
    int paths = licenseExpression.paths();
    if (dependencies.size()==0) {
      Log.d(LOG_TAG, "paths: " + name + ": " + licenseExpression.paths());
      return paths;
    }
    int sum = 1;
    for (Component d : dependencies) {
      sum *= d.paths();
    }

    Log.d(LOG_TAG,"paths: " + name + ": " + sum);
    return sum*paths;
  }

  public int nrDependencies() {
    if (dependencies.size()==0) {
      return 0;
    }
    int sum = dependencies.size();
    for (Component d : dependencies) {
      sum += d.nrDependencies();
    }
    return sum;
  }

  public List<Component> allDependenciesImpl() {
    List<Component> components = new ArrayList<>();
    for (Component d : dependencies) {
      components.add(d);
      components.addAll(d.allDependenciesImpl());
    }
    return components;
  }

  private String allLicensesImpl() {
    if (dependencies.size()==0) {
      return license();
    }
    StringBuilder sb = new StringBuilder();

    sb.append(license());

    for (Component d : dependencies) {
      sb.append(" ");
      sb.append(d.allLicensesImpl());
    }
    return sb.toString();
  }

  public Set<String> allLicenses() {
    Log.d(LOG_TAG, "allLicenses: " + allLicensesImpl());
    String spacedLicenses = allLicensesImpl().
            replace('&', ' ').
            replace('|', ' ').
            replace('(', ' ').
            replace(')',' ');
    Log.d(LOG_TAG, "allLicenses: " + spacedLicenses);
    Set<String> licenses = new HashSet<>();
    for (String s : spacedLicenses.split(" ")) {
      if (s.replaceAll(" ", "").length()>0) {
        Log.d(LOG_TAG, "allLicenses:  * " + s);
        licenses.add(s);
      }
    }
    return licenses;
  }


  public String toStringLong() {
    StringBuffer sb = new StringBuffer();
    sb.append("{ ");
    sb.append(name);
    sb.append("(");
    sb.append(licenseString);
    sb.append(")");

    sb.append(" [");
    for (Component c : dependencies) {
      sb.append( "  " + c.toStringLong()  );
    }
    sb.append(" ] }" );
    return sb.toString();
  }

  // TODO: Move the method elsewhere - really not part of Component's concern
  public String toStringWithLicenses() {
    StringBuffer sb = new StringBuffer();
    sb.append(name);
    sb.append("\n------------------------------");
    sb.append("\n plain:               ");
    sb.append(licenseString);
    sb.append("\n paths:               ");
    sb.append(licensePaths());
    try {
      sb.append(" / ");
      sb.append(paths());
      LicenseExpressionParser lep = new LicenseExpressionParser() ;
      String fixed = lep.fixLicenseExpression(licenseString);
      sb.append("\n fixed:               ");
      sb.append(fixed);
      sb.append("\n license expresssion: ");
      LicenseExpression le = lep.parse(fixed);
      sb.append(licenseExpression);
      sb.append("\n license list       : ");
      sb.append(licenseList);
    } catch (LicenseExpressionException e) {
      e.printStackTrace();
    } catch (IllegalLicenseExpression illegalLicenseExpression) {
      illegalLicenseExpression.printStackTrace();
    }
    sb.append("\n");
    sb.append("\n");

    for (Component c : dependencies) {
      sb.append(c.toStringWithLicenses()  );
    }
    return sb.toString();
  }

  @Override
  public String toString() {
    return name;
  }
  
}
