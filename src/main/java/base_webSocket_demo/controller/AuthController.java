package base_webSocket_demo.controller;

import base_webSocket_demo.dto.request.LoginEmailRequest;
import base_webSocket_demo.dto.request.LoginRequest;
import base_webSocket_demo.dto.request.RegisterRequest;
import base_webSocket_demo.dto.response.AuthResponse;
import base_webSocket_demo.service.AuthService;
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

    @PostMapping("/login-otp")
    public ResponseEntity<?> loginWithOtp(@RequestBody LoginEmailRequest request) {
        authService.loginWithOtp(request);
        return ResponseEntity.ok("OTP đã được gửi đến email của bạn.");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(@RequestParam String email,
                                                  @RequestParam String code) {
        AuthResponse response = authService.verifyOtpAndLogin(email, code);
        return ResponseEntity.ok(response);
    }


}