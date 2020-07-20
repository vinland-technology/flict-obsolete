// SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package com.sandklef.compliance.domain;

import com.sandklef.compliance.utils.Version;
import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.*;

public class MetaData {

    private final String producer;
    private final String version;
    private final LocalDateTime start;
    private LocalDateTime stop;

    public MetaData() {
        producer = Version.LICENSE_CHECKER_NAME;
        version = Version.LICENSE_CHECKER_VERSION;
        start = LocalDateTime.now();
    }

    public MetaData(String producer,String version) {
        this.producer = producer;
        this.version = version;
        start = null;
    }

    public void finished() {
        stop = LocalDateTime.now();
    }

    private void possiblyAdd(long amount, String unit, StringBuffer sb) {
        if (amount>0 || sb.length() > 0 ) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(amount + " " + unit + (amount == 1 ? "" : "s"));
        }
    }

    public String producer() {
        return producer;
    }

    public String version() {
        return version;
    }

    public LocalDateTime start() {
        return start;
    }

    public LocalDateTime stop() {
        return stop;
    }

    public String duration() {
        StringBuffer sb = new StringBuffer();
        LocalDateTime tmpTime = LocalDateTime.from(start);

        if (stop==null) {
            finished();
        }

        long years = tmpTime.until( stop, YEARS );
        //System.out.println(" tmpTime: " + years);
        tmpTime = tmpTime.plusYears( years );
        possiblyAdd(years, "year", sb);

        long months = tmpTime.until( stop, MONTHS );
        tmpTime = tmpTime.plusMonths( months );
        possiblyAdd(months, "month", sb);

        long days = tmpTime.until( stop, DAYS );
        tmpTime = tmpTime.plusDays( days );
        possiblyAdd(days, "day", sb);

        long hours = tmpTime.until( stop, HOURS );
        tmpTime = tmpTime.plusHours( hours );
        possiblyAdd(hours, "hour", sb);

        long minutes = tmpTime.until( stop, MINUTES );
        tmpTime = tmpTime.plusMinutes( minutes );
        possiblyAdd(minutes, "minute", sb);

        long seconds = tmpTime.until( stop, SECONDS );
        sb.append( seconds);
        sb.append(" seconds");

        return sb.toString();
    }

    @Override
    public String toString() {
        if (start != null) {
            return "[ " +
                    " producer='" + producer + "'" +
                    " version='" + version + "'" +
                    " duration: " + duration() +
                    ']';
        } else {
            return "[ " +
                    " producer='" + producer + "'" +
                    " version='" + version + "' ]" ;
        }
    }

}
