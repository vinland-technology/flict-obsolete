package com.sandklef.compliance.utils;

import com.sandklef.compliance.domain.IllegalLicenseExpression;
import com.sandklef.compliance.domain.License;
import com.sandklef.compliance.domain.LicenseExpression;
import com.sandklef.compliance.domain.LicenseExpressionException;

import java.util.ArrayList;
import java.util.Collections;


public class LicenseExpressionParser {

    private static final String LOG_TAG = LicenseExpressionParser.class.getSimpleName();

    public boolean letterNextUC(String s) {
        return s.charAt(0) >= 'A' && s.charAt(0) <= 'Z';
    }

    public boolean letterNextLC(String s) {
        return s.charAt(0) >= 'a' && s.charAt(0) <= 'z';
    }

    public boolean opNext(String s) {
        if (s.equals("")) {
            return false;
        }
        return s.charAt(0) == '&' || s.charAt(0) == '|';
    }

    public LicenseExpression.Operator op(String s) /*throws LicenseExpressionException*/ {
        Log.d(LOG_TAG, " get op from: \"" + s + "\"");
        if (s.charAt(0) == '&') {
            return LicenseExpression.Operator.AND;
        } else if (s.charAt(0) == '|') {
            return LicenseExpression.Operator.OR;
        }
        return null;
//        throw new LicenseExpressionException("Missing operator");
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
        for (Integer i : indices) {
            if (i != -1) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return s;
        }

        return s.substring(0, index);
    }

    public static String readParenthisedExpr(String expr) {
        if (expr.charAt(0) != '(') {
            return "";
        }

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
        return subExpr;
    }

    public String fixLicenseExpression(String expr) throws LicenseExpressionException {
        // remove space
        Log.d(LOG_TAG, "fixLicenseExpression: " + expr);
        expr = expr.replaceAll("\\s", "");
        Log.d(LOG_TAG, "fixLicenseExpression: --->" + expr + "<---");

        String first = null;
        boolean isParanthesis = true;

        if (expr.charAt(0) == '(') {
            Log.d(LOG_TAG, "   ( found:  in " + expr + " <-----------------------------------------");
            isParanthesis = true;
            String pExpr = readParenthisedExpr(expr);
            expr = expr.substring(pExpr.length() + 1);
            Log.d(LOG_TAG, "(  found    : " + pExpr);
            Log.d(LOG_TAG, "(  remaining    : \"" + expr + "\"");

            if (expr.length() == 1 && expr.equals(")")) {
                if (letterNextUC(expr)) {
                    Log.d(LOG_TAG, " discard () from : " + pExpr);
                    return fixLicenseExpression(pExpr);
                } else {
                    Log.d(LOG_TAG, " add () from : " + pExpr);
                    return "(" + fixLicenseExpression(pExpr) + ")";
                }
            } else if (expr.length()==0) {
                Log.d(LOG_TAG, " noting left in expr,   pExpr: " + pExpr);
                return "(" + fixLicenseExpression(pExpr) + ")";
            }

            if (expr.charAt(0)==')') {
                expr = expr.substring(1);
            }
            Log.d(LOG_TAG, "(  remaining    : \"" + expr + "\"   after returns pExpr: " + pExpr);
            first = "(" + pExpr + ")" ;
        } else if (letterNextUC(expr)) {
            Log.d(LOG_TAG, "  NO ( found: ");
            String licenseString = readLicense(expr);
            expr = expr.substring(licenseString.length());
            first = licenseString;
        } else if (letterNextLC(expr)) {
            Log.d(LOG_TAG, "Licenses should perhaps start with an upper case letter: \"" + expr + "\" is therefore suspected to be invalid... but ok");
            String licenseString = readLicense(expr);
            expr = expr.substring(licenseString.length());
            first = licenseString;
        } else {
            throw new LicenseExpressionException("Invalid expression: " + first);
        }

            Log.d(LOG_TAG, "  license      : " + first);
            Log.d(LOG_TAG, "  expr         : " + expr);

            StringBuffer sb = new StringBuffer();

            if (expr.length() == 0) {
                Log.d(LOG_TAG, "  returning: " + first);
                return first;
            }

            LicenseExpression.Operator op = op(expr);
            if ( op == null ) {
                throw new LicenseExpressionException("Invalid operator (null)");
            }
            expr = expr.substring(1);
            if (op == LicenseExpression.Operator.AND) {
                Log.d(LOG_TAG, "  AND found: " + first + "  remain: " + expr + "   current: " + first);
                if (isParanthesis) {
                    sb.append("(");
                }
                sb.append(first);
                first="";
                // loop until op==|
                while (true) {
                    if (op == LicenseExpression.Operator.OR) {
                        Log.d(LOG_TAG, "  OR found: " + first + " quit loop");
                        if (isParanthesis) {
                            sb.append(")");
                        }
                        break;
                    }
                    if (expr.charAt(0) == '(') {
                        String innerExpr = readParenthisedExpr(expr);
                        sb.append(LicenseExpression.operatorToString(LicenseExpression.Operator.AND));
                        sb.append("("+fixLicenseExpression(innerExpr)+")");
                        Log.d(LOG_TAG, "  ( found inner:  " + innerExpr);
                        expr = expr.substring(innerExpr.length()+2);
                        Log.d(LOG_TAG, "  ( found expr :  " + expr);
                    } else if (letterNextUC(expr)) {
                        String innerExpr = readLicense(expr);
                        sb.append(LicenseExpression.operatorToString(LicenseExpression.Operator.AND));
                        sb.append(innerExpr);
                        Log.d(LOG_TAG, "  license found in AND statement expr:  " + innerExpr);
                        Log.d(LOG_TAG, "  license found in AND statement expr:  " + innerExpr.length());
                        Log.d(LOG_TAG, "  license found in AND statement expr:  " + expr.length());
                        if (innerExpr.length()==expr.length()) {
                            Log.d(LOG_TAG, "  license found in AND statement remain: " + expr + "   (" + sb.toString() + ")");
                            if (isParanthesis) {
                                sb.append(")");
                            }
                            expr="";
                            break;
                        }
                        expr = expr.substring(innerExpr.length());
                        Log.d(LOG_TAG, "  license found in AND statement remain: " + expr + "   (" + sb.toString() + ")");
                    } else {
                        throw new LicenseExpressionException("Invalid expression: " + expr);
                    }

                    if (expr.length()==0) {
                        if (isParanthesis) {
                            sb.append(")");
                        }
                        break;
                    }
                    Log.d(LOG_TAG, "  license found in AND statement check next op: " + expr);
                    op = op(expr);
                    expr = expr.substring(1);
                }

                //sb.append(")");
            }


            Log.d(LOG_TAG, "  HANDLE: " + expr);

            if (op == LicenseExpression.Operator.OR) {
                if (first.equals("")) {
                    Log.d(LOG_TAG, "    ------------------------------ " + expr);
                    sb.append(LicenseExpression.operatorToString(LicenseExpression.Operator.OR));
                    sb.append(fixLicenseExpression(expr));
                } else {
                    Log.d(LOG_TAG, "  OR found, append: " + first);
                    sb.append(first);
                    sb.append(LicenseExpression.operatorToString(op));
                    sb.append(fixLicenseExpression(expr));
                }
            } else if ( expr.length() > 0){
                throw new LicenseExpressionException("Invalid operator: " + op +  " \"" + expr + "\"");
            }

            Log.d(LOG_TAG, "  RETURNING: " + sb.toString());
            return sb.toString();
    }

    public LicenseExpression parse(String expression) throws LicenseExpressionException, IllegalLicenseExpression {
        String exprToParse = fixLicenseExpression(expression);
        Log.d(LOG_TAG, "parse:       " + expression);
        Log.d(LOG_TAG, "parse fixed: " + exprToParse);
        LicenseExpression le = doParse(exprToParse);
        Log.d(LOG_TAG, "parse: " + exprToParse);
        Log.d(LOG_TAG, "parse: " + le);
        return le;
    }

    private LicenseExpression doParse(String expression) throws LicenseExpressionException, IllegalLicenseExpression {
        String expr = expression.replaceAll("\\s", "");
        Log.d(LOG_TAG, "doParse: \"" + expr + "\"    calling");

        LicenseExpression le = new LicenseExpression();
        LicenseExpression.Operator op = null;

        while(true) {
            Log.d(LOG_TAG, "doParse loop: \"" + expr + "\"") ;
            if (le.valid()) {
                Log.d(LOG_TAG, "doParse loop: \"" + expr + "\"   le: " + le) ;
            }

            if (expr.length()==0) {
                Log.d(LOG_TAG, "doParse loop: expr EOF") ;
                break;
            }

            Log.d(LOG_TAG, "doParse before: \"" + expr + "\"");
            Log.d(LOG_TAG, "doParse before: " + (expr.charAt(0)=='(') );
            if (expr.charAt(0) == '(') {
                LicenseExpression firstExpr = null;
                Log.d(LOG_TAG, "doParse parenth: \"" + expr + "\"");
                String firstExprStr = readParenthisedExpr(expr);
                expr = expr.substring(firstExprStr.length() + 2);
                Log.d(LOG_TAG, "doParse parenth: \"" + expr + "\"");
                firstExpr = doParse(firstExprStr);
                Log.d(LOG_TAG, "doParse parenth: firstExpr: \"" + firstExpr + "\"");
                Log.d(LOG_TAG, "doParse parenth: \"" + expr + "\" ---");
                le.addLicense(firstExpr);
            } else if (letterNextUC(expr)) {
                LicenseExpression firstExpr = null;
                Log.d(LOG_TAG, "doParse license: \"" + expr + "\"");
                Log.d(LOG_TAG, "doParse license: \"" + expr + "\"");
                String firstExprStr = readLicense(expr);
                License license = LicenseStore.getInstance().license(firstExprStr);
                Log.d(LOG_TAG, "doParse license: \"" + expr + "\"   license: " + license);
                firstExpr = new LicenseExpression(license);
                le.addLicense(firstExpr);
                Log.d(LOG_TAG, "doParse license: \"" + expr + "\"" + " " + firstExprStr.length() + " " + expr.length());
                expr = expr.substring(firstExprStr.length());
                Log.d(LOG_TAG, "doParse license: \"" + expr + "\"" + " " + firstExprStr.length() + " " + expr.length());
                Log.d(LOG_TAG, "doParse license: \"" + expr + "\"" + "       " + firstExprStr + " is valid: " + firstExpr.valid());
            } else {
                throw new IllegalLicenseExpression("Invalid expression, from: " + expr + " and previous op: " + op);
            }


            Log.d(LOG_TAG, " op: " + op);
            Log.d(LOG_TAG, " le: " + le);

            if (expr.length()==0) {
                Log.d(LOG_TAG, "doParse license: ------ nothing more: ");
                break;
            }

            // If op is null, then this is the first expr in this expression (with same operators)
            if (op==null) {
                // Set op for this expression
                op = op(expr);
                le.op(op);
                Log.d(LOG_TAG, "doParse license: op null, and now set to: " + op + "  expr: " + expr);
                expr = expr.substring(1);
                Log.d(LOG_TAG, "doParse license: op null, and now set to: " + op + "  expr: " + expr);
            } else {
                // We should homogenous expressions now, so this should not happen
                if ( op != op(expr) ) {
                    throw new IllegalLicenseExpression("Invalid expression: " + expr + " given previous op: " + op);
                }

                // read away operator (which is the same, otherwise exception))
                Log.d(LOG_TAG, "doParse license: read away op from " + expr);
                expr = expr.substring(1);
                Log.d(LOG_TAG, "doParse license: read away op to   " + expr);
            }
        }

//        Log.d(LOG_TAG, "doParse return 3 a: " + le.valid());
  //      Log.d(LOG_TAG, "doParse return 3 b: " + le.licenses());
    //    Log.d(LOG_TAG, "doParse return 3 c: " + le);

        if (le.valid()) {
            Log.d(LOG_TAG, "doParse return 3:  NORMAL " + le);
            return le;
        }
        if (le.licenses()!=null) {
            Log.d(LOG_TAG, "doParse return 3: SECOND: " + le);
            Log.d(LOG_TAG, "doParse return 3: SECOND: " + le.licenses());
            return le.licenses().get(0);
        }


        Log.d(LOG_TAG, "doParse return 3: PROBLEM -----------------------");
        System.exit(1);
        return le;
    }

}
