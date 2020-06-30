package com.sandklef.compliance.test;

import com.sandklef.compliance.domain.Component;
import com.sandklef.compliance.domain.IllegalLicenseExpression;
import com.sandklef.compliance.domain.LicenseExpression;
import com.sandklef.compliance.domain.LicenseExpressionException;
import com.sandklef.compliance.json.JsonLicenseConnectionsParser;
import com.sandklef.compliance.json.JsonLicenseParser;
import com.sandklef.compliance.utils.LicenseExpressionParser;
import com.sandklef.compliance.utils.LicenseStore;
import com.sandklef.compliance.utils.Log;

import java.io.IOException;

import static com.sandklef.compliance.test.Utils.*;
import static com.sandklef.compliance.test.Utils.assertHelper;

public class TestLicenseParser {

    static {
        try {
            LicenseStore.getInstance().addLicenses(new JsonLicenseParser().readLicenseDir("licenses/json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            LicenseStore.getInstance().connector(new JsonLicenseConnectionsParser().readLicenseConnection("licenses/connections/dwheeler.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println("Licenses:             " + LicenseStore.getInstance().licenses());
    }

    public static void printComponent(Component c) throws LicenseExpressionException, IllegalLicenseExpression {
        String fixed = (new LicenseExpressionParser()).fixLicenseExpression(c.license());
        String list = LicenseExpression.licenseListToString(c.licenseList());
        System.out.println(c);
        System.out.println(" paths:                     " + c.paths() );
        System.out.println(" license:                   " + c.license() );
        System.out.println(" fixed license expression : " + fixed);
        System.out.println(" license expression:        " + c.licenseExpression());
        System.out.println(" license expression list:   " + list );
        System.out.println("\n");
    }

    public static void printFixed(String expr) throws LicenseExpressionException {
        String fixedExpr = (new LicenseExpressionParser()).fixLicenseExpression(expr);
        System.out.println(" to be fixed:  " + expr );
        System.out.println("       fixed: " + fixedExpr.replaceAll("&", " & ").replaceAll("\\|", " | ").replaceAll("\\(", " ( ").replaceAll("\\)", " ) "));
        System.out.println(" fixed");
    }

    public static void test() throws LicenseExpressionException, IllegalLicenseExpression {
        Log.level(Log.DEBUG);

        /*
        printFixed(" (    BSD-3-Clause )   ");

        printFixed(" LGPL-2.1-or-later | BSD-3-Clause & MIT & GPL-2.0-only | LGPL-2.1-only ");
        printFixed(" LGPL-2.1-or-later | BSD-3-Clause & MIT & (GPL-2.0-only) | LGPL-2.1-only ");
        printFixed(" LGPL-2.1-or-later & (GPL-2.0-only | MIT) ");
        printFixed(" LGPL-2.1-or-later & (GPL-2.0-only | MIT) | BSD-3-Clause");
        printFixed(" LGPL-2.1-or-later | GPL-2.0-only | GPL-2.0-only | GPL-2.0-only | GPL-2.0-only | MIT & BSD-3-Clause");
        printFixed(" LGPL-2.1-or-later | GPL-2.0-only | GPL-2.0-only | GPL-2.0-only | GPL-2.0-only | MIT & BSD-3-Clause | GPL-2.0-only | GPL-2.0-only");
        printFixed(" LGPL-2.1-or-later | GPL-2.0-only | MIT & (BSD-3-Clause|MIT) | GPL-2.0-only | GPL-2.0-only");
        printFixed("  BSD-3-Clause|MIT&BSD-3-Clause");
        printFixed("  MIT & (BSD-3-Clause|MIT&BSD-3-Clause)");
        printFixed(" LGPL-2.1-or-later | GPL-2.0-only | MIT & (BSD-3-Clause|MIT&MIT) | GPL-2.0-only | GPL-2.0-only");
//        printFixed(" (   LGPL-2.1-or-later | BSD-3-Clause & MIT & GPL-2.0-only | LGPL-2.1-only ");
        printFixed(" (   LGPL-2.1-or-later | BSD-3-Clause & MIT & GPL-2.0-only | LGPL-2.1-only ");
//        printFixed("BSD-3-Clause");
//        System.exit(1);

//        Log.filterMessage(LicenseExpression.class.getSimpleName());
//        Log.filterTag(LicenseExpressionParser.class.getSimpleName());

*/
//        printFixed("MIT | BSD-3-Clause & GPL-2.0-only & GPL-2.0-only | GPL-2.0-or-later & BSD & MIT | MIT" );
  //      System.exit(1);


      //  LicenseExpressionParser.fixLicenseExpression("BSD-3-Clause");

        Component c;
        /*
        c = new Component("Donkey King", "BSD-3-Clause");
        c.expand();
        printComponent(c);
//        c = new Component("Donkey Kong", "GPL-2.0-only & MIT | BSD-3-Clause");
//        c = new Component("Donkey Kong", "MIT | BSD-3-Clause | GPL-2.0-only");
//        c = new Component("Donkey Kong", "MIT | BSD-3-Clause & GPL-2.0-only & GPL-2.0-or-later | LGPL-2.1-only");
*/

        /*
        c = new Component("Donkey King", "BSD-3-Clause & GPL-2.0-only");
        c.expand();
        printComponent(c);

        c = new Component("Donkey King", "BSD-3-Clause & GPL-2.0-only | MIT");
        c.expand();
        printComponent(c);


        c = new Component("Donkey King", "BSD-3-Clause & GPL-2.0-only | (MIT & BSD-3-Clause)");
        c.expand();
        printComponent(c);

        c = new Component("Donkey Kong", "BSD-3-Clause & GPL-2.0-only & GPL-2.0-only | GPL-2.0-or-later " );
        c.expand();
        printComponent(c);

        c = new Component("Donkey Kong", "MIT | BSD-3-Clause & GPL-2.0-only & GPL-2.0-only | GPL-2.0-or-later " );
        c.expand();
        printComponent(c);
*/
/*
        c = new Component("Donkey Kong", "GPL-2.0-or-later & BSD-3-Clause" );
        c.expand();
        printComponent(c);

        c = new Component("Donkey Kong", "GPL-2.0-or-later & (MIT|BSD-3-Clause)" );
        c.expand();
        printComponent(c);
*/
        c = new Component("Donkey Kong", "GPL-2.0-or-later & (MIT|BSD-3-Clause) & (GPL-2.0-only|Apache-2.0)" );
        c.expand();
        printComponent(c);

        c = new Component("Donkey Kong", "GPL-2.0-or-later & (MIT|BSD-3-Clause) & (GPL-2.0-only&Apache-2.0)" );
        c.expand();
        printComponent(c);


        c = new Component("Donkey Kong", "MIT | BSD-3-Clause & GPL-2.0-only & GPL-2.0-only | GPL-2.0-or-later & BSD-3-Clause & (MIT|BSD-3-Clause) | MIT" );
        c.expand();
        printComponent(c);
        
        System.exit(1);

    }



    public static void main(String[] args) throws LicenseExpressionException, IllegalLicenseExpression {
        test();
    }

}
