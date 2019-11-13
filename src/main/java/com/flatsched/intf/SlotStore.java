package com.flatsched.intf;

import com.flatsched.type.ProcessedSlot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface SlotStore {
    Optional<ProcessedSlot> tryToReserveSlot(long tenantId, LocalDate date, LocalTime time);

    Optional<ProcessedSlot> tryToCancelSlot(long tenantId, LocalDate date, LocalTime time);

    Optional<ProcessedSlot> tryToApproveSlot(LocalDate date, LocalTime time);

    Optional<ProcessedSlot> tryToRejectSlot(LocalDate date, LocalTime time);

    void removeAllDataForTesting();
}
