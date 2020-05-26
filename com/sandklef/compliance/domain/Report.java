// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.domain;

import com.sandklef.compliance.utils.LicenseArbiter;

import java.util.ArrayList;
import java.util.List;

public class Report {

    private Component component;
    private List<ComplianceAnswer> answers;
    private LicensePolicy policy;
    private MetaData metaData;

    public Report(Component component, LicensePolicy policy) {
        this.component = component;
        this.policy = policy;
        this.answers = new ArrayList<>();
        this.metaData = new MetaData();
    }

    public Component component() {
        return component;
    }

    public List<ComplianceAnswer> answers() {
        return answers;
    }

    public LicensePolicy policy() {
        return policy;
    }

    public MetaData metaData() {
        return metaData;
    }

    public void addAnswer(ComplianceAnswer answer) {
        answers.add(answer);
    }

    public void finished() {
        metaData.finished();
    }

    public String duration() {
        return metaData.duration();
    }


}