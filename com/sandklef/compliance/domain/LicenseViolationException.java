// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.domain;

import javax.swing.*;

public class LicenseViolationException extends Exception {

  private License user;
  private License usee;

  public Component component() {
    return component;
  }

  private Component component;

  public LicenseViolationException(String message) {
    super(message);
  }

  public LicenseViolationException(String message, Component c) {
    super(message);
    component = c;
  }

  public LicenseViolationException(String message,
                                   License user,
                                   License usee) {
    super(message);
    this.user = user;
    this.usee = usee;
  }
  
}