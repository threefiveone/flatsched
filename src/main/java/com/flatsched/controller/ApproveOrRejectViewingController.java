package com.flatsched.controller;

import com.flatsched.intf.ApproveOrRejectViewingService;
import com.flatsched.util.DateTimeParser;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Put;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@Controller("/flat/viewing-slot")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ApproveOrRejectViewingController {
    private final ApproveOrRejectViewingService approveOrRejectViewingService;

    /**
     * approve a viewing slot by the current tenant
     * uri example:
     * /flat/viewing-slot/2019-11-12/10-00
     * where
     *
     * @param date
     * @param time
     */
    @Put("/{date}/{time}/approve")
    public void approveSlot(String date, String time) {
        approveOrRejectViewingService.approve(DateTimeParser.parseDate(date), DateTimeParser.parseTime(time));
    }

    /**
     * approve a viewing slot by the current tenant
     * uri example:
     * /flat/viewing-slot/2019-11-12/10-00
     * where
     *
     * @param date
     * @param time
     */
    @Put("/{date}/{time}/reject")
    public void rejectSlot(String date, String time) {
        approveOrRejectViewingService.reject(DateTimeParser.parseDate(date), DateTimeParser.parseTime(time));
    }
}
