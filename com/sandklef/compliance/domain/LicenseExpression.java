package com.sandklef.compliance.domain;

import com.sandklef.compliance.utils.Log;

import java.util.ArrayList;
import java.util.List;

public class LicenseExpression {
    private static final String LOG_TAG = LicenseExpression.class.getSimpleName() ;

    /*

    LicenseExpression :=
        License
        ( License )
        LicenseExpression op LicenseExpression
        ( LicenseExpression op LicenseExpression )
     */

    public enum Operator {
        AND,
        OR
    };

    public static Operator stringToOperator(String op) {
        if (op.equals("&")) {
            return Operator.AND;
        } else if (op.equals("|")) {
            return Operator.OR;
        }
        return null;
    }

    private License license;
    private Operator op;
    private List<LicenseExpression> licenses;

    public LicenseExpression() {
        licenses = new ArrayList<>();
    }

    public LicenseExpression(License license) {
        this.license = license;
    }

    public void op(Operator op) {
        this.op = op;
    }

    public Operator op() {
        return this.op;
    }

    public void licenses(List<LicenseExpression> licenses) {
        this.licenses = licenses;
    }

    public void addLicense(LicenseExpression expr) {
        Log.d(LOG_TAG, " Add license: " + expr);
        licenses.add(expr);
    }

    public void addLicense(License license) {
        Log.d(LOG_TAG, " Add license: " + license.spdx());
        licenses.add(new LicenseExpression(license));
    }

  // How many paths does this expression cause/generate
  public int paths() {
    // If single license: 1
    if (license!=null) {
      Log.d(LOG_TAG, " paths: " + 1 + "  <---- " + license.spdx());
      return 1;
    }

    int pathCount = (op==Operator.AND?1:0);
    // If license expression(s)
    for (LicenseExpression le : licenses ) {

      int subPaths = le.paths();
      Log.d(LOG_TAG, " paths:          " + subPaths);
      
      if (op==Operator.AND) {
        pathCount *= subPaths;
      } else if (op==Operator.OR) {
        pathCount += subPaths;
      }
      Log.d(LOG_TAG, " paths:          => " + pathCount +  " (" + subPaths + ") " +  "  " + op);
    }
    Log.d(LOG_TAG, " paths: " + pathCount);
    return pathCount;
  }
  
    @Override
    public String toString() {
        if (license!=null) {
            return "\"" + license.spdx() + "\"";
        }
        return op.toString()  + licenses + "  ";
    }

}
