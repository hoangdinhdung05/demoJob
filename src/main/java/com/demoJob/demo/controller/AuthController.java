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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    /**
     * Đăng nhập người dùng
     * Trả về access token, refresh token và thông tin user
     * @param request chứa thông tin đăng nhập (username, password)
     * @return trả về thông tin xác thực người dùng
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        log.info("[AUTH] Login request for username: {}", request.getUsername());

        AuthResponse response = authService.authenticateUser(request);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "Đăng nhập thành công", response));
    }

    /**
     * Đăng ký người dùng
     * Tạo tài khoản và gửi OTP xác minh email
     * @param request chứa thông tin đăng ký
     * @return trả về thông tin người dùng đã đăng ký
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        log.info("[AUTH] Register request for email: {}", request.getEmail());
        UserDTO user = authService.register(request);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "Đăng ký thành công, vui lòng xác minh email", user));
    }

    /**
     * Resend OTP
     * @param request chứa email và loại OTP (REGISTER, FORGOT_PASSWORD)
     * @return trả về thông báo gửi lại OTP thành công
     */
    @PostMapping("/resend-otp")
    public ResponseEntity<ResponseData<Void>> resendOtp(@RequestBody @Valid SendOtpRequest request) {
        log.info("[OTP] Resending OTP for email: {}, type: {}", request.getEmail(), request.getType());
        authService.resendOtp(request);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "OTP đã được gửi lại"));
    }

    /**
     * Verify OTP
     * @param request chứa email và mã OTP
     * @return trả về thông báo xác minh thành công hoặc thất bại
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<ResponseData<Void>> verifyOtp(@RequestBody @Valid VerifyOtpRequest request) {
        log.info("[OTP] Verifying OTP for email: {}, type: {}", request.getEmail(), request.getType());
        authService.verifyOtpOrThrow(request);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "Xác minh OTP thành công"));
    }

    /**
     * Verify email bằng OTP code
     * @param email người dùng cần xác minh email
     * @param code mã OTP được gửi đến email
     * @return trả về thông báo xác minh thành công hoặc thất bại
     */
    @PostMapping("/verify-email")
    public ResponseEntity<ResponseData<String>> verifyEmailOtp(@RequestParam String email,
                                                               @RequestParam String code) {
        log.info("[VERIFY] Verifying email: {} with code: {}", email, code);
        String result = authService.verifyEmail(email, code);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), result));
    }

    /**
     * Làm mới access token bằng refresh token
     * @param request gửi lên refresh token
     * @return trả về access token và refresh token mới
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseData<TokenRefreshResponse>> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        log.info("[TOKEN] Refreshing token for refreshToken={}", request.getRefreshToken());
        TokenRefreshResponse token = authService.refreshToken(request);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "Làm mới access token thành công", token));
    }

    /**
     * Đăng xuất người dùng
     * Xóa refresh token và blacklist access token
     * @param request lấy thông tin người dùng từ request
     * @return trả về thông báo đăng xuất thành công
     */
    @PostMapping("/logout")
    public ResponseEntity<ResponseData<String>> logout(HttpServletRequest request) {
        String username = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "Unknown";
        log.info("[AUTH] Logout request for user: {}", username);
        String result = authService.logout(request);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), result));
    }

    /**
     * Xác minh OTP khôi phục mật khẩu
     */
    @PostMapping("/forgot-password/reset")
    public ResponseEntity<ResponseData<String>> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        log.info("[AUTH] Resetting password for email: {}", request.getEmail());
        String result = authService.resetPassword(request);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), result));
    }
}
