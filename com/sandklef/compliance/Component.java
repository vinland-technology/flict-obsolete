package com.sandklef.compliance;

import java.util.List;
import java.util.ArrayList;


public class Component {

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
      //      System.out.println(" concluded : " + this.name() + " : " + licenseTypes.get(0));
    } else {
      for (Component c : dependencies) {
        List<LicenseType> cLicenseTypes = c.concludedLicenseTypes();
        System.out.println(" concluded : " + c.name() + " : " + cLicenseTypes);
      }
    }
    return licenseTypes;
  }
  
  public LicenseType concludedLicenseType() {
    LicenseType licenseType ;

    System.out.println(" INFO: checking " + name() + " (" + dependencies.size()+ "):  " + license().type() );
    if (dependencies.size()==0) {
      licenseType = this.license().type();
      //      System.out.println(" concluded for " + name + " []  : " + licenseType);
      //      System.out.println(" concluded : " + this.name() + " : " + licenseTypes.get(0));
    } else {
      LicenseType concludedLicenseType = null;
      for (Component c : dependencies) {
        System.out.println(" INFO:       --- will check: " + c.name());
        if (concludedLicenseType==null) {
          System.out.println(" INFO:       --- will check: " + c.name() + " no deps");
          concludedLicenseType = c.concludedLicenseType();
        } else {
          System.out.println(" INFO:       --- will check: " + c.name() + " with deps");
          LicenseType cLicenseType = c.concludedLicenseType();
          concludedLicenseType = License.concludeType(cLicenseType, c.license().type());
          //          System.out.println(" concluded -- : " + c.name() + " : " + cLicenseType);
        }
      }
      // Is the concluded license from the deps
      // violating the current?
      System.out.println(" --violation check: " + concludedLicenseType + " ::: " + license.type());
      if ( concludedLicenseType.compareTo(license.type())<0) {
        System.out.println("");
        System.out.println("");
        System.out.println(" ************************************");
        System.out.println(" *** License type violation found ***");
        System.out.println(" *** Actual license for " + name +  ": " + license.type());
        System.out.println(" *** Concluded: " + concludedLicenseType);
        System.out.println(" ************************************");
        System.out.println("");
        System.out.println("");
        // TODO: handle this violation a bit better
      }

      // TODO: decision needed. set to concluded or keep actual
      licenseType = license().type();
      System.out.println(" --checking " + name() + " " + dependencies.size()+ ": " + concludedLicenseType);

    }
    System.out.println(" INFO: checking " + name() + " (" + dependencies.size()+ "):  " + license().type() + "  concluded: " + licenseType);
    return licenseType;
  }

  public void checkViolation() throws LicenseViolationException {
    //    System.out.print(" checkViolation: " + this.name);
    if (dependencies.size()==0) {
      System.out.println(" checkViolation: " + this.name + ": OK  (no deps)");
      return;
    } else {
      System.out.println();
      for (Component c : dependencies) {
        c.checkViolation();
      }      
    }
    System.out.println(" checkViolation: " + this.name + ": OK so far (" + dependencies.size() + " deps)");

    System.out.println(" checkViolation " + name );
    System.out.println(" -----------------------------");
    for (Component c : dependencies) {
      System.out.print(" * can " + license.spdxTag() + " use " + c.license().spdxTag() + " : " );
      LicenseArbiter.aUsesB(license, c.license());
      System.out.println("OK");      
    }      
    
  }
  
  public boolean checkViolationSafely()  {
    //    System.out.print(" checkViolation: " + this.name);
    if (dependencies.size()==0) {
      System.out.println(" checkViolation: " + this.name + ": OK  (no deps)");
      return true;
    } else {
      System.out.println();
      for (Component c : dependencies) {
        c.checkViolationSafely();
      }      
    }
    System.out.println(" checkViolation: " + this.name + ": OK so far (" + dependencies.size() + " deps)");

    System.out.println(" checkViolation " + name );
    System.out.println(" -----------------------------");
    for (Component c : dependencies) {
      System.out.print(" * can " + license.spdxTag() + " use " + c.license().spdxTag() + " : " );
      try {
        LicenseArbiter.aUsesB(license, c.license());
      } catch (LicenseViolationException e) {
        System.out.println("Exception");      
        System.out.println("    message: " + e.getMessage());
        System.out.println("    user:    " + name + " (" + e.user.spdxTag()+ ")");
        System.out.println("    usee:    " + c.name() + " (" + e.usee.spdxTag()+ ")");
        return false;
      }
      System.out.println("OK");      
    }      
    return true;
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
