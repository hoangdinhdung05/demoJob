package com.demoJob.demo.service;

import com.demoJob.demo.entity.Token;

public interface TokenService {
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

    /**
     * Check exists
     * @param accessToken Header
     * @return true/false
     */
    boolean existsByAccessToken(String accessToken);

    /**
     * Check exists
     * @param refreshToken Header
     * @return true/false
     */
    boolean existsByRefreshToken(String refreshToken);
}
