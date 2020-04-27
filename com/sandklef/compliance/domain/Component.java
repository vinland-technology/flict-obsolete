package com.sandklef.compliance.domain;

import java.util.List;
import java.util.ArrayList;

import com.sandklef.compliance.utils.*;

public class Component {

  public static String LOG_TAG = Component.class.getSimpleName();

  // name, enough for now
  private String name;
  private License license;
  private List<Component> dependencies;
  
  public Component(String name, License license, List<Component> dependencies) {
    this.name = name;
    this.license = license;
    this.dependencies = dependencies;
    if (dependencies==null) {
      this.dependencies = new ArrayList<>();
    }
  }

  public String name() {
    return name;
  }
  
  public License license() {
    return license;
  }
  
  public List<Component> dependencies() {
    return dependencies;
  }
  
  public List<LicenseType> concludedLicenseTypes() {
    List<LicenseType> licenseTypes = new ArrayList<>();
    if (dependencies.size()==0) {
      licenseTypes.add(this.license().type());
      //      Log.d(LOG_TAG, " concluded : " + this.name() + " : " + licenseTypes.get(0));
    } else {
      for (Component c : dependencies) {
        List<LicenseType> cLicenseTypes = c.concludedLicenseTypes();
        Log.d(LOG_TAG, " concluded : " + c.name() + " : " + cLicenseTypes);
      }
    }
    return licenseTypes;
  }
  
  public LicenseType concludedLicenseType() {
    LicenseType licenseType ;

    Log.d(LOG_TAG, " INFO: checking " + name() + " (" + dependencies.size()+ "):  " + license().type() );
    if (dependencies.size()==0) {
      licenseType = this.license().type();
      //      Log.d(LOG_TAG, " concluded for " + name + " []  : " + licenseType);
      //      Log.d(LOG_TAG, " concluded : " + this.name() + " : " + licenseTypes.get(0));
    } else {
      LicenseType concludedLicenseType = null;
      for (Component c : dependencies) {
        Log.d(LOG_TAG, " INFO:       --- will check: " + c.name());
        if (concludedLicenseType==null) {
          Log.d(LOG_TAG, " INFO:       --- will check: " + c.name() + " no deps");
          concludedLicenseType = c.concludedLicenseType();
        } else {
          Log.d(LOG_TAG, " INFO:       --- will check: " + c.name() + " with deps");
          LicenseType cLicenseType = c.concludedLicenseType();
          concludedLicenseType = License.concludeType(cLicenseType, c.license().type());
          //          Log.d(LOG_TAG, " concluded -- : " + c.name() + " : " + cLicenseType);
        }
      }
      // Is the concluded license from the deps
      // violating the current?
      Log.d(LOG_TAG, " --violation check: " + concludedLicenseType + " ::: " + license.type());
      if ( concludedLicenseType.compareTo(license.type())<0) {
        Log.d(LOG_TAG, "");
        Log.d(LOG_TAG, "");
        Log.d(LOG_TAG, " ************************************");
        Log.d(LOG_TAG, " *** License type violation found ***");
        Log.d(LOG_TAG, " *** Actual license for " + name +  ": " + license.type());
        Log.d(LOG_TAG, " *** Concluded: " + concludedLicenseType);
        Log.d(LOG_TAG, " ************************************");
        Log.d(LOG_TAG, "");
        Log.d(LOG_TAG, "");
        // TODO: handle this violation a bit better
      }

      // TODO: decision needed. set to concluded or keep actual
      licenseType = license().type();
      Log.d(LOG_TAG, " --checking " + name() + " " + dependencies.size()+ ": " + concludedLicenseType);

    }
    Log.d(LOG_TAG, " INFO: checking " + name() + " (" + dependencies.size()+ "):  " + license().type() + "  concluded: " + licenseType);
    return licenseType;
  }

  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("Component: " + name + "\nLicense: " + license +"\nDependencies: [" );
    for (Component c : dependencies) {
      sb.append( "  " + c.name());
    }
    sb.append("]" );
    sb.append("\n" );
    return sb.toString();
  }
  
  
}
