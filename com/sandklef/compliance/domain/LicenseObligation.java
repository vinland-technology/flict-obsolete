// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.domain;

public class LicenseObligation {
  
  private Obligation obligation;
  private ObligationState state;

  public LicenseObligation(Obligation obligation, ObligationState state) {
     this.obligation = obligation;
     this.state = state;
  }

  public Obligation obligation() {
    return obligation;
  }

  public String name() {
    return obligation.name();
  }

  public ObligationState state() {
    return state;
  }
  
}
  