package com.sandklef.compliance.utils;

import com.sandklef.compliance.domain.License;
import com.sandklef.compliance.domain.LicenseExpression;
import com.sandklef.compliance.domain.LicenseExpressionException;

import java.util.ArrayList;
import java.util.Collections;

import static com.sandklef.compliance.domain.License.GPL_2_0_LATER_SPDX;

public class LicenseExpressionParser {

    private static final String LOG_TAG = LicenseExpressionParser.class.getSimpleName();

    /*

        ( a & ( b & c) ) | ( d & e ) | f | (g)

	a AND 
	AND [ a 
	  AND [ b c ]
	AND [ a AND [ b c ]
     */

    public boolean letterNext(String s) {
        return s.charAt(0)>='A' && s.charAt(0)<='Z';
    }

    public boolean opNext(String s) {
        if (s.equals("")) { return false; }
        return s.charAt(0)=='&' || s.charAt(0)=='|';
    }

    public LicenseExpression.Operator op(String s) throws LicenseExpressionException {
        if (s.charAt(0)=='&') {
            return LicenseExpression.Operator.AND;
        } else if (s.charAt(0)=='|') {
            return LicenseExpression.Operator.OR;
        }
        throw new LicenseExpressionException("Missing operator");
    }

    public String readLicense(String s) {
        // look for operator
        ArrayList<Integer> indices = new ArrayList<>();
        indices.add(s.indexOf("&"));
        indices.add(s.indexOf("|"));
        indices.add(s.indexOf("("));
        indices.add(s.indexOf(")"));
        Collections.sort(indices);
        int index = -1;
        for ( Integer i : indices ) {
            if (i!=-1) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return s;
        }

        return s.substring(0, index);
    }

    
    public LicenseExpression parse(String expression) throws LicenseExpressionException {
        String expr = expression.replaceAll("\\s", "");
        Log.d(LOG_TAG, "parse: \"" + expr + "\"");

        // expr begins with "("
        if (expr.charAt(0)=='(') {
            Log.d(LOG_TAG, "   ( found: ");
            Log.d(LOG_TAG, "  expr to parse with paranthesises: " + expr);
            int parCount = 0;
            int i;
            for (i = 0; i < expr.length(); i++) {
                char current = expr.charAt(i);
                if (current == '(') {
                    parCount++;
                } else if (current == ')') {
                    parCount--;
                }
                if (parCount == 0) {
                    Log.d(LOG_TAG, "  current at break: " + current);
                    break;
                }
            }
            String subExpr = expr.substring(1, i);
            expr = expr.substring(i + 1);
            Log.d(LOG_TAG, "  expr remain  : " + expr);
            LicenseExpression le = new LicenseExpression();


            Log.d(LOG_TAG, "  expr remain  : " + expr);

            // Either operator or end of string (return new expr)
            if (!opNext(expr)) {
                return parse(subExpr);
            }
            le.addLicense(parse(subExpr));

            LicenseExpression.Operator op = op(expr);
            Log.d(LOG_TAG, " op found: " + op);
            expr = expr.substring(1);

            le.op(op);
            le.addLicense(parse(expr));
            return le;
        } else if (letterNext(expr)) {
            // Begins with a letter, so LICENSE op ....

            String licenseString = readLicense(expr);
            expr = expr.substring(licenseString.length());
            Log.d(LOG_TAG, " license 1 found: " + licenseString);

            LicenseExpression le = new LicenseExpression();

            // Either operator or end of string (return new expr)
            if (!opNext(expr)) {
                return new LicenseExpression(LicenseStore.getInstance().license(licenseString));
            }

            LicenseExpression.Operator op = op(expr);
            Log.d(LOG_TAG, " op found: " + op);
            expr = expr.substring(1);

            le.op(op);
            le.addLicense(new LicenseExpression(LicenseStore.getInstance().license(licenseString)));

            while (true) {
                Log.d(LOG_TAG, "  reading rest: " + expr);
                if (letterNext(expr)) {
                    Log.d(LOG_TAG, "  reading rest: license found " + expr);
                    licenseString = readLicense(expr);
                    le.addLicense(new LicenseExpression(LicenseStore.getInstance().license(licenseString)));

                    Log.d(LOG_TAG, " license n found: \"" + licenseString + "\"   from: " + expr);
                    // read away license
                    expr = expr.substring(licenseString.length());
                } else {
                    Log.d(LOG_TAG, "  --- implement me :)");
                    le.addLicense(parse(expr));
                }
                if (expr.equals("")) {
                    Log.d(LOG_TAG, " license n found: \"" + licenseString + "\"  no more to read");
                    return le;
                }
                Log.d(LOG_TAG, " license n found: " + licenseString + "   remains: \"" + expr + "\"");

                if (opNext(expr)) {
                    if (! op.equals(op(expr))) {
                        throw new LicenseExpressionException("Can't have different operators within same parenthesises");
                    }
                }
                // read away operator
                expr = expr.substring(1);
            }
        }


        return null;
    }
/*
    public LicenseExpression parse2(String expression) throws LicenseExpressionException {
        String expr = expression.replaceAll("\\s", "");
        Log.d(LOG_TAG, "parse: \"" + expr + "\"");
        if (expr.startsWith("(")) {
            if (!expr.endsWith(")")) {
                throw new LicenseExpressionException("An expression beginning with ( must end with )");
            }
            // peak at next
            if (expr.startsWith("((")) {
                Log.d(LOG_TAG, " (( found");
                if (expr.endsWith("))")) {
                    // (( expr )) => ( expr ), to removed useless ()
                    String trimmedExpr = expr.substring(1, expr.length()-1);
                    Log.d(LOG_TAG, "  parse: \"" + trimmedExpr + " instead of " + expr);
                    return parse(trimmedExpr);
                }

                // We have something like (( le ) op leRight )
                Log.d(LOG_TAG, " UH OH :....");
                int nextIndex = expr.indexOf(')');
                if (nextIndex==-1) {
                    throw new LicenseExpressionException("Can't parse expression: \"" + expr + "\"");
                }
                String firstExpr = expr.substring(1,nextIndex+1);
                Log.d(LOG_TAG, " firstExpr: \"" + firstExpr + "\"");
                LicenseExpression le = parse(firstExpr);
                Log.d(LOG_TAG, " expr le parse ... done?" + nextIndex + "  " + expr.length());
                if (nextIndex == expr.length()-1) {
                    return le;
                }
                Log.d(LOG_TAG, " expr le parse more on  find op: " + (nextIndex+1) + " | " + (nextIndex+2)  + " " + expr.length());
                Log.d(LOG_TAG, " expr le parse more on  find op: \"" + expr.substring(nextIndex+1,nextIndex+2) + "\"");
                LicenseExpression.Operator op = LicenseExpression.stringToOperator(expr.substring(nextIndex+1,nextIndex+2));
                Log.d(LOG_TAG, " expr le parse more on    op: \"" + op + "\"");
                String leRightString = expr.substring(nextIndex+2);

                nextIndex = leRightString.indexOf(')');
                if (nextIndex==-1) {
                    LicenseExpression leRight = parse(leRightString);
                    LicenseExpression returnLe = new LicenseExpression();
                    returnLe.op(op);
                    returnLe.addLicense(le);
                    returnLe.addLicense(leRight);
                    Log.d(LOG_TAG, " expr returnLe:  \"" + returnLe + "\"");
                    return returnLe;
                } else {
                    // ) found - parse the left side
                    Log.d(LOG_TAG, " some extra: " + leRightString.substring(0,nextIndex));
                    LicenseExpression leRight = parse(leRightString.substring(0,nextIndex));
                    Log.d(LOG_TAG, " some extra: leRight " + leRight);
                    String rest = leRightString.substring(nextIndex+1);

                    // if no right side (of ")")
                    if (rest.length()==0) {
                        LicenseExpression returnLe = new LicenseExpression();
                        returnLe.op(op);
                        returnLe.addLicense(le);
                        returnLe.addLicense(leRight);
                        Log.d(LOG_TAG, " expr returnLe:  \"" + returnLe + "\"");
                        return returnLe;
                    }

                    Log.d(LOG_TAG, " some extra: rest    " + rest + " <--");

                    // ) op expr
                    while (true) {
                        op = LicenseExpression.stringToOperator(rest.substring(0, 1));

                        Log.d(LOG_TAG, " some extra: op    " + op);
                        Log.d(LOG_TAG, " some extra: rest \"" + rest + "\"");

                        rest = rest.substring(1);

                        Log.d(LOG_TAG, " some extra: rest \"" + rest + "\"");
                        LicenseExpression returnLe = new LicenseExpression();
                        returnLe.op(op);
                        returnLe.addLicense(le);
                        leRight = parse(rest);
                        returnLe.addLicense(leRight);
                        Log.d(LOG_TAG, " expr returnLe:  \"" + returnLe + "\"");
                    }
                    return returnLe;
                }
            } else {
                Log.d(LOG_TAG, " (  found in =================================> " + expr);
                if (!expr.endsWith(")")) {
                    throw new LicenseExpressionException("An expression beginning with ( must end with )");
                }
                if (!expr.contains("(")) {
                    throw new LicenseExpressionException("Expression contains (. This might be a bug");
                }
                String shortExpr = expr.substring(0, expr.length() - 1);
                if (shortExpr.contains(")")) {
                    throw new LicenseExpressionException("Expression contains ). This might be a bug. " + shortExpr);
                }
                // We have something like ( expr )
                LicenseExpression le = new LicenseExpression();

                expr = expr.substring(1);

                String[] exprs = expr.split("&");
                le.op(LicenseExpression.Operator.AND);
                for (int i = 0; i < exprs.length; i++) {
                    String lic = exprs[i];
                    if (lic.endsWith(")")) {
                        Log.d(LOG_TAG, "lic: " + lic + "      i: " + i + "   length: " + exprs.length + "  expr: " + expr);
                        // if it is not the last expr, then it is a faulty expression
                        if (i != exprs.length - 1) {
                            throw new LicenseExpressionException("License can't end with \")\" " + lic + "  i: " + i + "   length: " + exprs.length);
                        } else {
                            lic = lic.substring(0, lic.length() - 1);
                        }
                    }
                    License license = LicenseStore.getInstance().license(lic.trim());
                    Log.d(LOG_TAG, " & expr add: " + lic + " (" + license + ")");
                    le.addLicense(license);
                }
                Log.d(LOG_TAG, " parsed AND:  " + le);
                return le;
            }
        } else if (expr.contains("&")) {
             Log.d(LOG_TAG, " &    work on expr: \"" + expr + "\"");
             if (expr.contains("|")) {
                 throw new LicenseExpressionException("A license expression can't contain both & and |");
             }
             LicenseExpression le = new LicenseExpression();

             String[] exprs = expr.split("&");
             le.op(LicenseExpression.Operator.AND);
             for (int i=0; i<exprs.length; i++) {
                String lic = exprs[i];
                if (lic.endsWith(")")) {
                    Log.d(LOG_TAG, "lic: " + lic + "      i: " + i + "   length: " + exprs.length +  "  expr: " + expr);
                    // if it is not the last expr, then it is a faulty expression
                    if (i != exprs.length-1) {
                        throw new LicenseExpressionException("License can't end with \")\" " + lic);
                    } else {
                        lic.substring(0, lic.length()-1);
                    }
                }
                License license = LicenseStore.getInstance().license(lic.trim());
                Log.d(LOG_TAG, " & expr add: " + lic + " (" + license + ")");
                le.addLicense(license);
            }
            Log.d(LOG_TAG, " parsed AND:  " + le);
            return le;
        } else if (expr.contains("|")) {
            Log.d(LOG_TAG, " | ");
            if (expr.contains("&")) {
                throw new LicenseExpressionException("A license expression can't contain both & and |");
            }
        } else {
            Log.d(LOG_TAG, " SIMPLE EXPR:  " + expr);
            Log.d(LOG_TAG, " SIMPLE EXPR:  " + LicenseStore.getInstance().license(expr));
            return new LicenseExpression(LicenseStore.getInstance().license(expr));
        }
        Log.d(LOG_TAG, "   return null from: " + expr);
        return null;
    }

 */

}
