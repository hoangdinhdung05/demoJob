package com.demoJob.demo.service;

import com.demoJob.demo.dto.request.ResendOtpRequest;
import com.demoJob.demo.dto.request.SendOtpRequest;
import com.demoJob.demo.dto.request.VerifyOtpRequest;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.util.enums.OtpType;

public interface OtpService {

    /**
     * Gửi OTP đến người dùng
     *
     * @param request Thông tin yêu cầu gửi OTP
     * @param type   loại OTP (LOGIN, TWO_FA, RESET_PASSWORD, VERIFY_EMAIL)
     */
    void sendOtp(SendOtpRequest request, OtpType type);

    /**
     * Gửi OTP đến người dùng
     *
     * @param request Thông tin yêu cầu gửi OTP
     */
    void resendOtp(ResendOtpRequest request);

    /**
     * verify key
     * @param request email và otp
     * @return mã verifyKey
     */
    String verifyOtp(VerifyOtpRequest request);

    /**
     * email và otp
     * @param request email và otp
     */
    void verifyEmail(VerifyOtpRequest request);

    /**
     * Xác minh verifyKey để lấy thông tin người dùng
     * @param verifyKey mã xác minh được gửi qua OTP
     * @return User nếu xác minh thành công
     */
    User confirmVerifyKey(String verifyKey);
}

