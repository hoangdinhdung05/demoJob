package com.demoJob.demo.service.impl;

import com.demoJob.demo.entity.BlacklistedToken;
import com.demoJob.demo.repository.BlacklistedTokenRepository;
import com.demoJob.demo.service.BlacklistService;
import com.demoJob.demo.util.TokenBlacklistReason;
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
