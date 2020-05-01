package com.sandklef.compliance.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.sandklef.compliance.domain.*;

// TODO: include notice

import static com.sandklef.compliance.domain.Obligation.*;
/*import static Obligation.MODIFY_NAME;
  import static Obligation.DISTRIBUTE_NAME;
  import static Obligation.PLACE_WARRANTY_NAME;
  import static Obligation.SUB_LICENSE_NAME;
  import static Obligation.HOLD_LIABLE_NAME;
  import static Obligation.INCLUDE_ORIGINAL_NAME;
  import static Obligation.DISCLOSE_SOURCE_NAME;
  import static Obligation.INCLUDE_COPYRIGHT_NAME;
  import static Obligation.STATE_CHANGES_NAME;
  import static Obligation.INCLUDE_LICENSE_NAME;
*/

public class LicenseStore {


  static {
    getInstance();
  }
  
  //TODO: Private use, patent claims, trademark
  private Map<String, License> licenses;

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

  private License addLicense(String name, Map<String, LicenseObligation> obligations) {
    License l = new License(name, obligations);
    licenses.put(name, l);
    return l;
  }

  public Map<String, License> licenses() {
    return licenses;
  }

  public String licenseString() {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, License> entry : licenses.entrySet())
    {
      sb.append(entry.getKey());
      sb.append("\n");
      for (LicenseObligation obligation : entry.getValue().obligations().values()) {
        sb.append(" * ");
        sb.append(obligation.name());
        sb.append(": ");
        sb.append(obligation.state());
        sb.append("\n");
      }
    }
    return sb.toString();
  }

  public void addLicenses(Map<String, License> licenses) {
    this.licenses.putAll(licenses);
  }
  
  public License license(String name) {
    return licenses.get(name);
  }

 
}
