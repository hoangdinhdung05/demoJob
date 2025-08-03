package com.demoJob.demo.service;

import com.demoJob.demo.dto.UserDTO;
import com.demoJob.demo.dto.request.Admin.ResetPasswordRequest;
import com.demoJob.demo.dto.request.LoginRequest;
import com.demoJob.demo.dto.request.Admin.RefreshTokenRequest;
import com.demoJob.demo.dto.request.RegisterRequest;
import com.demoJob.demo.dto.request.SendOtpRequest;
import com.demoJob.demo.dto.response.AuthResponse;
import com.demoJob.demo.dto.response.TokenRefreshResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    //=======//LOGIN//=======//
    AuthResponse authenticateUser(LoginRequest request);

    //=======//REGISTER//=======//
    UserDTO register(RegisterRequest request);

    //=======//REFRESH TOKEN//=======//
    TokenRefreshResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    //=======//LOGOUT//=======//
    String logout(HttpServletRequest request);

    //=======//VERIFY EMAIL//=======//
    String verifyEmail(String email, String code);

    //=======//FORGOT PASSWORD//=======//
    String resetPassword(ResetPasswordRequest request);

    //=======//RESEND OTP//=======//
    void resendOtp(SendOtpRequest request);

}
