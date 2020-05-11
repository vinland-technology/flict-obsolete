package com.sandklef.compliance.utils;

import com.sandklef.compliance.domain.License;
import com.sandklef.compliance.domain.LicenseObligation;
import com.sandklef.compliance.domain.Obligation;
import com.sandklef.compliance.domain.ObligationState;

import java.util.HashMap;
import java.util.Map;

import static com.sandklef.compliance.domain.Obligation.*;

public class VirtualLicenseBuilder {

    public static int useCounter;


    // One obligationstate is true => TRUE (e g one is copyleft, then TRUE)
    private static ObligationState oneTrue(LicenseObligation first, LicenseObligation second) {
        System.out.println(" oneTrue: " + first + " " + second);

        if (first.state()==ObligationState.TRUE || second.state()==ObligationState.TRUE) {
            return ObligationState.TRUE;
        }
        return ObligationState.FALSE;
    }

    // Both obligationstate are true => TRUE (e g of both sublicensing, then TRUE)
    private static ObligationState bothTrue(LicenseObligation first, LicenseObligation second) {
        if (first.state()==ObligationState.TRUE && second.state()==ObligationState.TRUE) {
            return ObligationState.TRUE;
        }
        return ObligationState.FALSE;
    }

    private static ObligationState calculateState(String obligationName, License first, License second) {
        System.out.println(" calc: " + obligationName);
        LicenseObligation firstLo = first.obligations().get(obligationName);
        LicenseObligation secondLo = second.obligations().get(obligationName);

        switch (obligationName) {
            case MODIFICATION_ALLOWED_NAME:
            case MODIFICATION_COPYLEFTED_NAME:
            case DISTRIBUTION_COPYLEFTED_NAME:
            case DISCLOSE_SOURCE_NAME:
            case STATE_CHANGES_NAME:
            case INCLUDE_COPYRIGHT_NAME:
            case INCLUDE_LICENSE_NAME:
            case INCLUDE_NOTICE_FILE_NAME:
            case INCLUDE_NOTICE_ABOUT_LICENSE_NAME:
            case INCLUDE_INSTALL_INSTRUCTIONS_NAME:
            case LINKING_COPYLEFTED_NAME:
                return oneTrue(firstLo, secondLo);
            case SUBLICENSING_ALLOWED_NAME:
            case DISTRIBUTION_ALLOWED_NAME:
                return bothTrue(firstLo, secondLo);
        }

        System.out.println(" ============================================ undef for: " + obligationName);
        return ObligationState.UNDEFINED;
    }

    private static Map<String, LicenseObligation> calculateStates(License first, License second) {
        return new ObligationBuilder().
                add(LINKING_COPYLEFTED, calculateState(LINKING_COPYLEFTED_NAME, first, second)).
                add(MODIFICATION_ALLOWED,calculateState(MODIFICATION_ALLOWED_NAME, first, second)).
                add(MODIFICATION_COPYLEFTED, calculateState(MODIFICATION_COPYLEFTED_NAME, first, second)).
                add(SUBLICENSING_ALLOWED, calculateState(SUBLICENSING_ALLOWED_NAME, first, second)).
                add(DISTRIBUTION_ALLOWED, calculateState(DISTRIBUTION_ALLOWED_NAME, first, second)).
                add(DISTRIBUTION_COPYLEFTED, calculateState(DISTRIBUTION_COPYLEFTED_NAME, first, second)).
                add(DISCLOSE_SOURCE, calculateState(DISCLOSE_SOURCE_NAME, first, second)).
                add(STATE_CHANGES, calculateState(STATE_CHANGES_NAME, first, second)).
                add(INCLUDE_COPYRIGHT, calculateState(INCLUDE_COPYRIGHT_NAME, first, second)).
                add(INCLUDE_LICENSE, calculateState(INCLUDE_LICENSE_NAME, first, second)).
                add(INCLUDE_INSTALL_INSTRUCTIONS, calculateState(INCLUDE_INSTALL_INSTRUCTIONS_NAME, first, second)).
                add(INCLUDE_NOTICE_FILE, calculateState(INCLUDE_NOTICE_FILE_NAME, first, second)).
                add(INCLUDE_NOTICE_ABOUT_LICENSE, calculateState(INCLUDE_NOTICE_ABOUT_LICENSE_NAME, first, second)).
                build();
    }


    public static License derive(License first, License second) {
        useCounter++;
        Map<String, LicenseObligation> obligations =
                calculateStates(first, second);

        return new License("VirtualLicense_#_" + useCounter,
                calculateStates(first, second));
    }


}
