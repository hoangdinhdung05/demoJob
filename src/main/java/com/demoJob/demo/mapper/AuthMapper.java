package com.demoJob.demo.mapper;

import com.demoJob.demo.dto.response.AuthResponse;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.util.TokenType;
import java.util.stream.Collectors;

public class AuthMapper {

    public static AuthResponse toResponse(String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(TokenType.ACCESS_TOKEN)
                .build();
    }

}
