package com.flatsched;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class QueryDateFormatters {
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH-mm");

    public static String formatDateAndTimeInQuery(LocalDate date, LocalTime time, int tenantId) {
        return new StringBuilder(QueryDateFormatters.DATE_FORMAT.format(date))
                .append("/")
                .append(QueryDateFormatters.TIME_FORMAT.format(time))
                .append("/")
                .append("?tenantId=")
                .append(tenantId)
                .toString();
    }

    public static String formatDateAndTimeInQuery(LocalDate date, LocalTime time) {
        return new StringBuilder(QueryDateFormatters.DATE_FORMAT.format(date))
                .append("/")
                .append(QueryDateFormatters.TIME_FORMAT.format(time))
                .toString();
    }

}
