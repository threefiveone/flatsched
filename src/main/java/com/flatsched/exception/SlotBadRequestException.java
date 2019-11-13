package com.flatsched.exception;

import io.micronaut.http.HttpStatus;

public class SlotBadRequestException extends SlotException {
    public SlotBadRequestException(String message, Throwable e) {
        super(HttpStatus.BAD_REQUEST.getCode(), message, e);
    }

    public SlotBadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST.getCode(), message);
    }
}
