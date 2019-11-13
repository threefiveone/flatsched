package com.flatsched;

import com.flatsched.intf.SlotStore;
import com.flatsched.util.DateTimeValidation;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
public class ReserveViewingControllerTest extends BaseControllerTest {

    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    @Inject
    DateTimeValidation dateTimeValidation;

    @Inject
    EmbeddedServer server;
    @Inject
    SlotStore slotStore;

    @BeforeEach
    void setup() {
        slotStore.removeAllDataForTesting();
        dateTimeValidation.clearNowTimeForTest();
    }

    @ParameterizedTest
    @MethodSource("getCorrectTimes")
    void reserveEmptySlotUpcomingWeekCorrectTime(String time) {
        assertEquals(HttpStatus.OK,
                reserveUpcomingWeekSlot(2, time));
    }

    static Stream<String> getCorrectTimes() {
        LocalTime startTime = LocalTime.of(DateTimeValidation.MIN_HOUR, 0);
        LocalTime endTime = LocalTime.of(DateTimeValidation.MAX_HOUR + 1, 0);
        return Stream
                .iterate(startTime,
                        endTime::isAfter,
                        t -> t.plusMinutes(DateTimeValidation.SLOT_SIZE_MINUTES))
                .map(TIME_FORMAT::format);
    }

    @Test
    void tryToReserveBusySlotByOtherTenant() {
        String time = "10:00";

        // reserve empty slot
        assertEquals(HttpStatus.OK,
                reserveUpcomingWeekSlot(2, time, 1));

        // reserve busy slot by second tenant
        HttpClientResponseException ex = assertThrows(HttpClientResponseException.class,
                () -> reserveUpcomingWeekSlot(2, time, 2));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }

    @ParameterizedTest
    @ValueSource(strings = {"09:00", "21:00"})
    void tryToReserveSlotHourOutside(String time) {
        // reserve slot in the past
        HttpClientResponseException ex = assertThrows(HttpClientResponseException.class,
                () -> reserveUpcomingWeekSlot(2, time));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @ParameterizedTest
    @ValueSource(strings = {"09:59", "10:01", "18:19", "19:05"})
    void tryToReserveWrongTimeSlot(String time) {
        // reserve slot in the past
        HttpClientResponseException ex = assertThrows(HttpClientResponseException.class,
                () -> reserveUpcomingWeekSlot(2, time));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void reserveAndCancelSlotOneTenant() {
        String time = "10:00";
        // reserve empty slot
        assertEquals(HttpStatus.OK,
                reserveUpcomingWeekSlot(2, time));

        // cancel its slot
        assertEquals(HttpStatus.OK,
                cancelUpcomingWeekSlot(2, time));
    }

    @Test
    void oneTenantReserveSlotAndSecondTryToCancel() {
        String time = "10:00";
        // reserve empty slot
        assertEquals(HttpStatus.OK,
                reserveUpcomingWeekSlot(2, time, 1));

        // try to cancel its slot
        HttpClientResponseException ex = assertThrows(HttpClientResponseException.class,
                () -> cancelUpcomingWeekSlot(2, time, 2));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }

    @Test
    void tryToReserveSlotLessThan24hours() {
        LocalDateTime nextWeekStartSlotTime = getNextWeekDay(1)
                .atStartOfDay()
                .withHour(DateTimeValidation.MIN_HOUR) //beginning slot time
                .withMinute(0);

        // setting 23 hours to first slot of upcoming week
        dateTimeValidation.setNowTimeForTest(nextWeekStartSlotTime.minusHours(23));

        // try to reserve first slot in upcoming week
        HttpClientResponseException ex = assertThrows(HttpClientResponseException.class,
                () -> cancelUpcomingWeekSlot(1, "10:00"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }

}
