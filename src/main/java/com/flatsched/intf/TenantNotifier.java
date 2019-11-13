package com.flatsched.intf;

import com.flatsched.type.ProcessedSlot;

import java.time.LocalDate;
import java.time.LocalTime;

public interface TenantNotifier {
    void notifyCurrent(ProcessedSlot slot, LocalDate date, LocalTime time);

    void notifyNew(ProcessedSlot slot, LocalDate date, LocalTime time);
}
