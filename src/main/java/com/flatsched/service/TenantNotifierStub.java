package com.flatsched.service;

import com.flatsched.intf.TenantNotifier;
import com.flatsched.type.ProcessedSlot;
import com.flatsched.type.SlotStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.time.LocalDate;
import java.time.LocalTime;

@Singleton
public class TenantNotifierStub implements TenantNotifier {
    private static final Logger log = LoggerFactory.getLogger(TenantNotifierStub.class);

    /**
     * Notify current tenant that a new tenant would like to view the flat at a given time
     *
     * @param date
     * @param time
     */
    @Override
    public void notifyCurrent(ProcessedSlot slot, LocalDate date, LocalTime time) {
        if (SlotStatus.WAITING_APPROVAL == slot.getStatus())
            log.info("Slot is waiting approving");
        else if (SlotStatus.CANCELLED == slot.getStatus())
            log.info("Slot is released");
    }

    @Override
    public void notifyNew(ProcessedSlot slot, LocalDate date, LocalTime time) {
        if (SlotStatus.RESERVED == slot.getStatus())
            log.info("Slot reserving is approved");
        else if (SlotStatus.REJECTED == slot.getStatus())
            log.info("Slot reserving is rejected");
    }
}
