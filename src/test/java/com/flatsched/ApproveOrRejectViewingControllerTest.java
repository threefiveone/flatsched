package com.flatsched;

import com.flatsched.intf.SlotStore;
import com.flatsched.util.DateTimeValidation;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
public class ApproveOrRejectViewingControllerTest extends BaseControllerTest {

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

    @Test
    void reserveAndApproveSlot() {
        String time = "10:00";
        assertEquals(HttpStatus.OK,
                reserveUpcomingWeekSlot(2, time));

        assertEquals(HttpStatus.OK,
                approveUpcomingWeekSlot(2, time));
    }

    @Test
    void reserveAndRejectAndTryToAgainReserveSlot() {
        String time = "10:20";

        assertEquals(HttpStatus.OK,
                reserveUpcomingWeekSlot(2, time));

        assertEquals(HttpStatus.OK,
                rejectUpcomingWeekSlot(2, time));


        HttpClientResponseException ex = assertThrows(HttpClientResponseException.class,
                () -> reserveUpcomingWeekSlot(2, time));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }

    @Test
    void reserveAndApproveAndCancelSlot() {
        String time = "10:40";
        assertEquals(HttpStatus.OK,
                reserveUpcomingWeekSlot(2, time));

        assertEquals(HttpStatus.OK,
                approveUpcomingWeekSlot(2, time));

        assertEquals(HttpStatus.OK,
                cancelUpcomingWeekSlot(2, time));
    }

    @Test
    void reserveAndApproveAndCancelAndReserveAgainSlot() {
        String time = "11:00";
        assertEquals(HttpStatus.OK,
                reserveUpcomingWeekSlot(2, time));

        assertEquals(HttpStatus.OK,
                approveUpcomingWeekSlot(2, time));

        assertEquals(HttpStatus.OK,
                cancelUpcomingWeekSlot(2, time));

        assertEquals(HttpStatus.OK,
                reserveUpcomingWeekSlot(2, time));
    }
}
