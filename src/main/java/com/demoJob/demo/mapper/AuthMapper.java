package com.demoJob.demo.mapper;

import com.demoJob.demo.dto.response.AuthResponse;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.util.TokenType;
import java.util.stream.Collectors;

public class AuthMapper {

    public static AuthResponse toResponse(User user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(TokenType.ACCESS_TOKEN)
                .userId(user.getId())
                .username(user.getUsername())
                .roles(user.getUserHasRoles().stream()
                        .map(role -> role.getRole().getName())
                        .collect(Collectors.toSet()))
                .build();
    }

}
