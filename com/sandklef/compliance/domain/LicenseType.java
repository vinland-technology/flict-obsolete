// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.domain;

public enum LicenseType {
  COPYLEFT_STRONG(0),
  COPYLEFT_WEAK(1),
  PERMISSIVE(2);
  
  private int value;
  private LicenseType(int value) {
    this.value = value;
  }
  public int value() {
    return value;
  }
}