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

    @Override
    public String toString() {
        if (license!=null) {
            return "\"" + license.spdx() + "\"";
        }
        return op.toString()  + licenses + "  ";
    }

}
