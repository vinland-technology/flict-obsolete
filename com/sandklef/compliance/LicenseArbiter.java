package com.sandklef.compliance;

public class LicenseArbiter {

  public static ObligationState state(License license, String obligationName) {
    return license.obligations().get(obligationName).state();
  }
  
  public static boolean aUsesB(License user, License usee) throws LicenseViolationException {
    
    if (user.spdxTag().equals(usee.spdxTag())) {
      //      System.out.println("Same license: true");
      return true;
    }

    // Below it will be assumed that the licenses differ
    
    // If usee cannot be sublicensed with changed license
    //      => violation
    if (state(usee, Obligation.LINKING_AND_CHANGE_LICENSE_NAME) == ObligationState.CANNOT) {
      //System.out.println(user.spdxTag() + " using " + usee.spdxTag() + " : violation");
      throw new LicenseViolationException(user.spdxTag() + " can not sublicense " + usee.spdxTag(), user, usee);
    }
    return true;
  }
  
  public static boolean canAUseB(License user, License usee) {
    try {
      return aUsesB(user, usee);
    } catch (LicenseViolationException e) {
      System.err.println("INFO: License violation found, returning false.  Cause: \"" + e.getMessage() + "\"");
      return false;
    }
  }
  
}
