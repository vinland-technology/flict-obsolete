package com.sandklef.compliance.utils;

import java.util.ArrayList;
import java.util.List;

import com.sandklef.compliance.domain.*;
import com.sandklef.compliance.utils.*;

public class LicenseArbiter {

    public static String LOG_TAG = LicenseArbiter.class.getSimpleName();

    public static ObligationState state(License license, String obligationName) {
        return license.obligations().get(obligationName).state();
    }

    public static boolean aUsesB(Component c, Report report,License user, License usee) throws LicenseViolationException, NoLicenseException {
        if (usee==null) {
            Log.d(LOG_TAG, "aUsesB(" + user + ", " + usee + ", ...)  ===>  false");
            report.violation().addObligationViolation(
                    new Violation.ObligationViolation(c));
            throw new NoLicenseException("Used licenses unknown ", user, usee);
        }
        Log.d(LOG_TAG, "aUsesB(" + user + ", " + usee + ", ...)");
        Log.d(LOG_TAG, "aUsesB(" + user.spdxTag() + ", " + usee.spdxTag() + ", ...)");

        if (user.spdxTag().equals(usee.spdxTag())) {
            Log.d(LOG_TAG, "Same license: true");
            return true;
        }

        // Below it will be assumed that the licenses differ

        // If usee is copylefted AND licenses differs
        //      => violation

        Log.d(LOG_TAG, "aUsesB: start checking  ");
        if (state(usee, Obligation.LINKING_COPYLEFTED_NAME) == ObligationState.TRUE &&
                (!user.equals(usee))) {
            Log.d(LOG_TAG, user.spdxTag() + " using " + usee.spdxTag() + " : violation detected");
            throw new LicenseViolationException(user.spdxTag() + " can not sublicense " + usee.spdxTag(), user, usee);
        }
        return true;
    }

    public static boolean canAUseB(Component c, Report report, License user, License usee) {
        try {
            return aUsesB(c, report, user, usee);
        } catch (LicenseViolationException | NoLicenseException e) {
            Log.d(LOG_TAG, "Exception");
            Log.d(LOG_TAG, "    message: " + e.getMessage());
//            Log.d(LOG_TAG, "    user:    (" + e.user.spdxTag() + ")");
  //          Log.d(LOG_TAG, "    usee:    (" + e.usee.spdxTag() + ")");
            return false;
        }
    }
  
/*
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

  */
  
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

    public static Report report(Component c) {
        Log.d(LOG_TAG, "reportViolations()    c: " + c.name());
        Report report = new Report(c);
        report(c, report);
        return report;
    }

    private static void report(Component c, Report report) {
        Log.d(LOG_TAG, "report() component: " + c.name() + " violation: " + c.name() + "   viols: " + report.violation.obligations().size());


        if (report.hasViolation()) {
            Log.d(LOG_TAG, c.name() + " violation already detected, bailing out");
            return;
        }

        // Sort the licenses in permissive order
        Log.d(LOG_TAG, c.name() + " licenses: " + c.licenses());
        c.licenses().sort(new MostPermissiveLicenseComparator());
        Log.d(LOG_TAG, c.name() + " licenses: " + c.licenses());


        // second - return true if no deps
        if (c.dependencies().size() == 0) {
            License license = c.licenses().get(0);
            Log.d(LOG_TAG, c.name() + ": has no deps, concluding license: " + license);
            Log.d(LOG_TAG, " DINKEY component:      " + c.name() + " DINKEY concluded:      " + license);
            Log.d(LOG_TAG, " DINKEY " + c.name() + " ch licenses:       no deps: " + c.licenses() );
            Log.d(LOG_TAG, " DINKEY " + c.name() + " choice:  " + license);
            c.concludedLicense(license);
            report.conslusion().addLicenseConclusion(new Conclusion.LicenseConclusion(c, license));
            return;
        } else {
            Log.d(LOG_TAG, " * checking dependencies for : " + c + "   nr: " + c.dependencies().size());
            for (Component d : c.dependencies()) {
                Log.d(LOG_TAG, " * checking component: " + d);
                report(d, report);
            }
        }

        // DEBUG: Loop over c's licenses
        Log.d(LOG_TAG, " * Component: " + c.name());
        Log.d(LOG_TAG, "   |--Licenses: " + c.licenses().size());
        for (License l : c.licenses()) {
            Log.d(LOG_TAG, "    |-- license: " + l.spdxTag());
        }

        // DEBUG: Loop over c's deps
        Log.d(LOG_TAG, "   |--Dependencies: " + c.dependencies().size());
        for (Component d : c.dependencies()) {
            Log.d(LOG_TAG, "     |--- " + d.name());
        }

        // For each of the current component's licenses
        // -- for each of the depdendency component's concluded license
        // ------ check if ok
        boolean allCleared = true;

        // List for all licenses that can be used - we will choose which later on
        List<License> myCheckedLicenses = new ArrayList<>();

        for (License l : c.licenses()) {
            allCleared = true;
            Log.d(LOG_TAG, "    can " + c.name() + " with: " + l.spdxTag() + " in components: " + c.dependencies().size());
            for (Component d : c.dependencies()) {

                Log.d(LOG_TAG, "    can " + c.name() + " (" + l.spdxTag() + ")    use: " + d.name() + "(" + d.concludedLicense() + ")");
                try {
                    aUsesB(d, report, l, d.concludedLicense());
                    // So, the license l can be used when using d
                    Log.d(LOG_TAG, "    can " + c.name() + " (" + l.spdxTag() + ")    use: " + d.name() + "(" + d.concludedLicense() + ") :  OK choice");
                    myCheckedLicenses.add(l);
                    Log.d(LOG_TAG, "    myCheckedLicenses for " + c.name() + "  added " + l.spdxTag() + "  to " + myCheckedLicenses);
                } catch (LicenseViolationException e) {
                    Log.d(LOG_TAG, "    can " + c.name() + " (" + l.spdxTag() + ")    use: " + d.name() + "(" + d.concludedLicense() + ") :  FAIL: " + e.getMessage());
                    allCleared = false;
                } catch (NoLicenseException e) {
                    Log.d(LOG_TAG, "    can " + c.name() + " (" + l.spdxTag() + ")    use: " + d.name() + "(" + d.concludedLicense() + ") :  FAIL: " + e.getMessage());
                    allCleared = false;
                    report.violation().addObligationViolation(
                            new Violation.ObligationViolation(c));
                    return;
                }
            }
            Log.d(LOG_TAG, " DINKEY 2 " + c.name() + " allCleared:        " + allCleared);
            if (allCleared) {
                Log.d(LOG_TAG, " DINKEY 2 " + c.name() + " allCleared:        " + allCleared +  " <----- choose: " + l + " for " + c.name());
                c.concludedLicense(l);
                report.conslusion().addLicenseConclusion(new Conclusion.LicenseConclusion(c, l));
                break;
            }
        }

        License concludedLicense = c.concludedLicense();
        Log.d(LOG_TAG, " DINKEY component:      " + c.name() + " concluded license: " + concludedLicense);
//        if (concludedLicense == null) {
  //      }
    }

/*

  private static void report2(Component c, Report report) {
    Log.d(LOG_TAG, "reportViolations() component: " + c.name() + " violation: " + c.name());

    // First of all - return true if no deps
    if (c.dependencies().size() == 0) {
      Log.d(LOG_TAG, " reportViolation: " + c.name() + ": OK  (no deps)");
      return;
    } else {
      Log.d(LOG_TAG, " * checking dependencies for : " + c + "   nr: " + c.dependencies().size());
      for (Component d : c.dependencies()) {
        Log.d(LOG_TAG, " * checking component: " + d);
        report(d, report);
      }
    }

    if (c.concludedLicense()==null) {
      Log.d(LOG_TAG, " * User license not conncluded: " + c.licenses());

      Log.d(LOG_TAG, " **** " + c.name() + " licenses       : " + c.licenses());
      c.licenses().sort(new MostPermissiveLicenseComparator());
      Log.d(LOG_TAG, " **** " + c.name() + " licenses sorted: " + c.licenses());
      
      for (License l : c.licenses()) {
        Log.d(LOG_TAG, " ** trying user license: " + l);
        for (Component d : c.dependencies()) {
          if (d.concludedLicense()==null) {
            Log.d(LOG_TAG, " **** " + d.name() + " licenses       : " + d.licenses());
            d.licenses().sort(new MostPermissiveLicenseComparator());
            Log.d(LOG_TAG, " **** " + d.name() + " licenses sorted: " + d.licenses());

            // Missing concluded license - find one
            Log.d(LOG_TAG, " * Choosing among " + d.licenses().size() + " licenses    for c: " + d.name());
            for (int i = 0; i < d.licenses().size(); i++) {
              Log.d(LOG_TAG, " * license  i: " + i + "  license: " + l);
              Log.d(LOG_TAG, " * license  i: " + i + "  license: " + d.licenses().get(i));
              boolean ret = canAUseB(l, d.licenses().get(i));
              Log.d(LOG_TAG, " ** can " + l.spdxTag()
                      + " use " + d.licenses().get(i).spdxTag() + " : " + ret);
              if (ret) {
                License cl = d.licenses().get(i);
                Log.d(LOG_TAG, " * setting concluded license for " + d + " to " + cl);
                Log.d(LOG_TAG, " **** license concluded for " + d.name() + ": " + cl);
                d.concludedLicense(cl);
                report.conslusion().addLicenseConclusion(new Conclusion.LicenseConclusion(d, cl));
                break;
              }
            }
          } else {
            try {
              aUsesB(l, d.concludedLicense());
              Log.d(LOG_TAG, " **** User license not concluded for " + c.name()+ ", but we have a candidate: " + l);
              c.concludedLicense(l);
            } catch (LicenseViolationException e) {
              Log.d(LOG_TAG, " **** Exception ..  for " + c.name()+ " but not stored. We tried license: " + l);
            }
          }
          if (d.concludedLicense()==null) {
            Log.d(LOG_TAG, "**** no luck with + " + c.name() );
          } else {
            Log.d(LOG_TAG, "**** Yes luck with + " + c.name() + " we found: " + d.concludedLicense());
            break;
          }
        }
        if (c.concludedLicense()==null) {
          Log.d(LOG_TAG, "**** Yes luck with + " + c.name() + " we found: " + c.concludedLicense());
          break;
        }
      }

      Log.d(LOG_TAG, " **** License for " + c.name() + ": " + c.concludedLicense() + " DINK");
    } else {
      Log.d(LOG_TAG, " **** single license " + c.concludedLicense() + " for " + c.name());
      for (Component d : c.dependencies()) {
        if (d.concludedLicense()==null) {
          d.licenses().sort(new MostPermissiveLicenseComparator());
          
          // Missing concluded license - find one
          Log.d(LOG_TAG, " * Choosing among " + d.licenses().size() + " licenses    for c: " + c.name());
          for (int i=0; i<d.licenses().size(); i++) {
            Log.d(LOG_TAG, " * license  i: " +  i + "  license: " + c.concludedLicense());
            Log.d(LOG_TAG, " * license  i: " +  i + "  license: " + d.licenses().get(i));
            try {
              aUsesB(c.concludedLicense(), d.licenses().get(i));
              Log.d(LOG_TAG, " ** can " + c.concludedLicense().spdxTag()
                  + " use " + d.licenses().get(i).spdxTag() + " : yes" );
              License l = d.licenses().get(i);
              Log.d(LOG_TAG, " **** setting concluded license for " +  d + " to " + l);
              d.concludedLicense(l);
              report.conslusion().addLicenseConclusion(new Conclusion.LicenseConclusion(d, l));
            } catch (LicenseViolationException e) {
              Log.d(LOG_TAG, "**** violation detected: " + c.concludedLicense() + " can't use " + d.licenses().get(i) );
              report.violation().addObligationViolation(
                      new Violation.ObligationViolation(
                              c,
                              d,
                              e.usee.obligations().get(Obligation.LINKING_COPYLEFTED_NAME)));
            }
          }
        } else {
          try {
            aUsesB(c.concludedLicense(), d.concludedLicense());
          } catch (LicenseViolationException e) {
            Log.d(LOG_TAG, "Exception");
            Log.d(LOG_TAG, "    message: " + e.getMessage());
            Log.d(LOG_TAG, "    user:    (" + e.user.spdxTag()+ ")");
            Log.d(LOG_TAG, "    usee:    (" + e.usee.spdxTag()+ ")");
            report.violation().addObligationViolation(
                                             new Violation.ObligationViolation(
                                                                               c,
                                                                               d,
                                                                               e.usee.obligations().get(Obligation.LINKING_COPYLEFTED_NAME)));
          }
        }
      }
      
    }
  }
 */
  /*
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
  */
}
