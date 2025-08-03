package com.demoJob.demo.exception;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends ApiException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 400; // Bad Request
    }

    @Override
    public String getErrorCode() {
        return "DUPLICATE_RESOURCE";
    }
}
