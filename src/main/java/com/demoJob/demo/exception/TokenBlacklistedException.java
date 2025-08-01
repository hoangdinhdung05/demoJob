package com.demoJob.demo.exception;

public class TokenBlacklistedException extends ApiException {
    public TokenBlacklistedException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 401;
    }

    @Override
    public String getErrorCode() {
        return "BLACK_LIST_TOKEN";
    }


}
