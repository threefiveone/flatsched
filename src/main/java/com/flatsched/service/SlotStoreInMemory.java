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
import java.util.function.Function;

@Singleton
public class SlotStoreInMemory implements SlotStore {
    /**
     * the map stores only busy time slots of a day
     * the map key is a week day (date)
     * the map value is a list of busy (approved or waiting to approve) slots
     */
    private Map<LocalDate, Map<LocalTime, SlotData>> map;

    @PostConstruct
    void init() {
        map = new ConcurrentHashMap<>();
    }

    public Optional<ProcessedSlot> tryToReserveSlot(long tenantId, LocalDate date, LocalTime time) {
        Map<LocalTime, SlotData> daySlotsMap = map.computeIfAbsent(date, k -> new ConcurrentHashMap<>());
        synchronized (daySlotsMap) {
            if (!daySlotsMap.containsKey(time)) {
                SlotData slot = new SlotData(time, SlotStatus.WAITING_APPROVAL, tenantId);
                daySlotsMap.put(time, slot);
                return Optional.of(new ProcessedSlot(slot.getStatus(), slot.getTenantId()));
            } else
                return Optional.empty();
        }
    }

    public Optional<ProcessedSlot> tryToCancelSlot(long tenantId, LocalDate date, LocalTime time) {
        Map<LocalTime, SlotData> daySlotsMap = map.computeIfAbsent(date, k -> new ConcurrentHashMap<>());
        SlotData slot = daySlotsMap.get(time);
        if (slot != null) {
            if (slot.getTenantId() == tenantId && slot.getStatus() != SlotStatus.REJECTED) {
                daySlotsMap.remove(time);
                return Optional.of(new ProcessedSlot(SlotStatus.CANCELLED, slot.getTenantId()));
            }
        }
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
        return findAndChangeSlot(date, time, (mayBeSlot) -> {
            if (mayBeSlot.isPresent()) {
                SlotData slot = mayBeSlot.get();
                if (slot.getStatus() == SlotStatus.WAITING_APPROVAL) {
                    slot.setStatus(newSlotStatus);
                    return new ProcessedSlot(newSlotStatus, slot.getTenantId());
                }
            }

            return null;
        });
    }


    private Optional<ProcessedSlot> findAndChangeSlot(LocalDate date, LocalTime time,
                                                      Function<Optional<SlotData>, ProcessedSlot> slotAction) {
        Map<LocalTime, SlotData> daySlotsMap = map.getOrDefault(date, Collections.emptyMap());
        SlotData slot = daySlotsMap.get(time);
        return Optional.ofNullable(
                slotAction.apply(
                        Optional.ofNullable(slot)));
    }

    @Override
    public void removeAllDataForTesting() {
        map.clear();
    }
}
