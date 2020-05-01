// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.utils;

import com.sandklef.compliance.domain.License;

import java.util.Comparator;

public class LeastPermissiveLicenseComparator implements Comparator<License> {

  public int compare(License first, License second) {
   /* System.err.println("WARNING: Dummy implementation of least permissive compare(License first, License second) :  " +
            (0-MostPermissiveLicenseComparator.comparator.compare(first, second)));
    */
    return (0-MostPermissiveLicenseComparator.comparator.compare(first, second));
  }

  public static final LeastPermissiveLicenseComparator comparator =
    new LeastPermissiveLicenseComparator();
  
}
