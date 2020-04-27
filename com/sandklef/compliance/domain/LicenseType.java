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
