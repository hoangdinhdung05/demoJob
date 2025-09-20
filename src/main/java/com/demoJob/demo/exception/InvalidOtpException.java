package com.demoJob.demo.exception;

public class InvalidOtpException extends ApiException {
    public InvalidOtpException(String message) {
        super(400, "INVALID_OTP", message);
    }
}
