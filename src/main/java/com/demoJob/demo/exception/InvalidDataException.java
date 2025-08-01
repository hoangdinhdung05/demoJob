package com.demoJob.demo.exception;

import org.springframework.http.HttpStatus;

public class InvalidDataException extends ApiException {

    public InvalidDataException(String message) {
        super(message);
    }

    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.CONFLICT.value(); // 409
    }

    @Override
    public String getErrorCode() {
        return "INVALID_DATA";
    }
}
