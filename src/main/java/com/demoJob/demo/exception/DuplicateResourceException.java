package com.demoJob.demo.exception;

public class DuplicateResourceException extends ApiException {
    public DuplicateResourceException(String message) {
        super(400, "DUPLICATE_RESOURCE", message);
    }
}
