package com.flatsched.service;

import com.flatsched.exception.SlotException;
import com.flatsched.intf.ApproveOrRejectViewingService;
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
public class ApproveOrRejectViewingServiceImpl implements ApproveOrRejectViewingService {
    private final SlotStore slotStore;
    private final TenantNotifier tenantNotifier;
    private final DateTimeValidation dateTimeValidation;

    @Override
    public void approve(LocalDate approveDate, LocalTime approveTime) {
        dateTimeValidation.checkDateTimeIsValidForApprovalOrCancellation(approveDate, approveTime);

        Optional<ProcessedSlot> processedSlot = slotStore.tryToApproveSlot(approveDate, approveTime);
        processedSlot.ifPresentOrElse((slot) -> tenantNotifier.notifyNew(slot, approveDate, approveTime),
                () -> {
            throw new SlotException(HttpStatus.NOT_FOUND.getCode(), "Not possible to approve");
        });
    }

    @Override
    public void reject(LocalDate rejectDate, LocalTime rejectTime) {
        dateTimeValidation.checkDateTimeIsValidForApprovalOrCancellation(rejectDate, rejectTime);

        Optional<ProcessedSlot> processedSlot = slotStore.tryToRejectSlot(rejectDate, rejectTime);
        processedSlot.ifPresentOrElse((slot) -> tenantNotifier.notifyNew(slot, rejectDate, rejectTime),
                () -> {
            throw new SlotException(HttpStatus.NOT_FOUND.getCode(), "Not possible to reject");
        });
    }
}
