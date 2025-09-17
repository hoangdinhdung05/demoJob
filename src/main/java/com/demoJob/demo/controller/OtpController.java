package com.demoJob.demo.controller;

import com.demoJob.demo.dto.request.SendOtpRequest;
import com.demoJob.demo.dto.response.system.ResponseData;
import com.demoJob.demo.service.OtpService;
import com.demoJob.demo.util.enums.OtpType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "OTP", description = "Quản lý OTP")
public class OtpController {

    private final OtpService otpService;

    /**
     * Gửi OTP đến email của người dùng.
     * Kiểm tra xem email có tồn tại trong hệ thống hay không.
     * Nếu tồn tại, gửi OTP và trả về thông báo thành công.
     * @param request Thông tin yêu cầu gửi OTP
     * @return ResponseEntity chứa mã trạng thái và thông báo
     */
    @Operation(summary = "Gửi OTP đến email", description = "Gửi OTP đến email của người dùng")
    @PostMapping("/resend")
    public ResponseEntity<ResponseData<Void>> sendOtp(@RequestBody @Valid SendOtpRequest request, @RequestParam OtpType type) {
        log.info("[OTP] Sending OTP to email, type: {} {}", request.getEmail(), type);
        otpService.sendOtp(request, type);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "OTP đã được gửi"));
    }
}

