package com.sandklef.compliance;

import java.util.HashMap;
import java.util.Map;

// TODO: include notice

import static com.sandklef.compliance.Obligation.*;
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
    setupLicenses();
  }

  private License addLicense(String name, Map<String, LicenseObligation> obligations) {
    License l = new License(name, obligations);
    licenses.put(name, l);
    return l;
  }
  
  private void setupLicenses() {
    GPL_2_0_ONLY = addLicense(GPL_2_0_ONLY_NAME, new ObligationBuilder().
                               add(COMMERCIAL_USE, ObligationState.CAN).
                               add(MODIFY, ObligationState.CAN).
                               add(DISTRIBUTE,ObligationState.CAN).
                               add(PLACE_WARRANTY,ObligationState.CAN).
                               add(SUB_LICENSE,ObligationState.CANNOT).
                               add(HOLD_LIABLE,ObligationState.CANNOT).
                               add(INCLUDE_ORIGINAL,ObligationState.MUST).
                               add(DISCLOSE_SOURCE,ObligationState.MUST).
                               add(INCLUDE_COPYRIGHT,ObligationState.MUST).
                               add(STATE_CHANGES,ObligationState.MUST).
                               add(INCLUDE_LICENSE,ObligationState.MUST).
                               add(LINKING_AND_CHANGE_LICENSE,ObligationState.CANNOT).
                               build());
    APACHE_2_0 = addLicense(APACHE_2_0_NAME, new ObligationBuilder().
                            add(COMMERCIAL_USE, ObligationState.CAN).
                            add(MODIFY, ObligationState.CAN).
                            add(DISTRIBUTE,ObligationState.CAN).
                            add(PLACE_WARRANTY,ObligationState.CAN).
                            add(SUB_LICENSE,ObligationState.CAN).
                            add(HOLD_LIABLE,ObligationState.CANNOT).
                            add(INCLUDE_ORIGINAL,ObligationState.MUST).
                            add(DISCLOSE_SOURCE,ObligationState.MUST).
                            add(INCLUDE_COPYRIGHT,ObligationState.MUST).
                            add(STATE_CHANGES,ObligationState.MUST).
                            add(INCLUDE_LICENSE,ObligationState.MUST).
                            add(LINKING_AND_CHANGE_LICENSE,ObligationState.CAN).
                            build());
    LGPL_2_1_ONLY = addLicense(LGPL_2_1_ONLY_NAME, new ObligationBuilder().
                               add(COMMERCIAL_USE, ObligationState.CAN).
                               add(MODIFY, ObligationState.CAN).
                               add(DISTRIBUTE,ObligationState.CAN).
                               add(SUB_LICENSE,ObligationState.CANNOT).
                               add(HOLD_LIABLE,ObligationState.CANNOT).
                               add(INCLUDE_ORIGINAL,ObligationState.MUST).
                               add(DISCLOSE_SOURCE,ObligationState.MUST).
                               add(INCLUDE_COPYRIGHT,ObligationState.MUST).
                               add(STATE_CHANGES,ObligationState.MUST).
                               add(INCLUDE_LICENSE,ObligationState.MUST).
                               add(LINKING_AND_CHANGE_LICENSE,ObligationState.CAN).
                               build());
    /*
      add(PLACE_WARRANTY,ObligationState.CAN).

    */  }
  
  public License license(String name) {
    return licenses.get(name);
  }

  public static final String GPL_2_0_ONLY_NAME = "GPL-2.0-only";
  public static final String LGPL_2_1_ONLY_NAME = "LGPL-2.1-only";
  public static final String APACHE_2_0_NAME = "Apache-2.0";
  
  public static License GPL_2_0_ONLY;
  public static License LGPL_2_1_ONLY;
  public static License APACHE_2_0;
  
}
