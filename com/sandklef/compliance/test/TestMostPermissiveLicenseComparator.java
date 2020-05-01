package com.sandklef.compliance.test;

import com.sandklef.compliance.domain.License;
import com.sandklef.compliance.domain.LicenseViolationException;
import com.sandklef.compliance.json.JsonLicenseParser;
import com.sandklef.compliance.utils.LeastPermissiveLicenseComparator;
import com.sandklef.compliance.utils.LicenseArbiter;
import com.sandklef.compliance.utils.LicenseStore;
import com.sandklef.compliance.utils.MostPermissiveLicenseComparator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.sandklef.compliance.domain.License.*;

public class TestMostPermissiveLicenseComparator {


    public static void main(String[] args) throws IOException {
        LicenseStore.getInstance().addLicenses(new JsonLicenseParser().readLicenseDir("./licenses/json"));
        System.out.println(LicenseStore.getInstance().licenseString());

        License lgpl3 = LicenseStore.getInstance().license(LGPL_3_0_SPDX);
        License lgpl2 = LicenseStore.getInstance().license(LGPL_2_0_SPDX);
        License gpl2 = LicenseStore.getInstance().license(GPL_2_0_SPDX);
        License gpl3 = LicenseStore.getInstance().license(GPL_3_0_SPDX);
        License apache2 = LicenseStore.getInstance().license(APACHE_2_0_SPDX);

        List<License> licenses = new ArrayList<>();
        licenses.add(gpl2);
        licenses.add(lgpl2);
        licenses.add(gpl3);
      //  licenses.add(lgpl3);
        licenses.add(apache2);

        System.out.println("Unsorted");
        System.out.println(licenses);
        licenses.sort(MostPermissiveLicenseComparator.comparator);
        System.out.println("Most perm sorted");
        System.out.println(licenses);
        licenses.sort(LeastPermissiveLicenseComparator.comparator);
        System.out.println("Least perm sorted");
        System.out.println(licenses);



    }


}
