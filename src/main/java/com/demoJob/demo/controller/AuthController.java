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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * Đăng nhập người dùng.
     * @param request Thông tin đăng nhập bao gồm username và password.
     * @return ResponseEntity chứa mã trạng thái và thông tin đăng nhập thành công.
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseData<AuthResponse>> login(@RequestBody @Valid LoginRequest request) {
        log.info("[AUTH] Login request for username: {}", request.getUsername());
        AuthResponse response = authService.authenticateUser(request);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "Đăng nhập thành công", response));
    }

    /**
     * Đăng ký người dùng mới.
     * @param request Thông tin đăng ký bao gồm email, password và các thông tin khác.
     * @return ResponseEntity chứa mã trạng thái và thông tin người dùng đã đăng ký.
     */
    @PostMapping("/register")
    public ResponseEntity<ResponseData<UserDTO>> register(@RequestBody @Valid RegisterRequest request) {
        log.info("[AUTH] Register request for email: {}", request.getEmail());
        UserDTO user = authService.register(request);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "Đăng ký thành công, vui lòng xác minh email", user));
    }

    /**
     * Xác minh email người dùng.
     * @param request Thông tin xác minh bao gồm verifyKey và email.
     * @return ResponseEntity chứa mã trạng thái và thông báo xác minh thành công.
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseData<TokenRefreshResponse>> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        log.info("[TOKEN] Refreshing token");
        TokenRefreshResponse token = authService.refreshToken(request);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "Làm mới token thành công", token));
    }

    /**
     * Reset mật khẩu người dùng.
     * @param request Thông tin đặt lại mật khẩu bao gồm verifyKey và mật khẩu mới.
     * @return ResponseEntity chứa mã trạng thái và thông báo đặt lại mật khẩu thành công.
     */
    @PostMapping("/password/reset")
    public ResponseEntity<ResponseData<String>> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        log.info("[AUTH] Reset password request for verifyKey: {}", request.getVerifyKey());
        String result = authService.resetPassword(request);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "Đặt lại mật khẩu thành công", result));
    }

    /**
     * Xác minh email người dùng.
     * @param request Thông tin xác minh bao gồm verifyKey và email.
     * @return ResponseEntity chứa mã trạng thái và thông báo xác minh thành công.
     */
    @PostMapping("/logout")
    public ResponseEntity<ResponseData<String>> logout(HttpServletRequest request) {
        String username = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "Unknown";
        log.info("[AUTH] Logout request for user: {}", username);
        String result = authService.logout(request);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), result));
    }
}
