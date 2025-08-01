package com.demoJob.demo.service;

import com.demoJob.demo.util.TokenBlacklistReason;
import java.time.Instant;

public interface BlacklistService {
    void blacklistToken(String token, Instant expiryDate, TokenBlacklistReason reason);

    boolean isBlacklisted(String token);
}
