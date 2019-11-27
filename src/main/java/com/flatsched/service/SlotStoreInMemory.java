package com.flatsched.service;

import com.flatsched.intf.SlotStore;
import com.flatsched.type.ProcessedSlot;
import com.flatsched.type.SlotData;
import com.flatsched.type.SlotStatus;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class SlotStoreInMemory implements SlotStore {
    /**
     * the map stores only busy time slots of a day
     * the map key is a week day (date)
     * the map value is a list of busy (approved or waiting to approve or rejected) slots
     */
    private Map<LocalDate, Map<LocalTime, SlotData>> mapDayToSlots;

    @PostConstruct
    void init() {
        mapDayToSlots = new ConcurrentHashMap<>();
    }

    public Optional<ProcessedSlot> tryToReserveSlot(long tenantId, LocalDate date, LocalTime time) {

        var daySlotsMap = mapDayToSlots.computeIfAbsent(date, k -> new ConcurrentHashMap<>());

        var prevSlot = daySlotsMap.putIfAbsent(time, new SlotData(time, SlotStatus.WAITING_APPROVAL, tenantId));

        if (prevSlot != null)
            // put was rejected
            return Optional.empty();
        else {
            // getting new slot
            var slot = daySlotsMap.get(time);
            return Optional.of(new ProcessedSlot(slot.getStatus(), slot.getTenantId()));
        }
    }

    public Optional<ProcessedSlot> tryToCancelSlot(long tenantId, LocalDate date, LocalTime time) {
        var daySlotsMap = mapDayToSlots.get(date);

        if (daySlotsMap != null) {
            var slot = daySlotsMap.computeIfPresent(time, (timeKey, slotData) -> {
                if (slotData.getTenantId() == tenantId && slotData.getStatus() != SlotStatus.REJECTED) {
                    //removing element
                    return null;
                } else
                    return slotData;
            });

            if (slot == null) // slot removed
                return Optional.of(new ProcessedSlot(SlotStatus.CANCELLED, tenantId));
            else
                return Optional.empty();
        } else
            return Optional.empty();
    }

    @Override
    public Optional<ProcessedSlot> tryToApproveSlot(LocalDate date, LocalTime time) {
        return changeSlotStatusFromWaiting(SlotStatus.RESERVED, date, time);
    }

    @Override
    public Optional<ProcessedSlot> tryToRejectSlot(LocalDate date, LocalTime time) {
        return changeSlotStatusFromWaiting(SlotStatus.REJECTED, date, time);
    }

    private Optional<ProcessedSlot> changeSlotStatusFromWaiting(SlotStatus newSlotStatus, LocalDate date, LocalTime time) {
        Map<LocalTime, SlotData> daySlotsMap = mapDayToSlots.getOrDefault(date, Collections.emptyMap());
        SlotData slot = daySlotsMap.get(time);

        if (slot != null) {
            synchronized (slot) {
                if (slot.getStatus() == SlotStatus.WAITING_APPROVAL) {
                    slot.setStatus(newSlotStatus);
                    return Optional.of(new ProcessedSlot(newSlotStatus, slot.getTenantId()));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public void removeAllDataForTesting() {
        mapDayToSlots.clear();
    }
}
