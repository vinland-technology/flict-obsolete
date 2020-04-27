package com.sandklef.compliance.utils;

import java.util.List;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.utils.*;

public class LicenseArbiter {

  public static String LOG_TAG = LicenseArbiter.class.getSimpleName();
  
  public static ObligationState state(License license, String obligationName) {
    return license.obligations().get(obligationName).state();
  }
  
  public static boolean aUsesB(License user, License usee) throws LicenseViolationException {
    
    if (user.spdxTag().equals(usee.spdxTag())) {
      Log.d(LOG_TAG, "Same license: true");
      return true;
    }

    // Below it will be assumed that the licenses differ
    
    // If usee cannot be sublicensed with changed license
    //      => violation
    if (state(usee, Obligation.LINKING_AND_CHANGE_LICENSE_NAME) == ObligationState.CANNOT) {
      Log.d(LOG_TAG, user.spdxTag() + " using " + usee.spdxTag() + " : violation");
      throw new LicenseViolationException(user.spdxTag() + " can not sublicense " + usee.spdxTag(), user, usee);
    }
    return true;
  }
  
  public static boolean canAUseB(License user, License usee) {
    try {
      return aUsesB(user, usee);
    } catch (LicenseViolationException e) {
      Log.d(LOG_TAG, "Exception");      
      Log.d(LOG_TAG, "    message: " + e.getMessage());
      Log.d(LOG_TAG, "    user:    (" + e.user.spdxTag()+ ")");
      Log.d(LOG_TAG, "    usee:    (" + e.usee.spdxTag()+ ")");
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
      Log.d(LOG_TAG, " * can " + c.concludedLicense().spdxTag() + " use " + d.concludedLicense().spdxTag() + " : " );
      LicenseArbiter.aUsesB(c.concludedLicense(), d.concludedLicense());
      Log.d(LOG_TAG, "OK");      
    }      
    
  }

  public static boolean checkSubComponentsSafely(Component c)  {
    System.out.println("--------------------------------------------- ");
    for (Component d : c.dependencies()) {
      if ( d.concludedLicense()==null ||
           d.concludedLicense().spdxTag()==null ||
           d.concludedLicense().spdxTag().equals("UNKNOWN")) {
        return false;
      }
    }      
    return true;
  }

  
  
  /*  public static License mostPermissive(List<License> licenses) {
    int index = 0;
    License license = licenses.get(index);
    for (int i=0; i<licenses.size(); i++) {
      System.err.println("Check license: " + licenses.get(i).spdxTag());
      if (MostPermissiveLicenseComparator.
          comparator.
          compare(license, licenses.get(i))
          < 0 ) {
        System.err.println("Updating license"  + licenses.get(i).spdxTag());
        license = licenses.get(i);
        index=i;
      }
    }
    System.err.println("Updating license"  + license.spdxTag());
    return index;
    }*/

  public static boolean checkViolationSafely(Component c)  {
    //    System.out.print(" checkViolation: " + this.name);

    // First of all - return true if no deps
    if (c.dependencies().size()==0) {
      Log.d(LOG_TAG, " checkViolation: " + c.name() + ": OK  (no deps)");
      return true;
    } else {
      Log.d(LOG_TAG, "");
      for (Component d : c.dependencies()) {
        if (!checkViolationSafely(d)) {
          return false;
        }
      }      
    }
    Log.d(LOG_TAG, " checkViolation: " + c.name() + ": OK so far (" + c.dependencies().size() + " deps)");
    
    Log.d(LOG_TAG, " checkViolation " + c.name() );
    Log.d(LOG_TAG, " -----------------------------");

    for (Component d : c.dependencies()) {
      if (d.concludedLicense()==null) {
        d.licenses().sort(new MostPermissiveLicenseComparator());
        Log.d(LOG_TAG, " * Choosing among " + d.licenses().size() + " licenses  DINK");
        for (int i=0; i<d.licenses().size(); i++) {
          boolean ret = canAUseB(c.concludedLicense(), d.licenses().get(i));
          Log.d(LOG_TAG, " ** can " + c.concludedLicense().spdxTag() 
                             + " use " + d.licenses().get(i).spdxTag() + " : " + ret );
          if (ret) {
            d.concludedLicense( d.licenses().get(i));
            return true;
          }
        }
      } else {
        return canAUseB(c.concludedLicense(), d.concludedLicense());
      }
    }      
    return true;
  }
  
}
