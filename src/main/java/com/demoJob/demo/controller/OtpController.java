package com.demoJob.demo.controller;

import com.demoJob.demo.dto.request.SendOtpRequest;
import com.demoJob.demo.dto.request.VerifyOtpRequest;
import com.demoJob.demo.dto.response.system.ResponseData;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.service.OtpService;
import com.demoJob.demo.util.OtpType;
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

    /**
     * Xác minh OTP được gửi đến email của người dùng.
     * Kiểm tra xem OTP có hợp lệ hay không.
     * Nếu hợp lệ, trả về verifyKey để xác minh tiếp.
     *
     * @param request Thông tin yêu cầu xác minh OTP
     * @return ResponseEntity chứa mã trạng thái và verifyKey
     */
    @PostMapping("/verify")
    public ResponseEntity<ResponseData<String>> verifyOtp(@RequestBody @Valid VerifyOtpRequest request) {
        log.info("[OTP] Verifying OTP for email: {} - type: {}", request.getEmail(), request.getType());
        String verifyKey = otpService.verifyOtp(request);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "Xác minh OTP thành công", verifyKey));
    }

    /**
     * Xác minh verifyKey để hoàn tất quá trình xác minh.
     * Nếu verifyKey hợp lệ, trả về thông báo xác minh thành công.
     *
     * @param key Mã xác minh được gửi qua OTP
     * @param type Loại OTP (EMAIL, PHONE, etc.)
     * @return ResponseEntity chứa mã trạng thái và thông báo
     */
    //Cái này chắc là mình xóa đi thôi anh nhỉ
    //Trước tạo ra để xác minh email, giờ mình có 1 api active bên Auth rồi
    @GetMapping("/confirm")
    public ResponseEntity<ResponseData<Void>> confirmVerifyKey(@RequestParam String key, @RequestParam OtpType type) {
        User user = otpService.confirmVerifyKey(key, type);
        log.info("[OTP] Confirmed verify key for user: {}", user.getEmail());
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "Xác minh thành công"));
    }

}

