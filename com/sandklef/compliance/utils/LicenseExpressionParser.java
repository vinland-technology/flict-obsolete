package com.sandklef.compliance.utils;

import com.sandklef.compliance.domain.IllegalLicenseExpression;
import com.sandklef.compliance.domain.License;
import com.sandklef.compliance.domain.LicenseExpression;
import com.sandklef.compliance.domain.LicenseExpressionException;

import javax.management.JMException;
import java.util.ArrayList;
import java.util.Collections;

import static com.sandklef.compliance.domain.License.GPL_2_0_LATER_SPDX;

public class LicenseExpressionParser {

    private static final String LOG_TAG = LicenseExpressionParser.class.getSimpleName();

    public boolean letterNext(String s) {
        return s.charAt(0) >= 'A' && s.charAt(0) <= 'Z';
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

        if (expr.charAt(0) == '(') {
            Log.d(LOG_TAG, "   ( found:  in " + expr + " <-----------------------------------------");
            String pExpr = readParenthisedExpr(expr);
            expr = expr.substring(pExpr.length() + 1);
            Log.d(LOG_TAG, "(  found    : " + pExpr);
            Log.d(LOG_TAG, "(  remaining    : \"" + expr + "\"");

            if (expr.length() == 1 && expr.equals(")")) {
                if (letterNext(expr)) {
                    Log.d(LOG_TAG, " discard () from : " + pExpr);
                    return fixLicenseExpression(pExpr);
                } else {
                    Log.d(LOG_TAG, " add () from : " + pExpr);
                    return "(" + fixLicenseExpression(pExpr) + ")";
                }
            } else if (expr.length()==0) {
                Log.d(LOG_TAG, " noting left in expr,   pExpr: " + pExpr);
                return fixLicenseExpression(pExpr);
            }

            if (expr.charAt(0)==')') {
                expr = expr.substring(1);
            }
            Log.d(LOG_TAG, "(  remaining    : \"" + expr + "\"   after returns pExpr: " + pExpr);
            first = pExpr;
        } else if (letterNext(expr)) {
            Log.d(LOG_TAG, "  NO ( found: ");
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
                sb.append("(");
                sb.append(first);
                first="";
                // loop until op==|
                while (true) {
                    if (op == LicenseExpression.Operator.OR) {
                        Log.d(LOG_TAG, "  OR found: " + first + " quit loop");
                        sb.append(")");
                        break;
                    }
                    if (expr.charAt(0) == '(') {
                        String innerExpr = readParenthisedExpr(expr);
                        sb.append(LicenseExpression.operatorToString(LicenseExpression.Operator.AND));
                        sb.append("("+fixLicenseExpression(innerExpr)+")");
                        Log.d(LOG_TAG, "  ( found inner:  " + innerExpr);
                        expr = expr.substring(innerExpr.length()+2);
                        Log.d(LOG_TAG, "  ( found expr :  " + expr);
                    } else if (letterNext(expr)) {
                        String innerExpr = readLicense(expr);
                        sb.append(LicenseExpression.operatorToString(LicenseExpression.Operator.AND));
                        sb.append(innerExpr);
                        Log.d(LOG_TAG, "  license found in AND statement expr:  " + innerExpr);
                        Log.d(LOG_TAG, "  license found in AND statement expr:  " + innerExpr.length());
                        Log.d(LOG_TAG, "  license found in AND statement expr:  " + expr.length());
                        if (innerExpr.length()==expr.length()) {
                            Log.d(LOG_TAG, "  license found in AND statement remain: " + expr + "   (" + sb.toString() + ")");
                            sb.append(")");
                            expr="";
                            break;
                        }
                        expr = expr.substring(innerExpr.length());
                        Log.d(LOG_TAG, "  license found in AND statement remain: " + expr + "   (" + sb.toString() + ")");
                    } else {
                        throw new LicenseExpressionException("Invalid expression: " + expr);
                    }

                    if (expr.length()==0) {
                        sb.append(")");
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
            } else if (letterNext(expr)) {
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
                throw new IllegalLicenseExpression("Invalid expression, from: " + expr);
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


    private LicenseExpression doParse2(String expression) throws LicenseExpressionException, IllegalLicenseExpression {
        String expr = expression.replaceAll("\\s", "");
        Log.d(LOG_TAG, "doParse: \"" + expr + "\"");

        // expr begins with "("
        if (expr.charAt(0) == '(') {
            Log.d(LOG_TAG, "   ( found: ");
            Log.d(LOG_TAG, "  expr to doParse with paranthesises: " + expr);
            String subExpr = readParenthisedExpr(expr);
            expr = expr.substring(subExpr.length() + +2);
            Log.d(LOG_TAG, "  ctl expr doParse   : " + subExpr);
            Log.d(LOG_TAG, "  ctl expr remain  : " + expr);
            LicenseExpression le = new LicenseExpression();

            Log.d(LOG_TAG, "  expr remain  : " + expr);

            // Either no operator or end of string (return new expr)
            if (!opNext(expr)) {
                return doParse(subExpr);
            }
            le.addLicense(doParse(subExpr));

            Log.d(LOG_TAG, "  expr remain  : \"" + expr + "\"   --- we should have left" );

            LicenseExpression.Operator op = op(expr);
            Log.d(LOG_TAG, "  ctl expr op  : " + op);
            Log.d(LOG_TAG, " op found: " + op);
            expr = expr.substring(1);

            le.op(op);
            le.addLicense(doParse(expr));
            return le;
        } else if (letterNext(expr)) {
            // Begins with a letter, so LICENSE op ....

            String licenseString = readLicense(expr);
            expr = expr.substring(licenseString.length());
            Log.d(LOG_TAG, " license 1 found: " + licenseString);

            LicenseExpression le = new LicenseExpression();

            // Either operator or end of string (if so, return new expr)
            if (!opNext(expr)) {
                return new LicenseExpression(LicenseStore.getInstance().license(licenseString));
            }

            LicenseExpression.Operator op = op(expr);
            Log.d(LOG_TAG, " op found: " + op);
            expr = expr.substring(1);

            le.op(op);
            le.addLicense(new LicenseExpression(LicenseStore.getInstance().license(licenseString)));

            LicenseExpression.Operator currentOp = op;

            boolean isAnd = (op == LicenseExpression.Operator.AND);
            Log.d(LOG_TAG, " isAnd: " + isAnd);

            LicenseExpression andLe = null;

            while (true) {
                Log.d(LOG_TAG, "  reading rest: " + expr);

                LicenseExpression leToAdd;
                if (letterNext(expr)) {
                    Log.d(LOG_TAG, "  reading rest: license found " + expr);
                    licenseString = readLicense(expr);

                    leToAdd = new LicenseExpression(LicenseStore.getInstance().license(licenseString));
                    // read away license
                    expr = expr.substring(licenseString.length());

                } else {
                    Log.d(LOG_TAG, "  --- implement me :)");
                    leToAdd = doParse(expr);
                }

                // We have an expression to add

                // Last one, simply add it
                if (expr.equals("")) {
                    Log.d(LOG_TAG, " license n found: \"" + licenseString + "\"  no more to read");
                    if (andLe != null) {
                        andLe.addLicense(leToAdd);
                        le.addLicense(andLe);
                        andLe = null;
                    } else {
                        le.addLicense(leToAdd);
                    }
                    return le;
                }

                if (opNext(expr)) {
                    // get next operator
                    op = op(expr);
                    // read away operator
                    expr = expr.substring(1);
                } else {
                    Log.d(LOG_TAG, "   HU AH ---------------------- ");
                }

                if (isAnd) {
                    // Currently in an AND expression
                    if (op == LicenseExpression.Operator.AND) {
                        // Next op also AND
                        Log.d(LOG_TAG, "   isAnd : AND and next AND");
                        le.addLicense(leToAdd);
                    } else {
                        // Next op OR
                        Log.d(LOG_TAG, "   isAnd : AND and next OR <------- PANIC MODE IN DETROIT");
                        Log.d(LOG_TAG, "   isAnd : AND and next OR <------- PANIC MODE IN DETROIT " + le);
                        Log.d(LOG_TAG, "   isAnd : AND and next OR <------- PANIC MODE IN DETROIT " + andLe);
                        Log.d(LOG_TAG, "   isAnd : AND and next OR <------- PANIC MODE IN DETROIT " + leToAdd);
                        if (andLe != null) {
                            le.addLicense(andLe);
                            andLe = null;
                        }
                        LicenseExpression temp = new LicenseExpression();
                        le.addLicense(leToAdd);
                        temp.op(op);
                        temp.addLicense(le);
                        le = temp;
                        isAnd = !isAnd;
                    }
                } else {
                    // Currently in an OR expression
                    Log.d(LOG_TAG, "   isAnd : OR  " + leToAdd);
                    if (op == LicenseExpression.Operator.AND) {
                        // Next op AND
                        Log.d(LOG_TAG, "   isAnd : OR and next AND  <------- DEALING");
                        if (andLe == null) {
                            andLe = new LicenseExpression();
                        }
                        andLe.op(op);
                        andLe.addLicense(leToAdd);
                        Log.d(LOG_TAG, "   isAnd : OR and next AND  <------- DEALING : " + andLe);
                    } else {
                        // Next op also OR
                        if (andLe != null) {
                            Log.d(LOG_TAG, "   isAnd : OR and next OR <--- andLe found: " + andLe);
                        }
                        Log.d(LOG_TAG, "   isAnd : OR and next OR");
                        le.addLicense(leToAdd);
                    }
                }
                op = currentOp;
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
