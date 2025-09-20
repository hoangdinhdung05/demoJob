package com.demoJob.demo.exception;

public class NotFoundException extends ApiException {
    public NotFoundException(String message) {
        super(404, "NOT_FOUND", message);
    }
    public NotFoundException(String message, Throwable cause) {
        super(404, "NOT_FOUND", message, cause);
    }
}
