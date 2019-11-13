package com.flatsched.service;

import com.flatsched.exception.SlotException;
import com.flatsched.intf.ReserveViewingService;
import com.flatsched.intf.SlotStore;
import com.flatsched.intf.TenantNotifier;
import com.flatsched.type.ProcessedSlot;
import com.flatsched.util.DateTimeValidation;
import io.micronaut.http.HttpStatus;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ReserveViewingServiceImpl implements ReserveViewingService {
    private final SlotStore slotStore;
    private final TenantNotifier tenantNotifier;
    private final DateTimeValidation dateTimeValidation;

    @Override
    public void reserve(long tenantId, LocalDate reserveDate, LocalTime reserveTime) {
        dateTimeValidation.checkDateTimeIsValidForReservation(reserveDate, reserveTime);

        Optional<ProcessedSlot> slot = slotStore.tryToReserveSlot(tenantId, reserveDate, reserveTime);
        if (slot.isPresent()) {
            tenantNotifier.notifyCurrent(slot.get(), reserveDate, reserveTime);
        } else
            throw new SlotException(HttpStatus.FORBIDDEN.getCode(), "Slot is busy");
    }

    @Override
    public void cancel(long tenantId, LocalDate reserveDate, LocalTime reserveTime) {
        dateTimeValidation.checkDateTimeIsValidForApprovalOrCancellation(reserveDate, reserveTime);

        Optional<ProcessedSlot> slot = slotStore.tryToCancelSlot(tenantId, reserveDate, reserveTime);
        if (slot.isPresent()) {
            tenantNotifier.notifyCurrent(slot.get(), reserveDate, reserveTime);
        } else
            throw new SlotException(HttpStatus.FORBIDDEN.getCode(), "Not possible to cancel the slot");
    }

}
