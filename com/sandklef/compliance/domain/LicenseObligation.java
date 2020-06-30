// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.domain;

public class LicenseObligation {

//  private Component component;
  private final Obligation obligation;
  private final ObligationState state;

  public LicenseObligation(/*Component component,*/ Obligation obligation, ObligationState state) {
//     this.component = component;
     this.obligation = obligation;
     this.state = state;
  }

  /*
  public Component component(){
    return component;
  }*/

  public Obligation obligation() {
    return obligation;
  }

  public String name() {
    return obligation.name();
  }

  public ObligationState state() {
    return state;
  }

  @Override
  public String toString() {
    return obligation +
            " =" + state;
    }
}
  