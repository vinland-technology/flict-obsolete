package com.sandklef.compliance;

import java.util.HashMap;
import java.util.Map;

public class ObligationBuilder{

  private Map<String, LicenseObligation> obligations;

  public ObligationBuilder() {
    obligations = new HashMap<>();
  }

  public ObligationBuilder add(LicenseObligation obligation) {
    obligations.put(obligation.name(),
                    obligation);
    return this;
  }
    
  public ObligationBuilder add(Obligation obligation, ObligationState state) {
    return this.add(new LicenseObligation(obligation, state));
  }
    
  public Map<String, LicenseObligation> build() {
    return obligations;
  }
  
}
