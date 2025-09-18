package com.demoJob.demo.mapper;

import com.demoJob.demo.dto.response.AuthResponse;
import com.demoJob.demo.util.enums.TokenType;

public class AuthMapper {

    public static AuthResponse toResponse(String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
