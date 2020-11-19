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

  @Override
  public boolean equals(Object lo) {
    License l = (License) lo;
//    System.out.println("License.equals: " + this.spdx + ".equals(" + l.spdx + ") = " + l!=null && l.spdx.equals(spdx) );
    return ( l!=null && l.spdx.equals(spdx) );
  }

}
