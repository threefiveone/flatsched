package com.flatsched.type;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ProcessedSlot {
    private final SlotStatus status;
    private final long tenantId;
}
