package com.flatsched.intf;

import java.time.LocalDate;
import java.time.LocalTime;

public interface ReserveViewingService {
    // "/flat/viewing-slot/2019-11-12/10-00?tenantId=1"

    void reserve(long tenantId, LocalDate reserveDate, LocalTime reserveTime);

    void cancel(long tenantId, LocalDate cancelDate, LocalTime cancelTime);
}
