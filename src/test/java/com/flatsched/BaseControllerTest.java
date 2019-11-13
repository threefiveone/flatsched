package com.flatsched;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class BaseControllerTest {
    public static final String BASE_URI = "/flat/viewing-slot/";

    public static final String APPROVE_COMMAND = "approve";
    public static final String REJECT_COMMAND = "reject";

    @Inject
    @Client("/")
    HttpClient client;

    /**
     * helper function for test
     * using upcoming week slots, to avoid calculation today's slots
     *
     * @param timeStr
     * @return
     */
    HttpStatus reserveUpcomingWeekSlot(int dayOfWeek, String timeStr, int tenantId) {
        return reserveSlot(getNextWeekDay(dayOfWeek), timeStr);
    }

    HttpStatus reserveUpcomingWeekSlot(int dayOfWeek, String timeStr) {
        return reserveUpcomingWeekSlot(dayOfWeek, timeStr, 1);
    }

    HttpStatus cancelUpcomingWeekSlot(int dayOfWeek, String timeStr, int tenantId) {
        return cancelSlot(getNextWeekDay(dayOfWeek), timeStr, tenantId);
    }

    HttpStatus cancelUpcomingWeekSlot(int dayOfWeek, String timeStr) {
        return cancelUpcomingWeekSlot(dayOfWeek, timeStr, 1);
    }

    LocalDate getNextWeekDay(int dayOfWeek) {
        return LocalDate.now().plusWeeks(1).with(WeekFields.of(Locale.getDefault()).dayOfWeek(), dayOfWeek);
    }

    /**
     * helper function for reserve
     *
     * @param timeStr
     * @return
     */
    HttpStatus reserveSlot(LocalDate date, String timeStr, int tenantId) {
        return client.toBlocking().exchange(
                HttpRequest.PUT(
                        constructReserveCancelUri(date, timeStr, tenantId),
                        "")).getStatus();
    }

    HttpStatus reserveSlot(LocalDate date, String timeStr) {
        return reserveSlot(date, timeStr, 1);
    }

    /**
     * helper function for cancel
     *
     * @param timeStr
     * @return
     */
    HttpStatus cancelSlot(LocalDate date, String timeStr) {
        return cancelSlot(date, timeStr, 1);
    }

    /**
     * helper function for cancel
     *
     * @param timeStr
     * @return
     */
    HttpStatus cancelSlot(LocalDate date, String timeStr, int tenantId) {
        return client.toBlocking().exchange(
                        HttpRequest.DELETE(
                                constructReserveCancelUri(date, timeStr, tenantId),
                                "")).getStatus();
    }

    String constructReserveCancelUri(LocalDate date, String timeStr, int tenantId) {
        LocalTime time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
        return new StringBuilder(BASE_URI)
                .append(QueryDateFormatters.formatDateAndTimeInQuery(date, time, tenantId))
                .toString();
    }

    String constructApproveRejectUri(LocalDate date, String timeStr, String command) {
        LocalTime time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
        return new StringBuilder(BASE_URI)
                .append(QueryDateFormatters.formatDateAndTimeInQuery(date, time))
                .append("/")
                .append(command)
                .toString();
    }

    HttpStatus approveUpcomingWeekSlot(int dayOfWeek, String timeStr) {
        return approveSlot(getNextWeekDay(dayOfWeek), timeStr);
    }

    /**
     * helper function for approve
     *
     * @param timeStr
     * @return
     */
    HttpStatus approveSlot(LocalDate date, String timeStr) {
        return client.toBlocking().exchange(
                HttpRequest.PUT(
                        constructApproveRejectUri(date, timeStr, APPROVE_COMMAND),
                        "")).getStatus();
    }

    HttpStatus rejectUpcomingWeekSlot(int dayOfWeek, String timeStr) {
        return rejectSlot(getNextWeekDay(dayOfWeek), timeStr);
    }

    /**
     * helper function for reject
     *
     * @param timeStr
     * @return
     */
    HttpStatus rejectSlot(LocalDate date, String timeStr) {
        return client.toBlocking().exchange(
                HttpRequest.PUT(
                        constructApproveRejectUri(date, timeStr, REJECT_COMMAND),
                        "")).getStatus();
    }
}
