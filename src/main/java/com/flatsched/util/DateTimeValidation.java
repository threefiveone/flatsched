package com.flatsched.util;

import com.flatsched.exception.SlotBadRequestException;

import javax.inject.Singleton;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.Locale;

@Singleton
public class DateTimeValidation {

    public static final int MIN_HOUR = 10;
    public static final int MAX_HOUR = 19;
    public static final int SLOT_SIZE_MINUTES = 20;

    public static final int RESERVATION_HOURS_GAP = 24;

    private LocalDateTime nowTimeForTest;

    /**
     * Checking date is valid for reservation
     * the date is in upcoming week
     * and time is a valid time slot (hour and minute)
     * and there is 24 hours from now till reserving time
     *
     * @param date
     * @param time
     */
    public void checkDateTimeIsValidForReservation(LocalDate date, LocalTime time) {
        LocalDateTime slotDateTime = LocalDateTime.of(date, time);
        LocalDateTime nextWeekFirstDay = LocalDate.now().plusWeeks(1).with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1).atStartOfDay();
        LocalDateTime nextNextWeekFirstDay = nextWeekFirstDay.plusWeeks(1);

        if (nextWeekFirstDay.isAfter(slotDateTime))
            throw new SlotBadRequestException("Not possible to reserve slot before upcoming week");
        if (nextNextWeekFirstDay.isBefore(slotDateTime) || nextNextWeekFirstDay.isEqual(slotDateTime))
            throw new SlotBadRequestException("Not possible to reserve slot after upcoming week");

        // checking if time variable is a right time slot 20-minutes part
        if (time.getHour() < MIN_HOUR || time.getHour() > MAX_HOUR)
            throw new SlotBadRequestException("Wrong slot hour");

        if (time.getMinute() % SLOT_SIZE_MINUTES != 0) {
            throw new SlotBadRequestException("Wrong slot minute");
        }

        //checking 24 hours to reservation from now

        if (Duration.between(getNowTime(), slotDateTime).toHours() < RESERVATION_HOURS_GAP)
            throw new SlotBadRequestException("Less than 24 hours till slot time");
    }

    /**
     * Checking date is valid for approval, rejecting or cancellation
     * and time is a valid time slot (hour and minute)
     * and time is not in the past
     *
     * @param date
     * @param time
     */
    public void checkDateTimeIsValidForApprovalOrCancellation(LocalDate date, LocalTime time) {
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        if (dateTime.isBefore(getNowTime()))
            throw new SlotBadRequestException("Not possible to change or cancel slot in the past");

        // checking if time variable is a right time slot 20-minutes part
        if (time.getHour() < MIN_HOUR || time.getHour() > MAX_HOUR)
            throw new SlotBadRequestException("Wrong slot hour");

        if (time.getMinute() % SLOT_SIZE_MINUTES != 0) {
            throw new SlotBadRequestException("Wrong slot minute");
        }
    }

    private LocalDateTime getNowTime() {
        return nowTimeForTest != null ? nowTimeForTest : LocalDateTime.now();
    }

    public void setNowTimeForTest(LocalDateTime dateTime) {
        nowTimeForTest = dateTime;
    }

    public void clearNowTimeForTest() {
        nowTimeForTest = null;
    }
}
