// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.utils;

import java.util.HashMap;
import java.util.Map;

import com.sandklef.compliance.domain.*;


public class ObligationBuilder{

  private final Map<String, LicenseObligation> obligations;

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
