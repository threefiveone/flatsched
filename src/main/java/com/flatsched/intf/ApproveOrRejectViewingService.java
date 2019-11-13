package com.flatsched.intf;

import java.time.LocalDate;
import java.time.LocalTime;

public interface ApproveOrRejectViewingService {
    void approve(LocalDate approveDate, LocalTime approveTime);

    void reject(LocalDate rejectDate, LocalTime rejectTime);
}
