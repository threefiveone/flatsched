package com.flatsched.type;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
public class SlotData {
    private LocalTime time;
    private SlotStatus status;
    private long tenantId;
}
