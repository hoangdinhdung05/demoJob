package com.demoJob.demo.service;

import com.demoJob.demo.entity.Token;
import com.demoJob.demo.entity.User;

import java.util.Date;
import java.util.Optional;

public interface TokenService {

    Token createRefreshToken(User user, String token, Date expiryInMs);

    Optional<Token> findByToken(String token);

    boolean isValid(Token refreshToken);

    void revokeToken(String token);

    void revokeTokenByUser(User user);

    /**
     * Lưu thông tin xuống db (username, accessToken, refreshToken)
     * Sử dụng khi login
     */
    long save(Token token);

    /**
     * Xóa token theo username
     * Sử dụng khi logout
     */
    void delete(String username);

    /**
     * Tìm token theo username
     */
    Token getByUsername(String username);
}
