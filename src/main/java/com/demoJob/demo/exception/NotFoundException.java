package com.demoJob.demo.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ApiException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.NOT_FOUND.value(); // 404
    }

    @Override
    public String getErrorCode() {
        return "NOT_FOUND";
    }
}