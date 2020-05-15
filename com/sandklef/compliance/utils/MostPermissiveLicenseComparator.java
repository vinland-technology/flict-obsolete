// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.utils;

import java.util.Comparator;

import com.sandklef.compliance.domain.*;

public class MostPermissiveLicenseComparator implements Comparator<License> {

  private static final String LOG_TAG = MostPermissiveLicenseComparator.class.getSimpleName();

  public int compare(License first, License second) {
//    System.err.println("WARNING: Dummy implementation of most permissive compare(" +
  //          first + ", " + second);
  /* System.err.println("WARNING: Dummy implementation of most permissive compare(" +
            first.spdxTag() +", " + second.spdxTag() +")  [" +
            first.isCopyleft() +", " + second.isCopyleft() +"] [" +
            first.spdxTag().compareTo(second.spdxTag()) + " == 1]");
*/

/*
    if (first.isCopyleft() && !second.isCopyleft()) {
   //   System.err.println("WARNING: Dummy implementation cl vs permissive");
      return 1;
    }
    if (!first.isCopyleft() && second.isCopyleft()) {
     // System.err.println("WARNING: Dummy implementation permissive vs cl");
      return -1;

    }
    if ( first.isCopyleft() == second.isCopyleft()) {
      //System.err.println("WARNING: Dummy implementation type vs type");
      return first.spdxTag().compareTo(second.spdxTag());
    }
*/
   //
    // System.err.println("WARNING: Dummy implementation permissive vs permissive  DEFAULT");

//    Log.level(Log.DEBUG);
    Log.d(LOG_TAG, "    compare licenses: " + first + "  " + second + " ===> " + first.spdx().compareTo(second.spdx()));
    if (first.spdx().contains("GPL")) {
      Log.d(LOG_TAG, "    compare licenses: " + first + "  " + second + " ===> fake 5");
      return 5;
    }

    //TODO: replace this rather sketchy solution
    return first.spdx().compareTo(second.spdx());
  }

  public static final MostPermissiveLicenseComparator comparator =
    new MostPermissiveLicenseComparator();
  
}
