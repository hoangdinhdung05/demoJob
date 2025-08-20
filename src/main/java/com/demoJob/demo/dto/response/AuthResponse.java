package com.demoJob.demo.dto.response;

import com.demoJob.demo.util.enums.TokenType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private TokenType tokenType;
}