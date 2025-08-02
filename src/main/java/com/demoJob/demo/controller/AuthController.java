package com.demoJob.demo.controller;

import com.demoJob.demo.dto.request.Admin.RefreshTokenRequest;
import com.demoJob.demo.dto.request.LoginEmailRequest;
import com.demoJob.demo.dto.request.LoginRequest;
import com.demoJob.demo.dto.request.RegisterRequest;
import com.demoJob.demo.dto.response.AuthResponse;
import com.demoJob.demo.dto.response.TokenRefreshResponse;
import com.demoJob.demo.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.authenticateUser(request));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/resend-verify-email")
    public ResponseEntity<String> resendVerifyOtp(@RequestParam String email) {
        authService.resendVerificationOtp(email);
        return ResponseEntity.ok("OTP xác minh đã được gửi lại");
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyOtp(@RequestParam String email,
                                                  @RequestParam String code) {
        return ResponseEntity.ok(authService.verifyEmail(email, code));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String result = authService.logout(request);
        return ResponseEntity.ok(result);
    }
}
