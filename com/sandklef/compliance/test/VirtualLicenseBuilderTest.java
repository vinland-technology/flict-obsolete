package com.sandklef.compliance.test;

import com.sandklef.compliance.domain.License;
import com.sandklef.compliance.domain.ObligationState;
import com.sandklef.compliance.utils.VirtualLicenseBuilder;

import static com.sandklef.compliance.domain.Obligation.SUBLICENSING_ALLOWED_NAME;
import static com.sandklef.compliance.test.Utils.*;

public class VirtualLicenseBuilderTest {
/*
    public static void test() {
        License derived = VirtualLicenseBuilder.derive(gpl20, apache20);

        assertHelper("gplv2 is copylefted", gpl20.isCopyleft());
        assertHelper("apache2 is not copylefted", !apache20.isCopyleft());
        assertHelper("dervied is not copylefted", derived.isCopyleft());

        assertHelper("gplv2 NOT sublicense", gpl20.obligations().get(SUBLICENSING_ALLOWED_NAME).state()== ObligationState.FALSE);
        assertHelper("apache2 sublicense", apache20.obligations().get(SUBLICENSING_ALLOWED_NAME).state()== ObligationState.TRUE);
        assertHelper("dervied NOT sublicense", derived.obligations().get(SUBLICENSING_ALLOWED_NAME).state()== ObligationState.FALSE);

        System.out.println(gpl20.toStringLong());
        System.out.println(apache20.toStringLong());
        System.out.println(derived.toStringLong()    );
    }
*/
    public static void main(String[] args) {

  //      test();

    }

}
