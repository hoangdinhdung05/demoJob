package com.demoJob.demo.service.impl;

import com.demoJob.demo.entity.User;
import com.demoJob.demo.entity.VerificationToken;
import com.demoJob.demo.repository.VerificationTokenRepository;
import com.demoJob.demo.service.VerificationTokenService;
import com.demoJob.demo.util.TokenTypeVerify;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;

    @Override
    public VerificationToken createToken(User user, TokenTypeVerify type, int minutesValid) {
        String tokenStr = UUID.randomUUID().toString();

        log.info("[{}] Create OTT for user={} -> token={}", type, user.getEmail(), tokenStr);

        VerificationToken token = VerificationToken.builder()
                .user(user)
                .type(type)
                .token(tokenStr)
                .expiryDate(LocalDateTime.now().plusMinutes(minutesValid))
                .used(false)
                .build();

        return verificationTokenRepository.save(token);
    }

    @Override
    public VerificationToken validateToken(String tokenStr, TokenTypeVerify expectedType) {
        VerificationToken token = verificationTokenRepository.findByToken(tokenStr)
                .orElseThrow(() -> new RuntimeException("Token không hợp lệ"));
        if (!token.getType().equals(expectedType)) {
            throw new RuntimeException("Sai dạng token");
        }
        if (token.isExpired()) throw new RuntimeException("Token đã hết hạn");
        if (token.isUsed()) throw new RuntimeException("Token đã được sử dụng");

        token.setUsed(true);
        verificationTokenRepository.save(token);
        return  token;
    }
}
