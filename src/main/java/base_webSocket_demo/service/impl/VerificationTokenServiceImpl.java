package base_webSocket_demo.service.impl;

import base_webSocket_demo.entity.User;
import base_webSocket_demo.entity.VerificationToken;
import base_webSocket_demo.repository.VerificationTokenRepository;
import base_webSocket_demo.service.VerificationTokenService;
import base_webSocket_demo.util.TokenTypeVerify;
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
