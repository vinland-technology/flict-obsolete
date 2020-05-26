package com.sandklef.compliance.domain;

import com.sandklef.compliance.utils.Version;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.*;

public class MetaData {

    private String producer;
    private String version;
    private LocalDateTime start;
    private LocalDateTime stop;

    public MetaData() {
        producer = Version.POLICY_CHECKER_NAME;
        version = Version.POLICY_CHECKER_VERSION;
        start = LocalDateTime.now();
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

    public String duration() {
        StringBuffer sb = new StringBuffer();
        LocalDateTime tmpTime = LocalDateTime.from(start);

        long years = tmpTime.until( stop, YEARS );
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
        possiblyAdd(seconds, "second", sb);

        return sb.toString();
    }


}
