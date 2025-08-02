package com.demoJob.demo.service;

import com.demoJob.demo.dto.UserDTO;
import com.demoJob.demo.dto.request.LoginEmailRequest;
import com.demoJob.demo.dto.request.LoginRequest;
import com.demoJob.demo.dto.request.Admin.RefreshTokenRequest;
import com.demoJob.demo.dto.request.RegisterRequest;
import com.demoJob.demo.dto.response.AuthResponse;
import com.demoJob.demo.dto.response.TokenRefreshResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    AuthResponse authenticateUser(LoginRequest request);

    UserDTO register(RegisterRequest request);

    TokenRefreshResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    String logout(HttpServletRequest request);

//    void loginWithOtp(LoginEmailRequest request);

    String verifyEmail(String email, String code);

    void resendVerificationOtp(String email);

}
