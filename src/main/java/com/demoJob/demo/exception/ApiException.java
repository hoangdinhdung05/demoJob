package com.demoJob.demo.exception;

import lombok.Getter;
import java.time.LocalDateTime;

public class ApiException extends RuntimeException {
    private final int status;
    @Getter
    private final String errorCode;
    @Getter
    private final LocalDateTime timestamp;

    public ApiException(int status, String errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
    }

    public ApiException(int status, String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatusCode() {
        return status;
    }
}
