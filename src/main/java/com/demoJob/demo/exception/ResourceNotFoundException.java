package com.demoJob.demo.exception;

public class ResourceNotFoundException extends ApiException {
    public ResourceNotFoundException(String message) {
        super(404, "RESOURCE_NOT_FOUND", message);
    }
    public ResourceNotFoundException(String message, Throwable cause) {
        super(404, "RESOURCE_NOT_FOUND", message, cause);
    }
}
