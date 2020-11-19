package com.sandklef.compliance.arbiter;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

public class LicenseArbiterFactory {

    private static LicenseArbiter arbiter;

    public static enum LICENSE_ARBITER_MODE {
        LICENSE_ARBITER_MODE_UNSET,
        LICENSE_ARBITER_MODE_GRAPH,
        LICENSE_ARBITER_MODE_MATRIX
    } ;

    public static LicenseArbiter defaultArbiter() {
        if (arbiter==null) {
            arbiter = new GraphLicenseArbiter();
        }
        return arbiter;
    }

    public static LicenseArbiter arbiter(LICENSE_ARBITER_MODE mode) {
        switch (mode) {
            case LICENSE_ARBITER_MODE_GRAPH:
                System.out.println( " GRAPH ARBITER");
                arbiter = new GraphLicenseArbiter();
                break;
            case LICENSE_ARBITER_MODE_MATRIX:
                System.out.println( " MATRIX ARBITER");
                arbiter = new MatrixLicenseArbiter();
                break;
            default:
                System.out.println( " NULL ARBITER");
                arbiter = null;
        }
        return arbiter;
    }
}
