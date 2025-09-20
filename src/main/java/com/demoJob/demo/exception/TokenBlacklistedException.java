package com.demoJob.demo.exception;

public class TokenBlacklistedException extends ApiException {
    public TokenBlacklistedException(String message) {
        super(401, "BLACK_LIST_TOKEN", message);
    }
}
