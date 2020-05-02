// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.utils;

import java.util.Comparator;

import com.sandklef.compliance.domain.*;

public class MostPermissiveLicenseComparator implements Comparator<License> {

  public int compare(License first, License second) {
   /* System.err.println("WARNING: Dummy implementation of most permissive compare(" +
            first.spdxTag() +", " + second.spdxTag() +")  [" +
            first.isCopyleft() +", " + second.isCopyleft() +"] [" +
            first.spdxTag().compareTo(second.spdxTag()) + " == 1]");
*/
    // TODO: constantly improve

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

   //
    // System.err.println("WARNING: Dummy implementation permissive vs permissive  DEFAULT");

    return first.spdxTag().compareTo(second.spdxTag());
  }

  public static final MostPermissiveLicenseComparator comparator =
    new MostPermissiveLicenseComparator();
  
}
