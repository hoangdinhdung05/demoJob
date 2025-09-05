package com.demoJob.demo.service.impl;

import com.demoJob.demo.entity.Token;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.exception.ResourceNotFoundException;
import com.demoJob.demo.repository.TokenRepository;
import com.demoJob.demo.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;

//    @Override
//    public Token createRefreshToken(User user, String token, Date expiryInMs) {
//        Token refreshToken = Token.builder()
//                .user(user)
//                .token(token)
//                .expiryDate(expiryInMs.toInstant())
//                .revoked(false)
//                .build();
//        return refreshTokenRepository.save(refreshToken);
//    }
//
//    @Override
//    public Optional<Token> findByToken(String token) {
//        return refreshTokenRepository.findByToken(token);
//    }
//
//    @Override
//    public boolean isValid(Token refreshToken) {
//        return !refreshToken.isRevoked() && refreshToken.getExpiryDate().isAfter(Instant.now());
//    }
//
//    @Override
//    public void revokeToken(String token) {
//        refreshTokenRepository.findByToken(token).ifPresent(rt -> {
//            rt.setRevoked(true);
//            refreshTokenRepository.save(rt);
//        });
//    }
//
//    @Override
//    public void revokeTokenByUser(User user) {
//        List<Token> tokens = refreshTokenRepository.findAllByUserAndRevokedFalse(user);
//        tokens.forEach(token -> token.setRevoked(true));
//        refreshTokenRepository.saveAll(tokens);
//    }

    /**
     * Lưu thông tin xuống db (username, accessToken, refreshToken)
     * Sử dụng khi login
     */
    @Override
    public long save(Token token) {

        log.info("Save info after login with username={}", token.getUsername());

        Optional<Token> optionalToken = tokenRepository.findByUsername(token.getUsername());
        if (optionalToken.isEmpty()) {
            //Login lần đầu
            tokenRepository.save(token);
            log.info("Save info successfully after login (CREATE)");
            return token.getId();
        } else {
            //Login lại => set accessToken và refreshToken mới
            Token t = optionalToken.get();
            t.setAccessToken(token.getAccessToken());
            t.setRefreshToken(token.getRefreshToken());
            tokenRepository.save(t);
            log.info("Save info successfully after login (UPDATE)");
            return t.getId();
        }
    }

    /**
     * Xóa token theo username
     * Sử dụng khi logout
     *
     */
    @Override
    public void delete(String username) {
        log.info("Delete info in tbl_token after logout server");
        Token token = getByUsername(username);
        tokenRepository.delete(token);
    }

    /**
     * Tìm token theo username
     *
     */
    @Override
    public Token getByUsername(String username) {
        return tokenRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Token not found"));
    }
}
