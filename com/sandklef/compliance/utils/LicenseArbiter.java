package com.sandklef.compliance.utils;

import com.sandklef.compliance.domain.*;

public class LicenseArbiter {

  public static String LOG_TAG = LicenseArbiter.class.getSimpleName();
  
  public static ObligationState state(License license, String obligationName) {
    return license.obligations().get(obligationName).state();
  }
  
  public static boolean aUsesB(License user, License usee) throws LicenseViolationException {
    
    if (user.spdxTag().equals(usee.spdxTag())) {
      //      Log.d(LOG_TAG, "Same license: true");
      return true;
    }

    // Below it will be assumed that the licenses differ
    
    // If usee cannot be sublicensed with changed license
    //      => violation
    if (state(usee, Obligation.LINKING_AND_CHANGE_LICENSE_NAME) == ObligationState.CANNOT) {
      //Log.d(LOG_TAG, user.spdxTag() + " using " + usee.spdxTag() + " : violation");
      throw new LicenseViolationException(user.spdxTag() + " can not sublicense " + usee.spdxTag(), user, usee);
    }
    return true;
  }
  
  public static boolean canAUseB(License user, License usee) {
    try {
      return aUsesB(user, usee);
    } catch (LicenseViolationException e) {
      Log.e(LOG_TAG, "INFO: License violation found, returning false.  Cause: \"" + e.getMessage() + "\"");
      return false;
    }
  }
  

  public static void checkViolation(Component c) throws LicenseViolationException {
    //    System.out.print(" checkViolation: " + this.name);
    if (c.dependencies().size()==0) {
      Log.d(LOG_TAG, " checkViolation: " + c.name() + ": OK  (no deps)");
      return;
    } else {
      Log.d(LOG_TAG, "");
      for (Component d : c.dependencies()) {
        checkViolation(d);
      }      
    }
    Log.d(LOG_TAG, " checkViolation: " + c.name() + ": OK so far (" + c.dependencies().size() + " deps)");

    Log.d(LOG_TAG, " checkViolation " + c.name() );
    Log.d(LOG_TAG, " -----------------------------");
    for (Component d : c.dependencies()) {
      Log.d(LOG_TAG, " * can " + c.license().spdxTag() + " use " + d.license().spdxTag() + " : " );
      LicenseArbiter.aUsesB(c.license(), d.license());
      Log.d(LOG_TAG, "OK");      
    }      
    
  }
  
  public static boolean checkViolationSafely(Component c)  {
    //    System.out.print(" checkViolation: " + this.name);
    if (c.dependencies().size()==0) {
      Log.d(LOG_TAG, " checkViolation: " + c.name() + ": OK  (no deps)");
      return true;
    } else {
      Log.d(LOG_TAG, "");
      for (Component d : c.dependencies()) {
        checkViolationSafely(d);
      }      
    }
    Log.d(LOG_TAG, " checkViolation: " + c.name() + ": OK so far (" + c.dependencies().size() + " deps)");

    Log.d(LOG_TAG, " checkViolation " + c.name() );
    Log.d(LOG_TAG, " -----------------------------");
    for (Component d : c.dependencies()) {
      Log.dn(LOG_TAG, " ** can " + c.license().spdxTag() + " use " + d.license().spdxTag() + " : " );
      try {
        LicenseArbiter.aUsesB(c.license(), d.license());
      } catch (LicenseViolationException e) {
        Log.d(LOG_TAG, "Exception");      
        Log.d(LOG_TAG, "    message: " + e.getMessage());
        Log.d(LOG_TAG, "    user:    " + c.name() + " (" + e.user.spdxTag()+ ")");
        Log.d(LOG_TAG, "    usee:    " + d.name() + " (" + e.usee.spdxTag()+ ")");
        return false;
      }
      Log.d(LOG_TAG, "OK");      
    }      
    return true;
  }
  
}
