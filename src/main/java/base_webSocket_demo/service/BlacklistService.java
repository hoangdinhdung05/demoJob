package base_webSocket_demo.service;

import base_webSocket_demo.util.TokenBlacklistReason;
import java.time.Instant;

public interface BlacklistService {
    void blacklistToken(String token, Instant expiryDate, TokenBlacklistReason reason);

    boolean isBlacklisted(String token);
}
