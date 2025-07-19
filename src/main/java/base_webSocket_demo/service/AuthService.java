package base_webSocket_demo.service;

import base_webSocket_demo.dto.UserDTO;
import base_webSocket_demo.dto.request.LoginRequest;
import base_webSocket_demo.dto.request.Admin.RefreshTokenRequest;
import base_webSocket_demo.dto.request.RegisterRequest;
import base_webSocket_demo.dto.response.AuthResponse;
import base_webSocket_demo.dto.response.TokenRefreshResponse;

public interface AuthService {
    AuthResponse authenticateUser(LoginRequest request);

    UserDTO register(RegisterRequest request);

    TokenRefreshResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    String logout(String token);
}
