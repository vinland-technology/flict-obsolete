package com.sandklef.compliance;

import java.util.HashMap;
import java.util.Map;
  
public class License {

  // TODO: add support for "or later"
  
  private String spdxTag;
  private Map<String, LicenseObligation> obligations;
  private LicenseType type;
  
  /*  public License (String spdxTag, LicenseType type) {
    this.spdxTag = spdxTag;
    this.type = type;
    }*/

  public License (String spdxTag, Map<String, LicenseObligation> obligations) {
    this.spdxTag = spdxTag;
    this.obligations = obligations;
  }

  /*  public int compareTo(LicenseType type) {
    return this.type.value() - type.value();
    }*/

  public static LicenseType concludeType(LicenseType type1, LicenseType type2) {
    System.out.print(" concluding -- : " + type1.name() + " oder " + type2.toString() + " ==> ");
    if (type1.value() < type2.value() ) {
      System.out.println(type1.toString());
      return type1;
    }
    System.out.println(type2.toString());
    return type2;
  }
  
  public String spdxTag() {
    return spdxTag;
  }
  
  public LicenseType type() {
    return type;
  }

  public Map<String, LicenseObligation> obligations() {
    return obligations;
  }

  /*  public boolean canUse(License l) {
    return LicenseArbiter.canAUseB(this, l);
  }
  
  public boolean uses(License l) throws LicenseViolationException {
    return LicenseArbiter.aUsesB(this, l);
    }*/
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(spdxTag);
    sb.append("\nObligations:\n");


    for (LicenseObligation obligation : obligations.values()) {
      sb.append(" * ");
      sb.append(obligation.name());
      sb.append(": ");
      sb.append(obligation.state());
      sb.append("\n");
    }
    return sb.toString();
  }

  
}
