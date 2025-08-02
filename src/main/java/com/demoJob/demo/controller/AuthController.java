package com.demoJob.demo.controller;

import com.demoJob.demo.dto.UserDTO;
import com.demoJob.demo.dto.request.*;
import com.demoJob.demo.dto.request.Admin.RefreshTokenRequest;
import com.demoJob.demo.dto.request.Admin.ResetPasswordRequest;
import com.demoJob.demo.dto.response.AuthResponse;
import com.demoJob.demo.dto.response.TokenRefreshResponse;
import com.demoJob.demo.dto.response.system.ResponseData;
import com.demoJob.demo.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
        * Đăng nhập với username và password
        * Trả về access token và refresh token
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.authenticateUser(request);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "Đăng nhập thành công", response));
    }

    /**
        * Đăng ký tài khoản mới
        * Trả về thông tin user và yêu cầu xác minh email
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        UserDTO user = authService.register(request);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "Đăng ký thành công, vui lòng xác minh email", user));
    }

    /**
        * Gửi OTP xác minh email trường hợp OTP đã hết hạn
     */
    @PostMapping("/verify-email/resend")
    public ResponseEntity<ResponseData<Void>> resendVerifyOtp(@RequestParam String email) {
        authService.resendVerificationOtp(email);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "OTP xác minh đã được gửi lại"));
    }

    /**
        * Xác minh email bằng OTP đã gửi
        * Trả về thông báo thành công hoặc lỗi
     */
    @PostMapping("/verify-email")
    public ResponseEntity<ResponseData<String>> verifyEmailOtp(@RequestParam String email,
                                                               @RequestParam String code) {
        String result = authService.verifyEmail(email, code);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), result));
    }

    /**
        * Làm mới access token bằng refresh token
        * Trả về access token mới
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseData<TokenRefreshResponse>> refreshToken(@RequestBody RefreshTokenRequest request) {
        TokenRefreshResponse token = authService.refreshToken(request);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "Làm mới access token thành công", token));
    }

    /**
        * Đăng xuất người dùng
        * Trả về thông báo thành công
        * Lưu access token vào blacklist
        * Đánh dấu refresh token là đã sử dụng
     */
    @PostMapping("/logout")
    public ResponseEntity<ResponseData<String>> logout(HttpServletRequest request) {
        String result = authService.logout(request);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), result));
    }

    // FORGOT PASSWORD

    /**
     * Gửi OTP khôi phục mật khẩu
     */
    @PostMapping("/forgot-password/send-otp")
    public ResponseEntity<ResponseData<Void>> sendForgotPasswordOtp(@RequestParam String email) {
        authService.resendResetPasswordOtp(email);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "Đã gửi mã OTP khôi phục mật khẩu"));
    }

    /**
     * Đặt lại mật khẩu sau khi xác minh OTP
     */
    @PostMapping("/forgot-password/reset")
    public ResponseEntity<ResponseData<String>> resetPassword(@RequestBody ResetPasswordRequest request) {
        String result = authService.resetPassword(request);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), result));
    }
}
