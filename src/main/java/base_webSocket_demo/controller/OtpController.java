package base_webSocket_demo.controller;

import base_webSocket_demo.dto.request.SendOtpRequest;
import base_webSocket_demo.dto.response.VerifyOtpRequest;
import base_webSocket_demo.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/send")
    public ResponseEntity<?> sendOtp(@RequestBody SendOtpRequest request) {
        otpService.sendOtp(request);
        return ResponseEntity.ok("OTP sent");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request) {
        boolean valid = otpService.verifyOtp(request);
        return ResponseEntity.ok(valid ? "OTP hợp lệ" : "OTP sai");
    }
}
