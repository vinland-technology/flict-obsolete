package com.sandklef.compliance.domain;

public class Obligation{

  private String name;
  private String description;

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
  
  public static final String COMMERCIAL_USE_NAME = "Commercial use";
  public static final String MODIFY_NAME = "Modify";
  public static final String DISTRIBUTE_NAME = "Distribute";
  public static final String PLACE_WARRANTY_NAME = "Place warranty";
  public static final String SUB_LICENSE_NAME = "Sublicense";
  public static final String HOLD_LIABLE_NAME = "Hold liable";
  public static final String INCLUDE_ORIGINAL_NAME = "Include original";
  public static final String DISCLOSE_SOURCE_NAME = "Disclose source";
  public static final String INCLUDE_COPYRIGHT_NAME = "Include copyright";
  public static final String STATE_CHANGES_NAME = "State changes";
  public static final String INCLUDE_LICENSE_NAME = "Include license";

  public static final String LINKING_AND_CHANGE_LICENSE_NAME = "Linking and change license";

  public static final Obligation COMMERCIAL_USE = new Obligation(COMMERCIAL_USE_NAME, "");
  public static final Obligation MODIFY = new Obligation(MODIFY_NAME, "");
  public static final Obligation DISTRIBUTE = new Obligation(DISTRIBUTE_NAME, "");
  public static final Obligation PLACE_WARRANTY = new Obligation(PLACE_WARRANTY_NAME, "");
  public static final Obligation SUB_LICENSE = new Obligation(SUB_LICENSE_NAME, "");
  public static final Obligation HOLD_LIABLE = new Obligation(HOLD_LIABLE_NAME, "");
  public static final Obligation INCLUDE_ORIGINAL = new Obligation(INCLUDE_ORIGINAL_NAME, "");
  public static final Obligation DISCLOSE_SOURCE = new Obligation(DISCLOSE_SOURCE_NAME, "");
  public static final Obligation INCLUDE_COPYRIGHT = new Obligation(INCLUDE_COPYRIGHT_NAME, "");
  public static final Obligation STATE_CHANGES = new Obligation(STATE_CHANGES_NAME, "");
  public static final Obligation INCLUDE_LICENSE = new Obligation(INCLUDE_LICENSE_NAME, "");
  public static final Obligation LINKING_AND_CHANGE_LICENSE = new Obligation(LINKING_AND_CHANGE_LICENSE_NAME, "");
}

