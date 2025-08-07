package com.demoJob.demo.controller;

import com.demoJob.demo.dto.request.SendOtpRequest;
import com.demoJob.demo.dto.response.system.ResponseData;
import com.demoJob.demo.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
@Slf4j
@Validated
public class OtpController {

    private final OtpService otpService;

    /**
     * Gửi OTP đến email của người dùng.
     * Kiểm tra xem email có tồn tại trong hệ thống hay không.
     * Nếu tồn tại, gửi OTP và trả về thông báo thành công.
     *
     * @param request Thông tin yêu cầu gửi OTP
     * @return ResponseEntity chứa mã trạng thái và thông báo
     */
    @PostMapping("/resend")
    public ResponseEntity<ResponseData<Void>> sendOtp(@RequestBody @Valid SendOtpRequest request) {
        log.info("[OTP] Sending OTP to email: {} - type: {}", request.getEmail(), request.getType());
        otpService.sendOtp(request);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "OTP đã được gửi"));
    }
}

