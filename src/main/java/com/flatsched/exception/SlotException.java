package com.flatsched.exception;

import lombok.Getter;

@Getter
public class SlotException extends RuntimeException {
    private final int statusCode;

    public SlotException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public SlotException(int statusCode, String message, Throwable e) {
        super(message, e);
        this.statusCode = statusCode;
    }
}
