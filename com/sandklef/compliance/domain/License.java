// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.domain;

import java.util.Map;

import com.sandklef.compliance.utils.*;

public class License {

  // TODO: add support for "or later"

  public static String LOG_TAG = License.class.getSimpleName();

  private String name;
  private String spdx;

  public String name() {
    return name;
  }

  @Override
  public String toString() {
    return spdx;
  }

  public License(String name, String spdx) {
    this.name = name;
    this.spdx = spdx;
  }

  public License(String spdx) {
    this.spdx = spdx;
  }


  //  private Map<String, LicenseObligation> obligations;
//  private LicenseType type;

  /*  public License (String spdx, LicenseType type) {
    this.spdx = spdx;
    this.type = type;
    }*/
/*
  public License (String spdx, Map<String, LicenseObligation> obligations) {
    this.spdx = spdx;
    this.obligations = obligations;
  }
*/

  /*  public int compareTo(LicenseType type) {
    return this.type.value() - type.value();
    }*/

  /*
  public static LicenseType concludeType(LicenseType type1, LicenseType type2) {
    Log.dn(LOG_TAG," concluding -- : " + type1.name() + " oder " + type2.toString() + " ==> ");
    if (type1.value() < type2.value() ) {
      Log.d(LOG_TAG, type1.toString());
      return type1;
    }
    Log.d(LOG_TAG, type2.toString());
    return type2;
  }
  */
  public String spdx() {
    return spdx;
  }
  
//  public LicenseType type() {
//    return type;
//  }
//
//  public Map<String, LicenseObligation> obligations() {
//    return obligations;
//  }

  /*  public boolean canUse(License l) {
    return LicenseArbiter.canAUseB(this, l);
  }
  
  public boolean uses(License l) throws LicenseViolationException {
    return LicenseArbiter.aUsesB(this, l);
    }*/

  //  public String toStringLong() {
//    StringBuilder sb = new StringBuilder();
//    sb.append(spdx);
//    sb.append("\nObligations:\n");
//
//    Log.d(LOG_TAG, "spdx: " + spdx);
//
//    for (LicenseObligation obligation : obligations.values()) {
//      sb.append(" * ");
//      sb.append(obligation.name());
//      sb.append(": ");
//      sb.append(obligation.state());
//      sb.append("\n");
//    }
//    return sb.toString();
//  }
//
//  public LicenseObligation obligation(String obligationName) {
//    return obligations.get(obligationName);
//  }
//
//  public ObligationState obligationState(String obligationName) {
//    return obligations.get(obligationName).state();
//  }

//  public boolean isCopyleft() {
//    return obligation(Obligation.LINKING_COPYLEFTED_NAME).state() ==
//            ObligationState.TRUE;
//  }

  public final static String GPL_2_0_SPDX = "GPL-2.0-only";
  public final static String GPL_3_0_SPDX = "GPL-3.0-only";
  public final static String GPL_3_0_LATER_SPDX = "GPL-3.0-or-later";
  public final static String GPL_3_1_SPDX = "GPL-3.1-only";
  public final static String LGPL_3_0_SPDX = "LGPL-3.0-only";
  public final static String LGPL_2_0_SPDX = "LGPL-2.0-only";
  public final static String GPL_2_0_LATER_SPDX = "GPL-2.0-or-later";
  public final static String LGPL_2_1_SPDX = "LGPL-2.1-only";
  public final static String LGPL_2_1_LATER_SPDX = "LGPL-2.1-or-later";
  public final static String LGPL_2_1_ONLY_SPDX = "LGPL-2.1-only";
  public final static String LGPL_3_1_SPDX = "LGPL-3.1-or-later";
  public final static String APACHE_2_0_SPDX = "Apache-2.0";
  public final static String BSD_3_SPDX = "BSD-3-Clause";

}
