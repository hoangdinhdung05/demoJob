package com.demoJob.demo.service;

import com.demoJob.demo.dto.request.SendOtpRequest;
import com.demoJob.demo.dto.request.VerifyOtpRequest;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.util.OtpType;

public interface OtpService {

    /**
     * Gửi OTP đến người dùng
     * @param request Thông tin yêu cầu gửi OTP
     */
    void sendOtp(SendOtpRequest request);

    /**
     * verify key
     * @param request
     * @return
     */
    String verifyOtp(VerifyOtpRequest request);

    /**
     * email và otp
     * @param request
     */
    void verifyEmail(VerifyOtpRequest request);

    /**
     * Xác minh verifyKey để lấy thông tin người dùng
     * @param verifyKey mã xác minh được gửi qua OTP
     * @param type loại OTP (ví dụ: EMAIL, PHONE)
     * @return User nếu xác minh thành công
     */
    User confirmVerifyKey(String verifyKey, OtpType type);
}

