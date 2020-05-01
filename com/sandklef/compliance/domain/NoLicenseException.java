package com.sandklef.compliance.domain;

public class NoLicenseException extends Exception {

  public final License user;
  public final License usee;

  public NoLicenseException(String message) {
    super(message);
    user=null;
    usee=null;
  }

  public NoLicenseException(String message,
                            License user,
                            License usee) {
    super(message);
    this.user = user;
    this.usee = usee;
  }
  
}
