package com.demoJob.demo.controller;

import com.demoJob.demo.dto.request.*;
import com.demoJob.demo.dto.request.Admin.ResetPasswordRequest;
import com.demoJob.demo.dto.request.User.Client.ChangePasswordRequest;
import com.demoJob.demo.dto.response.AuthResponse;
import com.demoJob.demo.dto.response.TokenRefreshResponse;
import com.demoJob.demo.dto.response.system.ResponseData;
import com.demoJob.demo.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import static com.demoJob.demo.util.containts.AuthMessage.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "AUTHENTICATION", description = "API xác thực và quản lý người dùng")
public class AuthController {

    private final AuthService authService;

    /**
     * Đăng nhập người dùng.
     * @param request Thông tin đăng nhập bao gồm username và password.
     * @return ResponseEntity chứa mã trạng thái và thông tin đăng nhập thành công.
     */
    @Operation(summary = "Đăng nhập người dùng", description = "Đăng nhập người dùng với username và password, trả về JWT token nếu thành công.")
    @PostMapping("/login")
    public ResponseEntity<ResponseData<AuthResponse>> login(@RequestBody @Valid LoginRequest request) {
        log.info("[AUTH] Login request for username: {}", request.getUsername());
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                LOGIN_SUCCESS,
                authService.authenticateUser(request)));
    }

    /**
     * Đăng ký người dùng mới.
     * @param request Thông tin đăng ký bao gồm email, password và các thông tin khác.
     * @return ResponseEntity chứa mã trạng thái và thông tin người dùng đã đăng ký.
     */
    @Operation(summary = "Đăng ký người dùng", description = "Đăng ký người dùng mới với email, password và các thông tin khác.")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        log.info("[AUTH] Register request for email: {}", request.getEmail());
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                authService.register(request)));
    }

    /**
     * Xác minh email người dùng.
     * @param request Thông tin xác minh bao gồm verifyKey và email.
     * @return ResponseEntity chứa mã trạng thái và thông báo xác minh thành công.
     */
    @Operation(summary = "Làm mới token", description = "Làm mới JWT token sử dụng refresh token.")
    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseData<TokenRefreshResponse>> refreshToken(HttpServletRequest request) {
        log.info("[TOKEN] Refreshing token");
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                REFRESH_TOKEN_SUCCESS,
                authService.refreshToken(request)));
    }

    /**
     * Reset mật khẩu người dùng.
     * @param request Thông tin đặt lại mật khẩu bao gồm verifyKey và mật khẩu mới.
     * @return ResponseEntity chứa mã trạng thái và thông báo đặt lại mật khẩu thành công.
     */
    @Operation(summary = "Đặt lại mật khẩu", description = "Đặt lại mật khẩu người dùng sử dụng verifyKey và mật khẩu mới.")
    @PostMapping("/reset-password")
    public ResponseEntity<ResponseData<String>> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        log.info("[AUTH] Reset password request for verifyKey: {}", request.getVerifyKey());
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                authService.resetPassword(request)));
    }

    /**
     * Gửi OTP đến email người dùng để xác minh hoặc đặt lại mật khẩu.
     * @param request Thông tin gửi OTP bao gồm email và loại OTP.
     * @return ResponseEntity chứa mã trạng thái và thông báo gửi OTP thành công.
     */
    @Operation(summary = "Gửi OTP", description = "Gửi mã OTP đến email người dùng để xác minh đặt lại mật khẩu.")
    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseData<String>> forgotPassword(@RequestBody @Valid SendOtpRequest request) {
        log.info("[AUTH] Sending OTP to email: {} - type: {}", request.getEmail(), request.getType());
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                authService.forgotPassword(request)));
    }

    /**
     * Xác minh email người dùng.
     * @param request Thông tin xác minh bao gồm verifyKey và email.
     * @return ResponseEntity chứa mã trạng thái và thông báo xác minh thành công.
     */
    @Operation(summary = "Đăng xuất người dùng", description = "Đăng xuất người dùng hiện tại, vô hiệu hóa token.")
    @PostMapping("/logout")
    public ResponseEntity<ResponseData<String>> logout(HttpServletRequest request) {
        String username = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "Unknown";
        log.info("[AUTH] Logout request for user: {}", username);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), authService.logout(request)));
    }

    /**
     * Xác minh mã OTP được gửi đến email người dùng.
     * Kiểm tra xem mã OTP có hợp lệ hay không.
     * Hợp lệ sẽ đánh dấu email là đã xác minh.
     *
     * @param request Thông tin xác minh bao gồm email và mã OTP.
     * @return ResponseEntity chứa mã trạng thái và thông báo xác minh thành công.
     */
    @Operation(summary = "Xác minh email", description = "Xác minh email người dùng sử dụng mã OTP đã gửi đến email.")
    @PostMapping("/active")
    public ResponseEntity<?> verifyEmail(@RequestBody @Valid VerifyOtpRequest request) {
        log.info("[AUTH] Verifying email for: {}", request.getEmail());
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                authService.active(request)));
    }

    /**
     * Xác minh mã OTP được gửi đến email người dùng để đặt lại mật khẩu.
     * Kiểm tra xem mã OTP có hợp lệ hay không.
     * Hợp lệ sẽ cho phép người dùng đặt lại mật khẩu.
     *
     * @param request Thông tin xác minh bao gồm email và mã OTP.
     * @return ResponseEntity chứa mã trạng thái và thông báo xác minh thành công.
     */
    @Operation(summary = "Xác minh OTP đặt lại mật khẩu", description = "Xác minh mã OTP đã gửi đến email để đặt lại mật khẩu.")
    @PostMapping("/reset-password/otp/verify")
    public ResponseEntity<ResponseData<String>> verifyResetPassword(@RequestBody @Valid VerifyOtpRequest request) {
        log.info("[AUTH] Verifying OTP for reset password for email: {}", request.getEmail());
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                VERIFY_OTP_SUCCESS,
                authService.verifyResetPassword(request)));
    }

    /**
     * Thay đổi mật khẩu của người dùng hiện tại.
     * @param request Thông tin thay đổi mật khẩu bao gồm mật khẩu hiện tại, mật khẩu mới và xác nhận mật khẩu mới.
     * @return ResponseEntity chứa mã trạng thái và thông báo thay đổi mật khẩu thành công.
     */
    @Operation(summary = "Thay đổi mật khẩu", description = "Thay đổi mật khẩu của người dùng hiện tại.")
    @PatchMapping("/password")
    public ResponseEntity<?> changeMyPassword(@RequestBody @Valid ChangePasswordRequest request) {
        log.info("Changing user password: {}", request);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(),
                authService.changeMyPassword(request)));
    }
}
