// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.domain;

import java.util.Arrays;
import java.util.List;

public class Obligation{

  private final String name;
  private final String description;

  private Obligation(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public String name() {
    return name;
  }

  public String description() {
    return description;
  }

  @Override
  public String toString() {
    return name;
  }

  public static final String LINKING_COPYLEFTED_TAG = "linking_copylefted";
  public static final String LINKING_COPYLEFTED_NAME = "Linking is copylefted";
  public static final Obligation LINKING_COPYLEFTED = new Obligation(LINKING_COPYLEFTED_NAME, "");

  public static final String MODIFICATION_ALLOWED_TAG = "modification_allowed";
  public static final String MODIFICATION_ALLOWED_NAME = "Modification is allowed";
  public static final Obligation MODIFICATION_ALLOWED = new Obligation( MODIFICATION_ALLOWED_NAME, "");

  public static final String MODIFICATION_COPYLEFTED_TAG = "modification_copylefted";
  public static final String MODIFICATION_COPYLEFTED_NAME = "Modification is copylefted";
  public static final Obligation MODIFICATION_COPYLEFTED = new Obligation( MODIFICATION_COPYLEFTED_NAME, "");

  public static final String SUBLICENSING_ALLOWED_TAG = "sublicensing_allowed";
  public static final String SUBLICENSING_ALLOWED_NAME = "Sublicensing is allowed";
  public static final Obligation SUBLICENSING_ALLOWED = new Obligation( SUBLICENSING_ALLOWED_NAME, "");

  public static final String DISTRIBUTION_ALLOWED_TAG = "distribution_allowed";
  public static final String DISTRIBUTION_ALLOWED_NAME = "Distribution is allowed";
  public static final Obligation DISTRIBUTION_ALLOWED = new Obligation( DISTRIBUTION_ALLOWED_NAME, "");

  public static final String DISTRIBUTION_COPYLEFTED_TAG = "distribution_copylefted";
  public static final String DISTRIBUTION_COPYLEFTED_NAME = "Distribution is copylefted";
  public static final Obligation DISTRIBUTION_COPYLEFTED = new Obligation( DISTRIBUTION_COPYLEFTED_NAME, "");

  public static final String DISCLOSE_SOURCE_TAG = "disclose_source";
  public static final String DISCLOSE_SOURCE_NAME = "Disclose source";
  public static final Obligation DISCLOSE_SOURCE = new Obligation(DISCLOSE_SOURCE_NAME, "");

  public static final String STATE_CHANGES_TAG = "state_changes";
  public static final String STATE_CHANGES_NAME = "State changes";
  public static final Obligation STATE_CHANGES = new Obligation(STATE_CHANGES_NAME, "");

  public static final String INCLUDE_COPYRIGHT_TAG = "include_copyright";
  public static final String INCLUDE_COPYRIGHT_NAME = "Include copyright";
  public static final Obligation INCLUDE_COPYRIGHT = new Obligation(INCLUDE_COPYRIGHT_NAME, "");

  public static final String INCLUDE_LICENSE_TAG = "include_license";
  public static final String INCLUDE_LICENSE_NAME = "Include license";
  public static final Obligation INCLUDE_LICENSE = new Obligation(INCLUDE_LICENSE_NAME, "");

  public static final String INCLUDE_INSTALL_INSTRUCTIONS_TAG = "include_install_instructions";
  public static final String INCLUDE_INSTALL_INSTRUCTIONS_NAME = "Include install instructions";
  public static final Obligation INCLUDE_INSTALL_INSTRUCTIONS = new Obligation(INCLUDE_INSTALL_INSTRUCTIONS_NAME, "");


  public static final String INCLUDE_NOTICE_FILE_TAG = "include_notice_file";
  public static final String INCLUDE_NOTICE_FILE_NAME = "Include notice_file";
  public static final Obligation INCLUDE_NOTICE_FILE = new Obligation(INCLUDE_NOTICE_FILE_NAME, "");

  public static final String INCLUDE_NOTICE_ABOUT_LICENSE_TAG = "include_notice_about_license";
  public static final String INCLUDE_NOTICE_ABOUT_LICENSE_NAME = "Include notice_about_license";
  public static final Obligation INCLUDE_NOTICE_ABOUT_LICENSE = new Obligation(INCLUDE_NOTICE_ABOUT_LICENSE_NAME, "");

  public static List<String> obligationNames =
          Arrays.asList(MODIFICATION_ALLOWED_NAME,
                  MODIFICATION_COPYLEFTED_NAME,
                  SUBLICENSING_ALLOWED_NAME,
                  DISTRIBUTION_ALLOWED_NAME,
                  DISTRIBUTION_COPYLEFTED_NAME,
                  DISCLOSE_SOURCE_NAME,
                  STATE_CHANGES_NAME,
                  INCLUDE_COPYRIGHT_NAME);
}
