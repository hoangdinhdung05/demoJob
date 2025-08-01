package com.demoJob.demo.exception;

public class InvalidOtpException extends ApiException {

    public InvalidOtpException(String message) {
      super(message);
    }

    @Override
    public int getStatusCode() {
      return 400;
    }

    @Override
    public String getErrorCode() {
      return "INVALID_OTP";
    }
}
