package com.sandklef.compliance.exporter;

import com.google.gson.Gson;
import com.sandklef.compliance.domain.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PileComplianceJsonExporter implements PileComplianceReportExporter {
    @Override
    public String exportReport(PileComplianceReport report, OutputRange range) throws IllegalLicenseExpression, LicenseExpressionException {
        return shortReport(report);
    }

    private String shortReport(PileComplianceReport pileComplianceStatus)
            throws IllegalLicenseExpression, LicenseExpressionException {

        Map<String, Map> reportMap = new HashMap<>();

        /*
         * Outbound
         */
        Map<String, List<List<License>>> outBound = new HashMap<>();
        outBound.put("allowed", pileComplianceStatus.compliantAllowed());
        outBound.put("avoid", pileComplianceStatus.compliantAvoid());

        /*
         * Meta
         */
        Map<String, String> metaData = new HashMap<>();
        metaData.put("producer", pileComplianceStatus.metaData().producer() + " (" + pileComplianceStatus.metaData().version());
        metaData.put("duration", pileComplianceStatus.metaData().duration() );
        metaData.put("component",pileComplianceStatus.component().toString());

        /*
         * Policy
         */
        Map <String, List<License>>policyMap = new HashMap<>();
        reportMap.put("policy", policyMap);
        policyMap.put("allowed",pileComplianceStatus.policy().allowedList() );
        policyMap.put("avoid",pileComplianceStatus.policy().avoidList() );
        policyMap.put("denied",pileComplianceStatus.policy().deniedList() );

        reportMap.put("outbound", outBound);
        reportMap.put("meta", metaData);
        reportMap.put("policy", policyMap);

        return (new Gson().toJson(reportMap));
    }


}
