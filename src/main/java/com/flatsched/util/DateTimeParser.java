package com.flatsched.util;

import com.flatsched.exception.SlotException;
import io.micronaut.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeParser {
    public static final DateTimeFormatter TIME_URI_FORMAT = DateTimeFormatter.ofPattern("HH-mm");

    public static LocalDate parseDate(String dateString) {
        try {
            return LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ex) {
            throw new SlotException(HttpStatus.BAD_REQUEST.getCode(), "Bad request format", ex);
        }
    }

    public static LocalTime parseTime(String timeString) {
        try {
            return LocalTime.parse(timeString, TIME_URI_FORMAT);
        } catch (DateTimeParseException ex) {
            throw new SlotException(HttpStatus.BAD_REQUEST.getCode(), "Bad request format", ex);
        }
    }
}
