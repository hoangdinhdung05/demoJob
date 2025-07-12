package base_webSocket_demo.service;

import base_webSocket_demo.dto.request.LoginRequest;
import base_webSocket_demo.dto.request.RefreshTokenRequest;
import base_webSocket_demo.dto.response.AuthResponse;
import base_webSocket_demo.dto.response.TokenRefreshResponse;

public interface AuthService {
    AuthResponse authenticateUser(LoginRequest request);

    TokenRefreshResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    String logout(String token);
}
