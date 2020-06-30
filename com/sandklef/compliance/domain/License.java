// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.domain;

import java.util.Map;

import com.sandklef.compliance.utils.*;

public class License {

  public static String LOG_TAG = License.class.getSimpleName();

  private String name;
  private final String spdx;
  private String license_group;

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

  public License clone() {
    return new License(this.name, this.spdx);
  }

  public License(String spdx) {
    this.spdx = spdx;
  }

  public String info() {
    return name + " / " + spdx + " / " + license_group;
  }

  public String spdx() {
    return spdx;
  }

  public String licenseGroup() {
    return license_group;
  }


  // TODO: move this to test
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
