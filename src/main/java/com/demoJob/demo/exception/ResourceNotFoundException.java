package com.demoJob.demo.exception;

public class ResourceNotFoundException extends RuntimeException {
    //custom exception for resource not found
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
