package com.demoJob.demo.controller;

import com.demoJob.demo.dto.request.SendOtpRequest;
import com.demoJob.demo.dto.response.VerifyOtpRequest;
import com.demoJob.demo.service.OtpService;
import com.demoJob.demo.util.OtpType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;


    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestParam String email, @RequestParam OtpType type) {
        otpService.sendOtp(email, type); // check user từ email
        return ResponseEntity.ok("OTP mới đã được gửi");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request) {
        boolean valid = otpService.verifyOtp(request);
        return ResponseEntity.ok(valid ? "OTP hợp lệ" : "OTP sai");
    }
}
