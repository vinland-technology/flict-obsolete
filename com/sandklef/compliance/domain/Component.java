// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.domain;

import java.math.BigInteger;
import java.util.*;

import com.sandklef.compliance.utils.*;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

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

  private Map<Component, List<List<License>>> allLicensesMap()
          throws IllegalLicenseExpression, LicenseExpressionException {

    /*
     Given by the component

                      (Europe - a flict example)
                      ["GPL-2.0-or-later & MIT"]
                        /                   \
                       /                     \
                      /                       \
             (Sweden)                       (Germany)
       ["GPL-2.0-only | Apache-2.0"]      ["GPL-2.0-or-later | MIT & BSD-3-Clause | Apache-2.0"]
                  /          \                            /      \
                 /            \                          /        \
       ("Gothenburg")      ("Stockholm")     ("Dusseldorf")      ("Berlin")
       ["BSD-3-Clause"]     ["MIT"]       ["GPL-2.0-or-later"]   ["MIT | MPL-1.1"]

    We should return this:

    Gothenburg: [[BSD-3-Clause]]
    Europe - a flict example: [[GPL-2.0-or-later, MIT], [GPL-3.0-only, MIT]]
    Sweden: [[GPL-2.0-only], [Apache-2.0]]
    Stockholm: [[MIT]]
    Germany: [[GPL-2.0-or-later], [GPL-3.0-only], [MIT, BSD-3-Clause], [Apache-2.0]]
    Dusseldorf: [[GPL-2.0-or-later], [GPL-3.0-only]]
    Berlin: [[MIT], [MPL-1.1]]
     */
    Map<Component,List<List<License>>> licenseMap = new HashMap<>();
    licenseMap.put(this,licenseList);
    System.out.println("HESA-allLicensesMap, put: " + name() +  " ==> size: " + licenseMap.size());
    System.out.println("HESA-allLicensesMap, put: " + name() +  " MAP: " + licenseMap);
    for (Component dep : dependencies()) {
      licenseMap.putAll(dep.allLicensesMap());
      System.out.println("HESA-allLicensesMap, put: " + dep.name() +  " ==> size: " + licenseMap.size());
      System.out.println("HESA-allLicensesMap, put: " + dep.name() +  " MAP: " + licenseMap);
      System.out.println("HESA-allLicensesMap, put: " + dep.name() +  " MAP: " + licenseMap.size());
      System.out.println("HESA-allLicensesMap, put: " + dep.name() +  " MAP: " + licenseMap.get("libXdmcp.so.6.0.0"));
    }
    return licenseMap;
  }

  private long mapsToCreate(Map<Component, List<List<License>>> allLicensesMap) {
    System.out.println("HESA-mapsToCreate");
    long count = 1;
    for (Map.Entry<Component, List<List<License>>> entry : allLicensesMap.entrySet()) {
      long size = entry.getValue().size();
      // TODO: REMOVE below
      count *= size;
      System.out.print("size: " + size + " ===> " + count);
//        System.out.println("mapsToCreate: " + entry.getKey() + " size: " + size + "  count: " + count);
    }

    System.out.println("APA mapsToCreate: ==> " + count);
    System.out.println("mapsToCreate: ==> " + allLicensesMap);
    return count;
  }

  private static Map<Component, List<List<License>>> cloneLicenseListMap (Map<Component, List<List<License>>> map) {
    // Create new Map
    Map<Component, List<List<License>>> clonedMap = new HashMap<>();
    // Iterate over the Map entries (List<List<License>>)
    for (Map.Entry<Component, List<List<License>>> entry : map.entrySet()) {
      Component c = entry.getKey();
      List<List<License>> list = entry.getValue();
      List<List<License>> newList = new ArrayList<>();
      for (List<License> item : list) {
        newList.add(item);
      }
      clonedMap.put(c, newList);
    }
    return clonedMap;
  }

  public List<Map<Component, List<List<License>>>> allLicensesCombinationsList()
          throws IllegalLicenseExpression, LicenseExpressionException {
   /* This is what we've got:
    Gothenburg: [[BSD-3-Clause]]
    Berlin: [[MIT], [MPL-1.1]]

    Next, create copies of this map (double the size per |) and put in a list

    [
    Gothenburg: [[BSD-3-Clause]]
    Berlin: [[MIT], [MPL-1.1]]
    ,
    Gothenburg: [[BSD-3-Clause]]
    Berlin: [[MIT], [MPL-1.1]]
    ]

    Only one expression in every Map of those list
    [
    Gothenburg: [[BSD-3-Clause]]
    Berlin: [[MIT]]

    Gothenburg: [[BSD-3-Clause]]
    Berlin: [[MPL-1.1]]
    ]

    Put Map #01 and Map #02 in a list and return
    */
    Map<Component, List<List<License>>> allLicensesMap = allLicensesMap();
    System.out.println("HESA-allLicensesMap MAP name and size: " + allLicensesMap.size());
    long count = 1 ;
    for (Map.Entry<Component,List<List<License>>> entry : allLicensesMap.entrySet()) {
      count *= entry.getValue().size();
      System.out.println("HESA-allLicensesMap MAP name license: " + entry.getKey() + ": " + entry.getValue().size() + ": " + count);
    }

    List<Map<Component, List<List<License>>>> list = new ArrayList<>();

    // Clone the map as many times needed
    long createMapCount = mapsToCreate(allLicensesMap);
    System.out.println(" * top list size: " + list.size() + " create count: " + createMapCount);
    for (int i=0; i<createMapCount ; i++) {
      list.add(cloneLicenseListMap(allLicensesMap));
    }
    System.out.println(" * top list size: " + list.size());

    // the list with maps are now identical, use the first one to
    // find the maps. The components and their order are
    List<Component> components = new ArrayList<>(allLicensesMap.keySet());
//    System.out.println("Components: " + components);

    long perColumn = createMapCount;
    // Loop through the component list
    for (Component c : components) {
      // System.out.println(" * " + c + ": " + list.get(0).get(c).size() + "  (" + perColumn +")  ");
      // Remember the last list sixe, to update the perColumn variable
      // 0 causes an exception,
      // - which should happen if we don't enter the loop below
      int lastListSize = 0;

      System.out.println(" * component: " + c.name());
      System.out.println(" * map: " + list.size());

      // Create index
      int index = 0;
      // For every List of License Map
      for (Map<Component, List<List<License>>> componentMap : list) {
        // find the one and only List of this component
        // - no need to loop, e simply use .get(c)
        List<List<License>> componentLicenseList = componentMap.get(c);

        // Calculate the index for the license
        System.out.println("APA perColumn:                  " +perColumn);
        System.out.println("APA componentLicenseList.size(): " + componentLicenseList.size());
        int a1 = (int) (perColumn/componentLicenseList.size());
        System.out.println(" a1: " + a1);
        int a2 = (int) (index / a1);
        System.out.println(" a2: " + a2);

        System.out.println("       calculate index " + index + "/ (" + perColumn + "/" + componentLicenseList.size() +")  => " + (perColumn/componentLicenseList.size()));
        int currentLicenseIndex = (int) (index / (perColumn/componentLicenseList.size())) % componentLicenseList.size();

        // Get the License List for the index, store in a List
        List<List<License>> currentLicense = new ArrayList<>();
        currentLicense.add(componentLicenseList.get(currentLicenseIndex));
//        System.out.println("       * index: " + currentLicenseIndex + "   ==> " + componentLicenseList.get(currentLicenseIndex) + "=" + currentLicense);

        // Replace the current License List with currentLicense
        componentMap.put(c, currentLicense);
/*        System.out.println("       * index: " + currentLicenseIndex + "  ===> " + componentMap.get(c));
        System.out.println(" ---" );
        System.out.println(" ---" );
        System.out.println(" ---" );
        System.out.println(" ---" );
        System.out.println("       * index: " + currentLicenseIndex + "  ===> " + componentLicenseList.size());
*/
        index++;
        lastListSize = componentLicenseList.size();
        System.out.println(" -------- size: " + componentLicenseList.size());
      }
      System.out.println(" --- c: " + c);
      System.out.println(" --- " + components.size() );
      perColumn = perColumn / lastListSize;
    }
    return list;
  }

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

  @Override
  public boolean equals(Object o) {
    if (o==null) return false;
    return name.equals( ((Component)o).name());
  }

  @Override
  public int hashCode() {
    return 7 * 31 * name.hashCode();
  }

  public long pileCombinations() throws IllegalLicenseExpression, LicenseExpressionException {
    Map<Component, List<List<License>>> map = allLicensesMap();
    long createMapCount = mapsToCreate(map);
    return createMapCount;
  }
}
