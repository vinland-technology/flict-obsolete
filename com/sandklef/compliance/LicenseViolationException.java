package com.sandklef.compliance;

public class LicenseViolationException extends Exception {

  public final License user;
  public final License usee;
  
  public LicenseViolationException(String message) {
    super(message);
    user=null;
    usee=null;
  }
  
  public LicenseViolationException(String message,
                                   License user,
                                   License usee) {
    super(message);
    this.user = user;
    this.usee = usee;
  }
  
}
