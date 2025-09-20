package com.demoJob.demo.exception;

public class InvalidTokenException extends ApiException {
    public InvalidTokenException(String message) {
        super(400, "INVALID_TOKEN", message);
    }
}
