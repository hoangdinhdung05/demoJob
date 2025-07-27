package base_webSocket_demo.service.impl;

import base_webSocket_demo.entity.BlacklistedToken;
import base_webSocket_demo.repository.BlacklistedTokenRepository;
import base_webSocket_demo.service.BlacklistService;
import base_webSocket_demo.util.TokenBlacklistReason;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class BlacklistServiceImpl implements BlacklistService {
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    @Override
    public void blacklistToken(String token, Instant expiryDate, TokenBlacklistReason reason) {
        if (!blacklistedTokenRepository.existsByToken(token)) {
            BlacklistedToken blacklisted = BlacklistedToken.builder()
                    .token(token)
                    .expiryDate(expiryDate)
                    .reason(reason)
                    .build();
            blacklistedTokenRepository.save(blacklisted);
        }
    }

    @Override
    public boolean isBlacklisted(String token) {
        return blacklistedTokenRepository.existsByToken(token);
    }
}
