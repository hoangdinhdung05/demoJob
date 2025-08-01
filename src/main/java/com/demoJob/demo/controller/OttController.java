package com.demoJob.demo.controller;

import com.demoJob.demo.dto.request.ForgotPasswordRequest;
import com.demoJob.demo.dto.request.ResetPasswordRequest;
import com.demoJob.demo.dto.request.VerifyEmailRequest;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.entity.VerificationToken;
import com.demoJob.demo.repository.UserRepository;
import com.demoJob.demo.service.MailService;
import com.demoJob.demo.service.VerificationTokenService;
import com.demoJob.demo.util.TokenTypeVerify;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ott")
@RequiredArgsConstructor
public class OttController {

    private final UserRepository userRepo;
    private final VerificationTokenService tokenService;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        User user = userRepo.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        VerificationToken token = tokenService.createToken(user, TokenTypeVerify.RESET_PASSWORD, 15);
        mailService.sendResetPasswordMail(user, token.getToken());

        return ResponseEntity.ok("Đã gửi email khôi phục mật khẩu.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        VerificationToken token = tokenService.validateToken(request.token(), TokenTypeVerify.RESET_PASSWORD);
        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepo.save(user);

        return ResponseEntity.ok("Mật khẩu đã được cập nhật.");
    }

    @PostMapping("/resend-verification/{email}")
    public ResponseEntity<?> resendVerification(@PathVariable String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        VerificationToken token = tokenService.createToken(user, TokenTypeVerify.VERIFY_EMAIL, 60);
        mailService.sendVerificationEmail(user, token.getToken());

        return ResponseEntity.ok("Đã gửi lại email xác minh.");
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody VerifyEmailRequest request) {
        VerificationToken token = tokenService.validateToken(request.token(), TokenTypeVerify.VERIFY_EMAIL);
        User user = token.getUser();
        user.setEmailVerified(true); // đảm bảo User có field này
        userRepo.save(user);

        return ResponseEntity.ok("Email đã được xác minh.");
    }
}
