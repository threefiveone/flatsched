package com.flatsched.controller;

import com.flatsched.exception.SlotException;
import com.flatsched.intf.ReserveViewingService;
import com.flatsched.util.DateTimeParser;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.*;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.time.format.DateTimeParseException;

@Controller("/flat/viewing-slot")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ReserveViewingController {

    private final ReserveViewingService reserveViewingService;

    /**
     * reserve a viewing slot by a new tenant
     * uri example:
     * /flat/viewing-slot/2019-11-12/10-00?tenantId=1
     * where
     *
     * @param date
     * @param time
     */
    @Put("/{date}/{time}")
    public void reserveSlot(String date, String time, @QueryValue("tenantId") Long tenantId) {
        reserveViewingService.reserve(tenantId, DateTimeParser.parseDate(date), DateTimeParser.parseTime(time));
    }

    /**
     * reserve a viewing slot by a new tenant
     * uri example:
     * /flat/viewing-slot/2019-11-12/10-00?tenantId=1
     * where
     *
     * @param date
     * @param time
     */
    @Delete("/{date}/{time}")
    public void cancelSlot(String date, String time, @QueryValue("tenantId") Long tenantId) {
        reserveViewingService.cancel(tenantId, DateTimeParser.parseDate(date), DateTimeParser.parseTime(time));
    }

    @Error
    public HttpResponse<JsonError> error(HttpRequest request, Throwable e) {
        int status = HttpStatus.INTERNAL_SERVER_ERROR.getCode();

        if (SlotException.class.isAssignableFrom(e.getClass())) {
            status = ((SlotException) e).getStatusCode();
        } else if (e.getClass() == DateTimeParseException.class) {
            status = HttpStatus.BAD_REQUEST.getCode();
        }

        JsonError error = new JsonError(e.getMessage());
        error.link(Link.SELF, Link.of(request.getUri()));
        return HttpResponse.serverError(error).status(status);
    }
}