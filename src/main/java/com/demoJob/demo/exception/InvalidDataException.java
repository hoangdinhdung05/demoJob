package com.demoJob.demo.exception;

public class InvalidDataException extends ApiException {
    public InvalidDataException(String message) {
        super(409, "INVALID_DATA", message);
    }

    public InvalidDataException(String message, Throwable cause) {
        super(409, "INVALID_DATA", message, cause);
    }
}
