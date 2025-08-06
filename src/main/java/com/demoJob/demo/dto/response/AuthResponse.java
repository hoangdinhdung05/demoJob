package com.demoJob.demo.dto.response;

import com.demoJob.demo.util.TokenType;
import lombok.Builder;
import lombok.Data;
import java.util.Set;

@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private TokenType tokenType;
}